package com.software.ing.jaradtracking.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.software.ing.jaradtracking.Activities.RedButtonActivity;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.services.InformationRecoveryService;
import com.software.ing.jaradtracking.services.LockingService;
import com.software.ing.jaradtracking.services.PanicService;
import com.software.ing.jaradtracking.services.UploadingFilesService;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

/**
 * Created by Raelyx on 10/7/2016.
 */
public final class SocketManager {

    public static Socket socket;
    public static IO.Options options;
    static UserPreferencesManager session;
    static Context aplicationContext;
    static boolean success = false;
    static UserPreferencesManager userPreferencesManager;
    static Handler customHandler;
    static String TAG = "SocketManager";
    public static boolean socketStatus = false;

    public SocketManager() {    }

    static Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){
                socket.connect();
                Utils.log(TAG+" HILO" , "TRATANDO DE CONECTAR" );
                customHandler.postDelayed(this, 10000);
            }
        }
    };

    public static void setAplicationContext(Context aplicationContext) {
        SocketManager.aplicationContext = aplicationContext;
    }

    public static SocketManager newInstance(Context context) {
        SocketManager.aplicationContext = context;
        SocketManager fragment = new SocketManager();

        return fragment;
    }

    public static void tryToConnect(){
        if(!socketStatus){
            socket.connect();
        }

    }

    public static void startSocket(){
        userPreferencesManager = new UserPreferencesManager(aplicationContext);
        session = new UserPreferencesManager(aplicationContext);
        options = new IO.Options();
        customHandler = new Handler();
        try{
            options.query = "token=" + session.getToken();
            Utils.log(TAG,"" + "options:" + options.query );

            socket = IO.socket(aplicationContext.getString(R.string.ipServerSocketC9), options);

            socket.connect();


        }catch(URISyntaxException e){
            throw new RuntimeException(e);
        }

        socket.on(com.github.nkzawa.socketio.client.Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Utils.log(TAG,"SOCKET DISCONECTED" + " ENTRO" );

                Looper.prepare();
                customHandler = new Handler();
                customHandler.postDelayed(updateTimerThread, 1000);
                socketStatus = false;
            }
        });

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Utils.log(TAG,"SOCKET CONECTED" +" ENTRO" );
                customHandler = null;
                socketStatus = true;
            }
        });

        socket.on("event:panic", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject response = (JSONObject) args[0];
                try {
                    if (response.get("status").equals(true)){

                        aplicationContext.startService(new Intent(aplicationContext, PanicService.class));
                        aplicationContext.startService(new Intent(aplicationContext, LockingService.class));
                        aplicationContext.startService(new Intent(aplicationContext, InformationRecoveryService.class));
                        aplicationContext.startService(new Intent(aplicationContext, UploadingFilesService.class));
                        userPreferencesManager.setPanico(true);


                    }else if (response.get("status").equals(false)){
                        aplicationContext.stopService(new Intent(aplicationContext, PanicService.class));
                        aplicationContext.stopService(new Intent(aplicationContext, LockingService.class));
                        aplicationContext.startService(new Intent(aplicationContext, InformationRecoveryService.class));
                        aplicationContext.stopService(new Intent(aplicationContext, UploadingFilesService.class));
                        userPreferencesManager.setPanico(false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static boolean emitRegister(JSONObject registerUser){
        Utils.log(TAG,"" + "emit register ENTRO" );
        socket.emit("new:device", registerUser, new Ack() {
            @Override
            public void call(Object... args) {
                JSONObject response = (JSONObject) args[0];
                try {
                    if (response.get("success").equals(true)){
                        Utils.log(TAG,"EMIT REGISTER SUCCESS" );
                        success = response.get("success").equals(true);
                    }else {
                       success = false;
                        Utils.log(TAG,"EMIT REGISTER NOT SUCCESS" );
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        return success;
    }

    public static void panicButtonOn (boolean status){
        if(socket!= null)
        socket.emit("panic:movil", status);
    }

    public static void emitLocation (JSONObject location){
        if(socket!= null)
        socket.emit("map:location", location);
    }

    public static void emitContacts (JSONObject contacts){
        if(socket!= null)
        socket.emit("save:contacts", contacts);
    }

    public static void emitCalls (JSONObject calls){
        if(socket!= null)
            socket.emit("new:event", calls);
    }

    public static void emitMsjs (JSONObject msjs){
        if(socket!= null)
            socket.emit("new:message", msjs);
    }

    public static void emitEmergencyMail (JSONObject mail){
        if(socket!= null)
            socket.emit("config:mail", mail);
    }

    public static void emitPhoneEvent (JSONObject event){
        if(socket!= null)
        socket.emit("new:event", event);

    }
}
