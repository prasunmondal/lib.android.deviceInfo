package com.prasunmondal.lib.android.deviceinfo.libdev;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

class InstalledApplications {

    protected static String get(InstalledApps type, Context context) throws Exception {

        switch (type) {
            case ALL_APPS_COUNT:
                return String.valueOf(getAllAppsCount(context));
            case USER_APPS_COUNT:
                return String.valueOf(getUserAppsCount(context));
            case SYSTEM_APPS_COUNT:
                return String.valueOf(getSystemAppsCount(context));

            case ALL_APPS_LIST:
                return getAllApps(context);
            case USER_APPS_LIST:
                return getUserApps(context);
            case SYSTEM_APPS_LIST:
                return getSystemApps(context);
        }
        return "";
    }

    private static String getAllApps(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

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
            result += GetAppName(packageInfo.packageName, context) + "  (" + packageInfo.packageName +")";
        }
        return result;
    }

    private static String getUserApps(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Collections.sort(packages, new Comparator<ApplicationInfo>() {
            public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                return o1.packageName.compareTo(o2.packageName);
            }
        });

        String result = "";
        boolean isFirst = true;
        for (ApplicationInfo packageInfo : packages) {
            if (!isSystemApp(packageInfo)) {
                if (!isFirst) {
                    result += "\n";
                } else {
                    isFirst = false;
                }
                result += GetAppName(packageInfo.packageName, context) + "  (" + packageInfo.packageName +")";
            }
        }
        return result;
    }

    private static String getSystemApps(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Collections.sort(packages, new Comparator<ApplicationInfo>() {
            public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                return o1.packageName.compareTo(o2.packageName);
            }
        });

        String result = "";
        boolean isFirst = true;
        for (ApplicationInfo packageInfo : packages) {
            if (isSystemApp(packageInfo)) {
                if (!isFirst) {
                    result += "\n";
                } else {
                    isFirst = false;
                }
                result += GetAppName(packageInfo.packageName, context) + "  (" + packageInfo.packageName +")";
            }
        }
        return result;
    }

    private static int getSystemAppsCount(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        int count = 0;
        for (ApplicationInfo packageInfo : packages) {
            if (isSystemApp(packageInfo)) {
                count++;
            }
        }
        return count;
    }

    private static int getUserAppsCount(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        int count = 0;
        for (ApplicationInfo packageInfo : packages) {
            if (!isSystemApp(packageInfo)) {
                count++;
            }
        }
        return count;
    }

    private static int getAllAppsCount(Context context) {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        return packages.size();
    }

    private static String GetAppName(String ApkPackageName, Context context){
        ApplicationInfo applicationInfo;
        PackageManager packageManager = context.getPackageManager();
        try {
            applicationInfo = packageManager.getApplicationInfo(ApkPackageName, 0);
            if(applicationInfo!=null){
                return  (String)packageManager.getApplicationLabel(applicationInfo);
            }
        }catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "Error Occured in fetching GetAppName";
    }

    private static boolean isSystemApp(ApplicationInfo packageInfo){
        return ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1);
    }
}
