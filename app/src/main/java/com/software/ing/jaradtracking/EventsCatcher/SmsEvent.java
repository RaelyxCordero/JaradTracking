package com.software.ing.jaradtracking.eventsCatcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;


public class SmsEvent extends BroadcastReceiver {

  //  final SmsManager sms = SmsManager.getDefault();
  UserPreferencesManager userPreferencesManager;
    JSONObject evento;
    String nombreEvento;
    String tipoEvento;

    public SmsEvent() {}
    //mensajes entrantes
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();

            try {

                if (extras != null) {

                    final Object[] pdusObj = (Object[]) extras.get("pdus");

                    for (int i = 0; i < pdusObj.length; i++) {

                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                        String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                        String senderNum = phoneNumber;
                        String message = currentMessage.getDisplayMessageBody();
                        //por aqui se envia el mensaje a la consola
                        Log.i("Mensaje", "De: "+ senderNum + "; mensaje: " + message);

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


                        // Show alert
                      //  int duration = Toast.LENGTH_LONG;
                      //  Toast toast = Toast.makeText(context, "senderNum: "+ senderNum + ", message: " + message, duration);
                      //  toast.show();

                    } // end for loop
                } // bundle is null

            } catch (Exception e) {
                Log.e("SmsReceiver", "Exception smsReceiver" +e);

            }



        }


}