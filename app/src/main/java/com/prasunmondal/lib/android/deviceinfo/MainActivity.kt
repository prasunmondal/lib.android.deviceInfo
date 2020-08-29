package com.prasunmondal.lib.android.deviceinfo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.prasunmondal.lib.android.deviceinfo.libdev.DeviceInfo
import com.prasunmondal.lib.android.deviceinfo.libdev.InstalledApps

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(" = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ")
        DeviceInfo.setContext(applicationContext, contentResolver)
        println(DeviceInfo.getAllInfo())

        findViewById<TextView>(R.id.textview1).text = DeviceInfo.getAllInfo() + "\n\n\n" + DeviceInfo.get(InstalledApps.ALL_APP_LIST) + "\n\n\n" + DeviceInfo.get(InstalledApps.APP_COUNT);
    }
}
