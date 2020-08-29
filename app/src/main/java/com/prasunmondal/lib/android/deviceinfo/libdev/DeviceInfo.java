package com.prasunmondal.lib.android.deviceinfo.libdev;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class DeviceInfo {

    private static DeviceInfo singleton = null;
    private static Context activity;
    private static ContentResolver contentResolver;

    public static String getAllInfo() {
         Device[] device = Device.values();
         String result = "- - - Device Details - - -";
         for(int i=0; i<device.length; i++) {
             result += "\n";
             try {
                 result += device[i].name() + ": " + get(device[i]);
             } catch (Exception e) {
                 result += device[i].name() + ": ExceptionOccured";
             }
         }
         return result;
    }

    public static DeviceInfo setContext(Context activity, ContentResolver contentResolver) {
        if(singleton == null) {
            singleton = new DeviceInfo();
            singleton.activity = activity;
            singleton.contentResolver = contentResolver;
        }
        return singleton;
    }

    public static String get(InstalledApps type) throws Exception {
        final PackageManager pm = activity.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        switch (type) {
            case APP_COUNT:
                return String.valueOf(packages.size());
            case ALL_APP_LIST:
                Collections.sort(packages, new Comparator<ApplicationInfo>() {
                    public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                        return o1.packageName.compareTo(o2.packageName);
                    }
                });
                String result = "";
                boolean isFirst = true;
                for (ApplicationInfo packageInfo : packages) {
                    if (!isFirst) {
                        result += "\n";
                    } else {
                        isFirst = false;
                    }
                    result += packageInfo.packageName;
                }
                return result;
        }
        return "";
    }

    public static String get(Device device) throws Exception {
        if(activity == null) {
            Log.e("DeviceInfo::",
                    "Context not set for DeviceInfo class - use DeviceInfo.setContext(applicationContext, contentResolver) - it's an one time operation");
            throw new Exception("Context not set for DeviceInfo class - use DeviceInfo.setContext(applicationContext, contentResolver)");
        }
        try {
            switch (device) {
                case LANGUAGE:
                    return Locale.getDefault().getDisplayLanguage();
                case TIME_ZONE:
                    return TimeZone.getDefault().getID();//(false, TimeZone.SHORT);
                case LOCAL_COUNTRY_CODE:
                    return activity.getResources().getConfiguration().locale.getCountry();
                case CURRENT_YEAR:
                    return "" + (Calendar.getInstance().get(Calendar.YEAR));
                case CURRENT_DATE_TIME:
                    Calendar calendarTime = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
                    long time = (calendarTime.getTimeInMillis() / 1000);
                    return String.valueOf(time);
                //                    return DateFormat.getDateTimeInstance().format(new Date());
                case CURRENT_DATE_TIME_ZERO_GMT:
                    Calendar calendarTime_zero = Calendar.getInstance(TimeZone.getTimeZone("GMT+0"), Locale.getDefault());
                    return String.valueOf((calendarTime_zero.getTimeInMillis() / 1000));
//                                    DateFormat df = DateFormat.getDateTimeInstance();
//                                    df.setTimeZone(TimeZone.getTimeZone("GMT+0"));
//                                    return df.format(new Date());
                case HARDWARE_MODEL:
                    return getDeviceName();
                case NUMBER_OF_PROCESSORS:
                    return Runtime.getRuntime().availableProcessors() + "";
                case LOCALE:
                    return Locale.getDefault().getISO3Country();
                case IP_ADDRESS_IPV4:
                    return getIPAddress(true);
                case IP_ADDRESS_IPV6:
                    return getIPAddress(false);
                case MAC_ADDRESS:
                    String mac = getMACAddress("wlan0");
                    if (TextUtils.isEmpty(mac)) {
                        mac = getMACAddress("eth0");
                    }
                    if (TextUtils.isEmpty(mac)) {
                        mac = "DU:MM:YA:DD:RE:SS";
                    }
                    return mac;

                case TOTAL_MEMORY:
                    if (Build.VERSION.SDK_INT >= 16)
                        return String.valueOf(getTotalMemory());
                case FREE_MEMORY:
                    return String.valueOf(getFreeMemory());
                case USED_MEMORY:
                    if (Build.VERSION.SDK_INT >= 16) {
                        long freeMem = getTotalMemory() - getFreeMemory();
                        return String.valueOf(freeMem);
                    }
                    return "";
                case TOTAL_CPU_USAGE:
                    int[] cpu = getCpuUsageStatistic();
                    if (cpu != null) {
                        int total = cpu[0] + cpu[1] + cpu[2] + cpu[3];
                        return String.valueOf(total);
                    }
                    return "";
                case TOTAL_CPU_USAGE_SYSTEM:
                    int[] cpu_sys = getCpuUsageStatistic();
                    if (cpu_sys != null) {
                        int total = cpu_sys[1];
                        return String.valueOf(total);
                    }
                    return "";
                case TOTAL_CPU_USAGE_USER:
                    int[] cpu_usage = getCpuUsageStatistic();
                    if (cpu_usage != null) {
                        int total = cpu_usage[0];
                        return String.valueOf(total);
                    }
                    return "";
                case MANUFACTURE:
                    return Build.MANUFACTURER;
                case SYSTEM_VERSION:
                    return String.valueOf(getDeviceName());
                case VERSION:
                    return String.valueOf(Build.VERSION.SDK_INT);
                case IN_INCH:
                    return getDeviceInch();
                case TOTAL_CPU_IDLE:
                    int[] cpu_idle = getCpuUsageStatistic();
                    if (cpu_idle != null) {
                        int total = cpu_idle[2];
                        return String.valueOf(total);
                    }
                    return "";
                case NETWORK_TYPE:
                    return getNetworkType();
                case NETWORK:
                    return checkNetworkStatus();
                case TYPE:
                    if (isTablet()) {
                        if (getDeviceMoreThan5Inch()) {
                            return "Tablet";
                        } else
                            return "Mobile";
                    } else {
                        return "Mobile";
                    }
                case SYSTEM_NAME:
                    return "Android OS";

                case UNIQUE_ID:
                    return new DeviceInfo_kotlin().generateDeviceId(activity, contentResolver);

                default:
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private static long getTotalMemory() {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.totalMem / 1048576L; // in megabyte (mb)
            return availableMegs;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static long getFreeMemory() {
        try {
            ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
            ActivityManager activityManager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.getMemoryInfo(mi);
            long availableMegs = mi.availMem / 1048576L; // in megabyte (mb)

            return availableMegs;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    @SuppressLint("NewApi")
    private static String getMACAddress(String interfaceName) {
        try {

            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName))
                        continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null)
                    return "";
                StringBuilder buf = new StringBuilder();
                for (byte b : mac) buf.append(String.format("%02X:", b));
                if (buf.length() > 0)
                    buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
            return "";
        } // for now eat exceptions
        return "";
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @return address or empty string
     */
    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = Inet4Address.getByName(sAddr) != null;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port
                                // suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    /*
     *
     * @return integer Array with 4 elements: user, system, idle and other cpu
     * usage in percentage.
     */
    private static int[] getCpuUsageStatistic() {
        try {
            String tempString = executeTop();

            tempString = tempString.replaceAll(",", "");
            tempString = tempString.replaceAll("User", "");
            tempString = tempString.replaceAll("System", "");
            tempString = tempString.replaceAll("IOW", "");
            tempString = tempString.replaceAll("IRQ", "");
            tempString = tempString.replaceAll("%", "");
            for (int i = 0; i < 10; i++) {
                tempString = tempString.replaceAll("  ", " ");
            }
            tempString = tempString.trim();
            String[] myString = tempString.split(" ");
            int[] cpuUsageAsInt = new int[myString.length];
            for (int i = 0; i < myString.length; i++) {
                myString[i] = myString[i].trim();
                cpuUsageAsInt[i] = Integer.parseInt(myString[i]);
            }
            return cpuUsageAsInt;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("executeTop", "error in getting cpu statics");
            return null;
        }
    }

    private static String executeTop() {
        Process p = null;
        BufferedReader in = null;
        String returnString = null;
        try {
            p = Runtime.getRuntime().exec("top -n 1");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while (returnString == null || returnString.contentEquals("")) {
                returnString = in.readLine();
            }
        } catch (IOException e) {
            Log.e("executeTop", "error in getting first line of top");
            e.printStackTrace();
        } finally {
            try {
                assert in != null;
                in.close();
                assert p != null;
                p.destroy();
            } catch (IOException e) {
                Log.e("executeTop", "error in closing and destroying top process");
                e.printStackTrace();
            }
        }
        return returnString;
    }

    private static String getNetworkType() {
        String networkStatus = "";

        final ConnectivityManager connMgr = (ConnectivityManager)
                activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        // check for wifi
        final android.net.NetworkInfo wifi =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        // check for mobile data
        final android.net.NetworkInfo mobile =
                connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable()) {
            networkStatus = "Wifi";
        } else if (mobile.isAvailable()) {
            networkStatus = getDataType();
        } else {
            networkStatus = "noNetwork";
        }
        return networkStatus;
    }

    private static String checkNetworkStatus() {
        String networkStatus = "";
        try {
            // Get connect mangaer
            final ConnectivityManager connMgr = (ConnectivityManager)
                    activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            // // check for wifi
            final android.net.NetworkInfo wifi =
                    connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            // // check for mobile data
            final android.net.NetworkInfo mobile =
                    connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (wifi.isAvailable()) {
                networkStatus = "Wifi";
            } else if (mobile.isAvailable()) {
                networkStatus = getDataType();
            } else {
                networkStatus = "noNetwork";
                networkStatus = "0";
            }


        } catch (Exception e) {
            e.printStackTrace();
            networkStatus = "0";
        }
        return networkStatus;

    }

    private static boolean isTablet() {
        return (activity.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private static boolean getDeviceMoreThan5Inch() {
        try {
            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
            // int width = displayMetrics.widthPixels;
            // int height = displayMetrics.heightPixels;

            float yInches = displayMetrics.heightPixels / displayMetrics.ydpi;
            float xInches = displayMetrics.widthPixels / displayMetrics.xdpi;
            double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
            if (diagonalInches >= 7) {
                // 5inch device or bigger
                return true;
            } else {
                // smaller device
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static String getDeviceInch() {
        try {
            DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();

            float yInches = displayMetrics.heightPixels / displayMetrics.ydpi;
            float xInches = displayMetrics.widthPixels / displayMetrics.xdpi;
            double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
            return String.valueOf(diagonalInches);
        } catch (Exception e) {
            return "-1";
        }
    }

    private static String getDataType() {
        String type = "Mobile Data";
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                type = "Mobile Data 3G";
                Log.d("Type", "3g");
                // for 3g HSDPA networktype will be return as
                // per testing(real) in device with 3g enable
                // data
                // and speed will also matters to decide 3g network type
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                type = "Mobile Data 4G";
                Log.d("Type", "4g");
                // No specification for the 4g but from wiki
                // i found(HSPAP used in 4g)
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                type = "Mobile Data GPRS";
                Log.d("Type", "GPRS");
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                type = "Mobile Data EDGE 2G";
                Log.d("Type", "EDGE 2g");
                break;

        }
        return type;
    }
}