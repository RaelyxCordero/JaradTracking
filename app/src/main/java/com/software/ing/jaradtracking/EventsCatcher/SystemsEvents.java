package com.software.ing.jaradtracking.eventsCatcher;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.software.ing.jaradtracking.services.GPService;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

public class SystemsEvents extends BroadcastReceiver {

    UserPreferencesManager userPreferencesManager;
    JSONObject evento;
    String TAG = "SystemsEvents";

    private static String MODO_AVION_CAMBIO = "modo avion activado/desactivado";
    private static String VOLUMEN_CAMBIO = "el volumen ha cambiado";
    private static String TLF_ENCENDIDO = "el telefono se ha encendido";
    private static String TLF_APAGANDO = "el telefono se esta apagando";
    private static String TLF_DESBLOQUEADO = "el telefono se ha desbloqueado";
    private static String TLF_FALLO_DESBLOQUEO = "el telefono se ha intentado desbloquear";
    private static String MSJ_RECIBIDO = "se ha recibido un SMS";
    private static String LLAMADA_SALIENDO = "se esta realizando una llamada";
    private static String CABLE_CONECTADO = "el cargador/cable usb se ha conectado";
    private static String CABLE_DESCONECTADO = "el cargador/cable usb se ha desconectado";
    private static String CAMBIO_INTERNET_CONNECT = "se ha conectado/desconectado de internet";
    private static String CAMBIO_WIFI_CONNECT = "se ha conectado/desconectado de WIFI";
    private static String CAMBIO_WIFI_CONNECT2 = "se ha conectado/desconectado de WIFI";
    private static String CAMBIO_HORA = "la hora ha cambiado";
    private static String CAMBIO_BLUETOOTH = "el bluetooth se ha conectado/desconectado";
    private static String CAMBIO_GPS = "el GPS se ha activado/desctivado";
    private static String CAMBIO_PANTALLA_ENCENDIDA = "la pantalla se ha encendido";
    private static String CAMBIO_PANTALLA_APAGADA = "la pantalla se ha apagado";

    private static String LLAMADA = "Llamada";
    private static String MENSAJE = "MENSAJE";
    private static String TELEFONO = "Telefono";
    private static String CAMBIO = "Cambio";
    private static String CAMARA = "Camara";
    private static String OTRO = "Otro";
    public static boolean screenOff = false;

    public SystemsEvents() {    }

    String nombreEvento;
    String tipoEvento;

    @Override
    public void onReceive(Context context, Intent intent) {
        nombreEvento = intent.getAction();

        Utils.log(TAG ,"Action: " + intent.getAction());
        userPreferencesManager = new UserPreferencesManager(context);
        SocketManager.setAplicationContext(context);


        switch (nombreEvento)
        {
            case "android.intent.action.SCREEN_OFF":
            {
                nombreEvento = CAMBIO_PANTALLA_APAGADA;
                tipoEvento = CAMBIO;
                screenOff = true;
                Utils.log(TAG, "SCREEN_OFF");
                break;
            }
            case "android.intent.action.SCREEN_ON":
            {
                nombreEvento = CAMBIO_PANTALLA_ENCENDIDA;
                tipoEvento = CAMBIO;
                screenOff = false;
                Utils.log(TAG,"SCREEN_ON");
                break;
            }

            case "android.intent.action.AIRPLANE_MODE":
            {
                nombreEvento = MODO_AVION_CAMBIO;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.app.action.ACTION_PASSWORD_SUCCEEDED":
            {
                nombreEvento = TLF_DESBLOQUEADO;
                tipoEvento = TELEFONO;
                break;
            }
            case "android.app.action.ACTION_PASSWORD_FAILED":
            {
                nombreEvento = TLF_FALLO_DESBLOQUEO;
                tipoEvento = TELEFONO;
                break;
            }


            case "android.media.VOLUME_CHANGED_ACTION":
            {
                nombreEvento = VOLUMEN_CAMBIO;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.intent.action.BOOT_COMPLETED":
            {
                nombreEvento = TLF_ENCENDIDO;
                tipoEvento = TELEFONO;

                // AL TERMINAR DE ENCENDER EL TLF INICIA EL SERVICIO DE GPS

                SharedPreferences prefer = context.getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
                if(prefer.getBoolean("activity_executed", false)){
                    Intent intentService = new Intent(context, GPService.class);
                    context.startService(intentService);
                    Utils.log(TAG, "GPService started");
                }


                break;
            }
            case "android.provider.Telephony.SMS_RECEIVED":
            {
                nombreEvento = MSJ_RECIBIDO;
                tipoEvento = MENSAJE;
                break;
            }

            case "android.intent.action.NEW_OUTGOING_CALL":
            {
                nombreEvento = LLAMADA_SALIENDO;
                tipoEvento = LLAMADA;
                break;
            }
            case "android.intent.action.ACTION_POWER_CONNECTED":
            {
                nombreEvento = CABLE_CONECTADO;
                tipoEvento = TELEFONO;
                break;
            }
            case "android.intent.action.ACTION_POWER_DISCONNECTED":
            {
                nombreEvento = CABLE_DESCONECTADO;
                tipoEvento = TELEFONO;
                break;
            }
            case "android.intent.action.ACTION_SHUTDOWN":
            {
                nombreEvento = TLF_APAGANDO;
                tipoEvento = TELEFONO;
                break;
            }
            case "android.net.conn.CONNECTIVITY_CHANGE":
            {
                nombreEvento = CAMBIO_INTERNET_CONNECT;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.net.wifi.WIFI_STATE_CHANGED":
            {
                nombreEvento = CAMBIO_WIFI_CONNECT;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.net.wifi.STATE_CHANGED":
            {
                nombreEvento = CAMBIO_WIFI_CONNECT2;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.intent.action.TIME_SET":
            {
                nombreEvento = CAMBIO_HORA;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.bluetooth.adapter.action.STATE_CHANGED":
            {
                nombreEvento = CAMBIO_BLUETOOTH;
                tipoEvento = CAMBIO;
                break;
            }
            case "android.location.PROVIDERS_CHANGED":
            {
                nombreEvento = CAMBIO_GPS;
                tipoEvento = CAMBIO;
                break;
            }
            default:
                break;
        }

        userPreferencesManager = new UserPreferencesManager(context);



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
