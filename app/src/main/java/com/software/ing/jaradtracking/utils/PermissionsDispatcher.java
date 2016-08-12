package com.software.ing.jaradtracking.utils;

import android.support.v4.app.ActivityCompat;
import com.software.ing.jaradtracking.Activities.RegisterActivity;
import permissions.dispatcher.PermissionUtils;


public class PermissionsDispatcher {
    private static final int REQUEST = 1234;
    String TAG = "PermissionsDispatcher";

    private static final String[] PERMISSIONS = new String[]
            {"android.permission.CAMERA",
                 "android.permission.READ_EXTERNAL_STORAGE",
                    "android.permission.WAKE_LOCK",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.READ_CONTACTS",
                    "android.permission.READ_CALL_LOG",
                    "android.permission.RECEIVE_SMS",
                    "android.permission.READ_SMS",
                    "android.permission.SEND_SMS",
                    "android.permission.ACCESS_WIFI_STATE",
                    "android.permission.ACCESS_NETWORK_STATE",
                    "android.permission.BLUETOOTH",
                    "android.permission.BLUETOOTH_ADMIN",
                    "android.permission.PROCESS_OUTGOING_CALLS",
                    "android.permission.INTERNET",
                    "android.permission.RECEIVE_BOOT_COMPLETED",
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.KILL_BACKGROUND_PROCESSES",
                    "android.permission.BATTERY_STATS",
                    "android.permission.SYSTEM_ALERT_WINDOW"
            };
//    private static final String[] PERMISSIONS = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE",
//        "android.permission.ACCESS_COARSE_LOCATION","android.permission.ACCESS_FINE_LOCATION",
//        "android.permission.CALL_PHONE","android.permission.READ_PHONE_STATE","android.permission.CAMERA"};


    private PermissionsDispatcher() {
    }

    public static void showDialogPermissions(RegisterActivity target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSIONS)) {
            Utils.log("PermissionsDispatcher","IFshowDialogPermissions");
            target.init();
        } else {
            Utils.log("PermissionsDispatcher","ELSEshowDialogPermissions");
            ActivityCompat.requestPermissions(target, PERMISSIONS, REQUEST);
        }
    }

    public static void onRequestPermissionsResult(RegisterActivity target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST:
                if (!PermissionUtils.hasSelfPermissions(target, PERMISSIONS)) {
                    Utils.log("PermissionsDispatcher","IFonRequestPermissionsResult");
                    target.finish();
                }else{
                    Utils.log("PermissionsDispatcher","ELSEonRequestPermissionsResult");
                    target.init();
                }
                break;
            default:
                break;
        }
    }
}