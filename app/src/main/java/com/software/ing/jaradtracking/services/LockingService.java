package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.software.ing.jaradtracking.eventsCatcher.SystemsEvents;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

/**
 * Created by Raelyx on 30/7/2016.
 */
public class LockingService extends Service{

    private Handler customHandler;
    UserPreferencesManager userPreferencesManager;
    public static DevicePolicyManager devicePolicyManager;
    String TAG = "LockingService";


    private Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){
                devicePolicyManager.lockNow();
                customHandler.postDelayed(updateTimerThread, 30000);
                Utils.log(TAG, "en el hilo");

            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        userPreferencesManager = new UserPreferencesManager(this);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);

        if(userPreferencesManager.getBloqueo() == UserPreferencesManager.KEY_BLOQUEO_T2){ /// SI EL BLOQUEO ES INTERMITENTE
            customHandler = new Handler();
            Utils.log(TAG, "llamando hilo");
            customHandler.postDelayed(updateTimerThread, 10000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customHandler = null;
    }
}
