package com.software.ing.jaradtracking.eventsCatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.software.ing.jaradtracking.services.PanicService;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class CallEvent extends BroadcastReceiver {

    UserPreferencesManager userPreferencesManager;
    JSONObject evento;
    String nombreEvento;
    String tipoEvento;
    String TAG = "CallEvent";


    public CallEvent() {  }

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras != null){

            //LLAMADAS ENTRANTES

            String state = extras.getString(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING))
            {
                String phoneNumber = extras.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                //por aqui se envia el mensaje a la consola
                Utils.log(TAG,"Llamada---->"+ phoneNumber);

                userPreferencesManager = new UserPreferencesManager(context);

                nombreEvento = phoneNumber;
                tipoEvento = "LLAMADA";

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
            }
        }
    }


}
