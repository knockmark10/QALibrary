package tec.android.com.qadebbuger.managers;

import android.content.Context;
import android.content.pm.PackageInfo;

import tec.android.com.qadebbuger.enums.AppInfo;

public class PackageManager {

    /**
     * Gets different information form the package.
     * Specify the type of data you want to be retrieved
     * with the AppInfo enum.
     * <p>
     * i.e. PackageManager.getPackageInfo(getContext, AppInfo.PackageName);
     *
     * @param context holder of the context
     * @param info the kind of data you want to be retrieved
     * @return the info requested
     */
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
