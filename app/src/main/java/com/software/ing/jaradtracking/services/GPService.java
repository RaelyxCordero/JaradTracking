package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.software.ing.jaradtracking.Activities.RegisterActivity;
import com.software.ing.jaradtracking.utils.FilesUploaderManager;
import com.software.ing.jaradtracking.utils.GPSManager;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Raelyx on 10/7/2016.
 */
public class GPService extends Service implements LocationListener{

    private Handler customHandler;
    private UserPreferencesManager session;
    private GPSManager gpsManager;
    private SocketManager socketManager;
    private boolean delayed = false;
    String TAG = "GPService";


    public void onCreate() {
        super.onCreate();
        session = new UserPreferencesManager(this);
        gpsManager = new GPSManager(this);
        customHandler = new Handler();

        SocketManager.setAplicationContext(this);
        if(gpsManager.obtainLastKnowLocation()!=null){
            onLocationChanged(gpsManager.getLocation());
            enviarAlerta();
        }else {
            delayed = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customHandler=null;
    }

    //hilo que envia geolocalizacion
    private Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){
                enviarAlerta();
            }
        }
    };

    private void enviarAlerta()   {

        JSONObject position = new JSONObject();

        try {
            position.put("latitud", gpsManager.getLocation().getLatitude());
            position.put("longitud", gpsManager.getLocation().getLongitude());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketManager.emitLocation(position);

        Utils.log(TAG, "LATITUD"+String.valueOf(gpsManager.getLocation().getLatitude()));
        Utils.log(TAG, "LONGITUD"+ String.valueOf(gpsManager.getLocation().getLongitude()));

        if(customHandler!=null) {
            customHandler.postDelayed(updateTimerThread, GPSManager.MIN_TIME_BW_UPDATES);
            Toast.makeText(GPService.this, "lt y lng", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {    }

    @Override
    public void onProviderEnabled(String provider) {    }

    @Override
    public void onProviderDisabled(String provider) {    }
}
