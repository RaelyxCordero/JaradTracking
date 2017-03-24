package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.software.ing.jaradtracking.eventsCatcher.SystemsEvents;
import com.software.ing.jaradtracking.utils.GPSManager;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Raelyx on 10/7/2016.
 */
public class GPService extends Service implements LocationListener{

    private Handler customHandler;
    private GPSManager gpsManager;
    public static boolean gpServiceStatus = false;
    String TAG = "GPService";


    public void onCreate() {
        super.onCreate();

        gpsManager = new GPSManager(this);
        customHandler = new Handler();
        SocketManager.setAplicationContext(this);
        if(!SocketManager.socketStatus){
            SocketManager.startSocket();
        }
        if(gpsManager.obtainLastKnowLocation()!=null){
            onLocationChanged(gpsManager.getLocation());
            sendLocation();
            gpServiceStatus = true;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        customHandler = null;
        gpServiceStatus = false;
    }

    //hilo que envia geolocalizacion
    private Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){
                if(!SocketManager.socketStatus){
                    SocketManager.setAplicationContext(getApplicationContext());
                    Utils.log(TAG, "socket status false");
                    SocketManager.tryToConnect();
                }
                sendLocation();
            }
        }
    };

    private void sendLocation()   {

        Location location  = gpsManager.obtainLastKnowLocation();


        JSONObject position = new JSONObject();
        try {
            position.put("latitud", location.getLatitude());
            position.put("longitud", location.getLongitude());

        } catch (JSONException e) {            e.printStackTrace();        }

       SocketManager.emitLocation(position);

        Utils.log(TAG, "LATITUD: "+String.valueOf(location.getLatitude()));
        Utils.log(TAG, "LONGITUD: "+ String.valueOf(location.getLongitude()));


        if(customHandler!=null) {
            customHandler.postDelayed(updateTimerThread, GPSManager.MIN_TIME_BW_UPDATES);
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
