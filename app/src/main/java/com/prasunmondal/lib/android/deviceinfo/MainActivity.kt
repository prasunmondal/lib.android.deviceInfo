package com.prasunmondal.lib.android.deviceinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(" = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ")
        DeviceInfo.setContext(applicationContext, contentResolver)
        println(DeviceInfo.get(Device.DEVICE_MAC_ADDRESS))
        println(DeviceInfo.get(Device.DEVICE_UNIQUE_ID))
    }
}
