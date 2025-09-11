package com.qiuqiuqiu.weatherPredicate
import android.app.Application
import com.baidu.location.LocationClient
import com.baidu.mapapi.SDKInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocationClient.setAgreePrivacy(true)  // ✅ 声明同意定位 SDK 的隐私政策

        // 必须在 SDK 初始化前设置用户同意隐私协议
        SDKInitializer.setAgreePrivacy(applicationContext, true)

        // 初始化百度地图 SDK
        SDKInitializer.initialize(applicationContext)
    }
}