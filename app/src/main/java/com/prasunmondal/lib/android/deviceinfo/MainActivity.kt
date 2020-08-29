package com.prasunmondal.lib.android.deviceinfo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.prasunmondal.lib.android.deviceinfo.libdev.DeviceInfo
import com.prasunmondal.lib.android.deviceinfo.libdev.InstalledApps

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        println(" = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = = ")
        DeviceInfo.setContext(applicationContext, contentResolver)
        println(DeviceInfo.getAllInfo())

        findViewById<TextView>(R.id.textview1).text =
            DeviceInfo.getAllInfo() + "\n\n\n" +
                    DeviceInfo.get(InstalledApps.ALL_APPS_LIST) + "\n\n\n" +
                    DeviceInfo.get(InstalledApps.ALL_APPS_COUNT) + "\n\n\n" +
                    DeviceInfo.get(InstalledApps.USER_APPS_LIST) + "\n\n\n" +
                    DeviceInfo.get(InstalledApps.USER_APPS_COUNT) + "\n\n\n" +
                    DeviceInfo.get(InstalledApps.SYSTEM_APPS_LIST) + "\n\n\n" +
                    DeviceInfo.get(InstalledApps.SYSTEM_APPS_COUNT) + "\n\n\n"

    }
}
