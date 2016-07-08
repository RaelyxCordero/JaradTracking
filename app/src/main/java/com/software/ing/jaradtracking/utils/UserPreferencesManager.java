package com.software.ing.jaradtracking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;


public class UserPreferencesManager {

    private static final String KEY_CORREO = "CORREO";
    private static final String KEY_TELEFONO = "TELEFONO";
    private static final String KEY_INTERVALO = "INTERVALO";
    private static final String KEY_MENSAJE = "MENSAJE";
    private static final String KEY_BLOQUEO = "BLOQUEO";
    public static final String KEY_BLOQUEO_T1 = "Permanente";
    public static final String KEY_BLOQUEO_T2 = "Intermintente";

    SharedPreferences pref;
    Editor editor;
    Context _context;
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

}