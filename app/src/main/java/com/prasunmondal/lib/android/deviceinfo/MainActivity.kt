package com.prasunmondal.lib.android.deviceinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(" = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ")
        DeviceInfo_java.setContext(applicationContext, contentResolver)
        println(DeviceInfo_java.get(Device.DEVICE_MAC_ADDRESS))
        println(DeviceInfo_java.get(Device.DEVICE_UNIQUE_ID))
        println(DeviceInfo_java.checkNetworkStatus(applicationContext))
        println(DeviceInfo_java.getNetworkType(applicationContext))
        println(DeviceInfo_java.getDeviceMoreThan5Inch(applicationContext))
        println(DeviceInfo_java.getDataType(applicationContext))
        println(DeviceInfo_java.getDeviceId(applicationContext))
        println(DeviceInfo_java.getDeviceInch(applicationContext))
    }
}
