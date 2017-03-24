package com.software.ing.jaradtracking.utils;

import android.support.v4.app.ActivityCompat;
import com.software.ing.jaradtracking.Activities.RegisterActivity;
import permissions.dispatcher.PermissionUtils;


public class PermissionsDispatcher {
    private static final int REQUEST = 1;
    public static String TAG = "PermissionsDispatcher";

    public static final String[] PERMISSIONS = new String[]
            {       "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.CAMERA",
                    "android.permission.READ_PHONE_STATE",
                    "android.permission.READ_CONTACTS",
                    "android.permission.READ_SMS",
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            };



    private PermissionsDispatcher() {    }

    public static void showDialogPermissions(RegisterActivity target) {
        if (PermissionUtils.hasSelfPermissions(target, PERMISSIONS)) {
            Utils.log(TAG,"IFshowDialogPermissions");
            target.init();
        } else {
            Utils.log(TAG,"ELSEshowDialogPermissions");
            ActivityCompat.requestPermissions(target, PERMISSIONS, REQUEST);

        }
    }

    public static void onRequestPermissionsResult(RegisterActivity target, int requestCode, int[] grantResults) {
        switch (requestCode) {
            case REQUEST:
                if (!PermissionUtils.hasSelfPermissions(target, PERMISSIONS)) {
                    Utils.log(TAG,"IFonRequestPermissionsResult");
                    target.finish();
                }else{
                    Utils.log(TAG,"ELSEonRequestPermissionsResult");
                    target.init();
                }
                break;
            default:
                break;
        }
    }
}