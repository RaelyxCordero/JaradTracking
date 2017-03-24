package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.software.ing.jaradtracking.Activities.RedButtonActivity;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

/**
 * Created by Raelyx on 30/7/2016.
 */
public class TimerService extends Service{

    private Handler customHandler;
    UserPreferencesManager userPreferencesManager;
    String TAG = "TimerService";


    private Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){

                if (!userPreferencesManager.isPanico()) {
                    userPreferencesManager.setPanico(true);
                    Utils.log(TAG, "LLAMANDO SERVICIOS");
                    RedButtonActivity.devicePolicyManager.lockNow();
                    SocketManager.panicButtonOn(true);
                    startService(new Intent(getApplicationContext(), PanicService.class));
                    startService(new Intent(getApplicationContext(), LockingService.class));
                    startService(new Intent(getApplicationContext(), InformationRecoveryService.class));
                    startService(new Intent(getApplicationContext(), UploadingFilesService.class));
                }else {
                    Utils.log(TAG, "el panico ya esta activo");
                }
                customHandler = null;
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
        Utils.log(TAG, "oncreate timer service");
        userPreferencesManager = new UserPreferencesManager(this);
        customHandler = new Handler();
//        customHandler.postDelayed(updateTimerThread, 30000);
        customHandler.postDelayed(updateTimerThread, Utils.getIntervalHour(("" + userPreferencesManager.getIntervaloPanic())));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customHandler = null;
    }
}
