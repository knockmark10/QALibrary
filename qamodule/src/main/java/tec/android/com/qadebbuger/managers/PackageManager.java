package tec.android.com.qadebbuger.managers;

import android.content.Context;
import android.content.pm.PackageInfo;
import tec.android.com.qadebbuger.enums.AppInfo;

public class PackageManager {

    public static String getPackageInfo(Context context, AppInfo info) {
        PackageInfo packageInfo = null;
        String appData = "";
        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        switch (info) {
            case VersionCode:
                appData = String.valueOf(packageInfo.versionCode);
                break;

            case VersionName:
                appData = packageInfo.versionName;
                break;

            case PackageName:
                appData = packageInfo.packageName;
                break;
        }

        return appData;
    }

}
