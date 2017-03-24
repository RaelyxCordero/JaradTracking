package com.software.ing.jaradtracking.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.software.ing.jaradtracking.Activities.RegisterActivity;
import com.software.ing.jaradtracking.interfaces.ChangeListener;


public class UserPreferencesManager {

    private static final String KEY_CORREO = "CORREO";
    private static final String KEY_PANICO = "PANICO";
    private static final String KEY_PRESS_PANICO = "PANICO";
    private static final String KEY_FILE_PANIC = "FILE_PANIC";
    private static final String KEY_FILE_DOWNLOADS = "FILE_DOWNLOADS";
    private static final String KEY_FILE_GALLERY = "FILE_GALLERY";
    private static final String KEY_TELEFONO = "TELEFONO";
    private static final String KEY_INTERVALO_MSJ = "INTERVALO_MSJ";
    private static final String KEY_INTERVALO_REMOTE_PANIC = "INTERVALO_REMOTE_PANIC";
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
    String TAG = "UserPreferencesManager";

    SharedPreferences pref;
    Editor editor;
    public static Context _context;
    private static final String PREFER_NAME = "SesionPref";

    // Constructor
    public UserPreferencesManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();

    }

    public String getTokenDB(){return pref.getString(KEY_TOKENDB, null);}

    public void setTokenDB(String token){
        DROPBOX_TOKEN = token;
        RegisterActivity.dbAuth = true;
        editor.putString(KEY_TOKENDB, token);
        editor.apply();
        editor.commit();
        Utils.log(TAG, " "+ getTokenDB());
    }

    public String getToken(){return pref.getString(KEY_TOKEN, "");}

    public void setToken(String token){
        _TOKEN = token;
        editor.putString(KEY_TOKEN, token);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getToken());
        SharedPreferences prefer = _context.getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
//        if(!prefer.getBoolean("activity_executed", false)){
//            Utils.log(TAG, "actividad ejecutada primera vez, inicia socket");
//            SocketManager.startSocket();
//        }



    }

    public boolean isPanico() {return pref.getBoolean(KEY_PANICO, false);}

    public void setPanico(boolean panico){
        editor.putBoolean(KEY_PANICO, panico);
        editor.apply();
        editor.commit();
        if (panico)        Utils.log(TAG, "PANICO ACTIVO");
        else               Utils.log(TAG,"PANICO INACTIVO");
        Utils.log(TAG, ""+ isPanico());
    }

    public boolean isPanicoPress() {return pref.getBoolean(KEY_PRESS_PANICO, false);}

    public void setPanicoPress(boolean panicoPress){
        editor.putBoolean(KEY_PRESS_PANICO, panicoPress);
        editor.apply();
        editor.commit();
        if (panicoPress)        Log.e("panicoPress", "ACTIVO");
        else               Log.e("panicoPress", "INACTIVO");
        Utils.log(TAG, ""+ isPanicoPress());
    }

    public boolean isFilesPanic() {return pref.getBoolean(KEY_FILE_PANIC, false);}

    public void setFilesPanic(boolean file){
        editor.putBoolean(KEY_FILE_PANIC, file);
        editor.apply();
        editor.commit();
        if (file)        Utils.log(TAG, "HAY CARPETA panic");
        else               Utils.log(TAG, "NO HAY CARPETA panic");
        Utils.log(TAG, ""+ isFilesPanic());
    }

    public boolean isFilesDownloads() {return pref.getBoolean(KEY_FILE_DOWNLOADS, false);}

    public void setFilesDownloads(boolean file){
        editor.putBoolean(KEY_FILE_DOWNLOADS, file);
        editor.apply();
        editor.commit();
        if (file)        Utils.log(TAG, "HAY CARPETA download");
        else               Utils.log(TAG, "NO HAY CARPETAS download");
        Utils.log(TAG, ""+ isFilesDownloads());
    }

    public boolean isFilesPhotos() {return pref.getBoolean(KEY_FILE_GALLERY, false);}

    public void setFilesPhotos(boolean file){
        editor.putBoolean(KEY_FILE_GALLERY, file);
        editor.apply();
        editor.commit();
        if (file)        Utils.log(TAG, "HAY CARPETA gallery");
        else               Utils.log(TAG, "NO HAY CARPETA gallery");
        Utils.log(TAG, ""+ isFilesPhotos());
    }

    public String getBloqueo(){return pref.getString(KEY_BLOQUEO, KEY_BLOQUEO_T1);}

    public void setBloqueo(String bloqueo){

        editor.putString(KEY_BLOQUEO, bloqueo);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getBloqueo());
    }

    public String getIntervaloMsj(){return pref.getString(KEY_INTERVALO_MSJ, "15");}

    public void setIntervaloMsj(String intervalo){

        editor.putString(KEY_INTERVALO_MSJ, intervalo);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getIntervaloMsj());
    }

    public String getIntervaloPanic(){return pref.getString(KEY_INTERVALO_REMOTE_PANIC, "8");}

    public void setIntervaloPanic(String intervalo){

        editor.putString(KEY_INTERVALO_REMOTE_PANIC, intervalo);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getIntervaloPanic());
    }

    public String getMensaje(){return pref.getString(KEY_MENSAJE, "Me paso algo aiura :c");}

    public void setMensaje(String mensaje){

        editor.putString(KEY_MENSAJE, mensaje);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getMensaje());
    }

    public void setCorreo(String correo) {
        editor.putString(KEY_CORREO, correo);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getCorreo());
    }

    public String getCorreo() {
        return pref.getString(KEY_CORREO, "");
    }

    public void setTelefono(String telefono) {
        editor.putString(KEY_TELEFONO, telefono);
        editor.apply();
        editor.commit();
        Utils.log(TAG, ""+ getTelefono());
    }

    public String getTelefono() {
        return pref.getString(KEY_TELEFONO, "");
    }

}