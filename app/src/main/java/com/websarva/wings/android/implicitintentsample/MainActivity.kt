package com.websarva.wings.android.implicitintentsample

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {
    // 緯度プロパティ
    private var _latitude = 0.0
    // 経度プロパティ
    private var _longitude = 0.0
    // FusedLocationProviderClientオブジェクトプロパティ
    private lateinit var _fusedLocationClient: FusedLocationProviderClient
    // LocationRequestオブジェクトプロパティ
    private lateinit var _locationRequest: LocationRequest
    // 位置情報が変更された時の処理を行うコールバックオブジェクト
    private lateinit var _onUpdateLocation: OnUpdateLocation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FusedLocationProviderClientオブジェクトを取得
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        // LocationRequestオブジェクトを生成
        _locationRequest = LocationRequest.create()
        // LocationRequestオブジェクトがnullでない場合
        _locationRequest?.let {
            // 位置情報の更新間隔を設定
            it.interval = 5000
            // 位置情報の最短更新間隔を設定
            it.fastestInterval = 1000
            // 位置情報の取得精度を設定
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        // 位置情報が変更された時の処理を行うコールバックオブジェクトを生成
        _onUpdateLocation = OnUpdateLocation()
    }

    override fun onResume() {
        super.onResume()
        // 位置情報の追跡を開始
        if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
                // ACCESS_FINE_LOCATIONの許可を求めるダイアログを表示 その際、リクエストコードを1000に設定
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this@MainActivity, permissions, 1000)
                // onResumeメソッドを終了
                return
        }
        _fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, mainLooper)
    }

    override fun onPause() {
        super.onPause()

        // 位置情報の追跡を停止
        _fusedLocationClient.removeLocationUpdates(_onUpdateLocation)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        // ACCESS_FINE_LOCATIONに対するパーミッションダイアログ、かつ許可を選択した場合
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 再度ACCESS_FINE_LOCATIONの許可が降りていないかどうかのチェック 降りていなければ処理を中止
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                return
            }
            // 位置情報の追跡を開始
            _fusedLocationClient.requestLocationUpdates(_locationRequest, _onUpdateLocation, mainLooper)
        }
    }

    private inner class OnUpdateLocation: LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult?.let {
                // 直近の位置情報を取得
                val location = it.lastLocation
                location?.let {
                    // locationオブジェクトから緯度を取得
                    _latitude = it.latitude
                    // locationオブジェクトから経度を取得
                    _longitude = it.longitude
                    // 取得した緯度をTextViewに表示
                    val tvLatitude = findViewById<TextView>(R.id.tvLatitude)
                    tvLatitude.text = _latitude.toString()
                    // 取得した経度をTextViewに表示
                    val tvLongitude = findViewById<TextView>(R.id.tvLongitude)
                    tvLongitude.text = _longitude.toString()
                }
            }
        }
    }


    fun onMapSearchButtonClick(view: View) {
        // 入力欄に入力されたキーワード文字列を取得
        val etSearchWord = findViewById<EditText>(R.id.etSearchWord)
        var searchWord = etSearchWord.text.toString()
        // 入力されたキーワードをURLエンコード
        searchWord = URLEncoder.encode(searchWord, "UTF-8")
        // マップアプリと連携するURI文字列を生成
        val uriStr = "geo:0,0?q=${searchWord}"
        // URI文字列からURIからURIオブジェクトを生成
        val uri = Uri.parse(uriStr)
        // Intentオブジェクトを生成
        val intent = Intent(Intent.ACTION_VIEW, uri)
        // アクティビティを起動
        startActivity(intent)
    }

    fun onMapShowCurrentButtonClick(view: View) {
        // プロパティの緯度と経度の値を元にマップアプリと連携するURI文字列を生成
        val uriStr = "geo:${_latitude},${_longitude}"
        // URI文字列からURIからURIオブジェクトを生成
        val uri = Uri.parse(uriStr)
        // Intentオブジェクトを生成
        val intent = Intent(Intent.ACTION_VIEW, uri)
        // アクティビティを起動
        startActivity(intent)
    }
}