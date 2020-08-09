package com.prasunmondal.lib.android.deviceinfo

import android.content.ContentResolver
import android.content.Context
import android.net.wifi.WifiManager
import android.provider.Settings
import java.util.*

class DeviceInfo_kotlin {

    protected fun generateDeviceId(context: Context, contentResolver: ContentResolver): String {
        val macAddr: String
        val wifiMan =
            context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInf = wifiMan.connectionInfo
        macAddr = wifiInf.macAddress
        val androidId: String = "" + Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val deviceUuid = UUID(androidId.hashCode().toLong(), macAddr.hashCode().toLong())
        return deviceUuid.toString()
    }
}