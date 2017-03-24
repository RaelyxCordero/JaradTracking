package com.software.ing.jaradtracking.eventsCatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;


public class SmsEvent extends BroadcastReceiver {

  UserPreferencesManager userPreferencesManager;
    JSONObject evento;
    String nombreEvento;
    String tipoEvento;
    String TAG = "SmsEvent";

    public SmsEvent() {}

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

            try {
                //MENSAJES ENTRANTES

                if (extras != null) {

                    final Object[] pdusObj = (Object[]) extras.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        //por aqui se envia el mensaje a la consola
                        Utils.log(TAG, "Mensaje de: "+ senderNum);
                        Utils.log(TAG, "dice: " + message);

                        userPreferencesManager = new UserPreferencesManager(context);

                        nombreEvento = "Ha llegado un mensaje de este numero: " + senderNum;
                        tipoEvento = "MENSAJE";

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

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }
        }
}