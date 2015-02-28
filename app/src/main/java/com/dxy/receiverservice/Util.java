package com.dxy.receiverservice;

import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by admin on 2015/2/27.
 */
public class Util {
    public final static int play = 1;
    public final static int stop = 2;
    public final static int pause = 3;
    public final static int exit = 4;
    public final static int close = 5;
    public final static int pre = 6;
    public final static int prepared = 7;
    public static String ACTION_NAME = "com.dxy.receiver.updateProgressReceiver";
    public static String MUSIC_ACTION_NAME = "com.dxy.receiver.musicReceiver";

    public static String BUNDLE_FIELD_POSITION = "position";
    public static String BUNDLE_FIELD_TYPE = "type";
    public static String BUNDLE_FIELD_OP = "op";
    public static String BUNDLE_FIELD_DURATION = "duration";


    public static String getTime(int time) {
        int musicTime = time / 1000;
        int minutes = musicTime / 60;
        int seconds = musicTime % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    /**
     * 获取当前应用版本号
     * @param context
     * @return version
     * @throws Exception
     */
    public static String getAppVersion(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
        String versionName = packInfo.versionName;
        return versionName;
    }

    /**
     * 获取当前系统SDK版本号
     */
    public static int getSystemVersion(){
		/*获取当前系统的android版本号*/
        int version= android.os.Build.VERSION.SDK_INT;
        return version;
    }
}
