package com.software.ing.jaradtracking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.software.ing.jaradtracking.interfaces.ChangeListener;


public class UserPreferencesManager {

    private static final String KEY_CORREO = "CORREO";
    private static final String KEY_PANICO = "PANICO";
    private static final String KEY_PRESS_PANICO = "PANICO";
    private static final String KEY_FILE = "FILE";
    private static final String KEY_TELEFONO = "TELEFONO";
    private static final String KEY_INTERVALO = "INTERVALO";
    private static final String KEY_MENSAJE = "MENSAJE";
    private static final String KEY_BLOQUEO = "BLOQUEO";
    public static final String KEY_BLOQUEO_T1 = "Permanente";
    public static final String KEY_BLOQUEO_T2 = "Intermintente";
    public static final String KEY_TOKEN = "Token";
    public static final String KEY_TOKENDB = "TokenDB";
    public static String _TOKEN = null;
    public static String DROPBOX_TOKEN = null;
    private ChangeListener changeListener = null;
    private ChangeListener changeListener2 = null;

    SharedPreferences pref;
    Editor editor;
    public static Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREFER_NAME = "SesionPref";

    // Constructor
    public UserPreferencesManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }
    //GETTERS KEY
    public static String getKeyCorreo() {
        return KEY_CORREO;
    }

    public static String getKeyTelefono() {
        return KEY_TELEFONO;
    }

    public static String getKeyIntervalo() {
        return KEY_INTERVALO;
    }

    public static String getKeyMensaje() {
        return KEY_MENSAJE;
    }

    public static String getKeyBloqueo() {
        return KEY_BLOQUEO;
    }

    public static String getKeyToken() {
        return KEY_TOKEN;
    }


    public String getTokenDB(){return pref.getString(KEY_TOKENDB, "");}

    public void setTokenDB(String token){
        DROPBOX_TOKEN = token;
        editor.putString(KEY_TOKENDB, token);
        editor.apply();
        editor.commit();
    }

    public String getToken(){return pref.getString(KEY_TOKEN, "");}

    public void setToken(String token){
        _TOKEN = token;
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        editor.commit();
    }

    public boolean isPanico() {return pref.getBoolean(KEY_PANICO, false);}

    public void setPanico(boolean panico){
        editor.putBoolean(KEY_PANICO, panico);
        editor.apply();
        editor.commit();
        if (panico)        Log.e("PANICO", "ACTIVO");
        else               Log.e("PANICO", "INACTIVO");
    }

    public boolean isPanicoPress() {return pref.getBoolean(KEY_PRESS_PANICO, false);}

    public void setPanicoPress(boolean panicoPress){
        editor.putBoolean(KEY_PRESS_PANICO, panicoPress);
        editor.apply();
        editor.commit();
        if (panicoPress)        Log.e("panicoPress", "ACTIVO");
        else               Log.e("panicoPress", "INACTIVO");
    }

    public boolean isFiles() {return pref.getBoolean(KEY_FILE, false);}

    public void setFiles(boolean file){
        editor.putBoolean(KEY_FILE, file);
        editor.apply();
        editor.commit();
        if (file)        Log.e("file", "ACTIVO");
        else               Log.e("file", "INACTIVO");
    }

    public String getBloqueo(){return pref.getString(KEY_BLOQUEO, KEY_BLOQUEO_T1);}

    public void setBloqueo(String bloqueo){

        editor.putString(KEY_BLOQUEO, bloqueo);
        editor.apply();
        editor.commit();
    }

    public String getIntervalo(){return pref.getString(KEY_INTERVALO, "15");}

    public void setIntervalo(String intervalo){

        editor.putString(KEY_INTERVALO, intervalo);
        editor.apply();
        editor.commit();
    }

    public String getMensaje(){return pref.getString(KEY_MENSAJE, "Me paso algo aiura :c");}

    public void setMensaje(String mensaje){

        editor.putString(KEY_MENSAJE, mensaje);
        editor.apply();
        editor.commit();
    }

    public void setCorreo(String correo) {
        editor.putString(KEY_CORREO, correo);
        editor.apply();
        editor.commit();
    }

    public String getCorreo() {
        return pref.getString(KEY_CORREO, "");
    }

    public void setTelefono(String telefono) {
        editor.putString(KEY_TELEFONO, telefono);
        editor.apply();
        editor.commit();
    }

    public String getTelefono() {
        return pref.getString(KEY_TELEFONO, "");
    }

    public void destroyed(){
        editor.clear();
        editor.apply();
        editor.commit();
    }


    public void setVariableChangeListener(ChangeListener variableChangeListener) {
        this.changeListener = variableChangeListener;
        if( _TOKEN != null) {
            // call the listener here, note that we don't want to a strong coupling
            // between the listener and where the event is occurring. With this pattern
            // the code has the flexibility of assigning the listener
            this.changeListener.onChange(_TOKEN);
        }

    }

    public void setVariableChangeListener2(ChangeListener variableChangeListener) {
        this.changeListener2 = variableChangeListener;
        Utils.log("user preferences listener", "entro");
        if( DROPBOX_TOKEN != null) {
            Utils.log("user preferences listener", "tokenThatHasChanged: " + DROPBOX_TOKEN);
            // call the listener here, note that we don't want to a strong coupling
            // between the listener and where the event is occurring. With this pattern
            // the code has the flexibility of assigning the listener
            this.changeListener2.onChange(DROPBOX_TOKEN);
        }

    }
}