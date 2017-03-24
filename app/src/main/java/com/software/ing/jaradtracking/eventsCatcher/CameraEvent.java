package com.software.ing.jaradtracking.eventsCatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Raelyx on 18/7/2016.
 */
public class CameraEvent extends BroadcastReceiver {
    public CameraEvent() {
    }
    String c;
    JSONObject evento;
    String nombreEvento;
    String tipoEvento;
    UserPreferencesManager userPreferencesManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        c = intent.getAction();
        if (c.equals("android.hardware.action.NEW_PICTURE")){

            userPreferencesManager = new UserPreferencesManager(context);
            nombreEvento = "se ha tomado una nueva foto";
            tipoEvento = "CAMARA";

            if (userPreferencesManager.isPanico()){
                evento = new JSONObject();
                try {
                    evento.put("event", nombreEvento);
                    evento.put("type", tipoEvento);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (userPreferencesManager.isPanico()){
                    SocketManager.emitPhoneEvent(evento);
                }
            }

            Toast.makeText(context, "CAMARA: " + "se ha tomado una nueva foto", Toast.LENGTH_SHORT).show();
            Log.i("evento", "CAMARA: " + "se ha tomado una foto nueva");
        }
    }

}
