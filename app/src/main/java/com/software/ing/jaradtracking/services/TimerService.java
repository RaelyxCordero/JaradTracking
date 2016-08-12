package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

/**
 * Created by Raelyx on 30/7/2016.
 */
public class TimerService extends Service{

    private Handler customHandler;


    private Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){
                startService(new Intent(getApplicationContext(), PanicService.class));
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
        customHandler = new Handler();
        Integer interval = 8;
        if(customHandler!=null){
            customHandler.postDelayed(updateTimerThread, Utils.getInterval(("" + interval)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customHandler = null;
    }
}
