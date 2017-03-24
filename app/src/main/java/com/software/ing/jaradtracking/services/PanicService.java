package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;


import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.utils.GPSManager;
import com.software.ing.jaradtracking.utils.PictureSaver;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


public class PanicService extends Service implements LocationListener {

   private Handler customHandler;
   private UserPreferencesManager session;
   private boolean delayed = false;
    private GPSManager gpsManager;
    JSONObject evento;
    String TAG = "PanicService";



   @Override
   public void onCreate() {
       super.onCreate();
       gpsManager = new GPSManager(this);

       session = new UserPreferencesManager(this);
       customHandler = new Handler();
       customHandler.postDelayed(updateTimerThread2, 0);
       delayed = true;
       sendEmail();

   }

    public void sendEmail(){
        JSONObject json = new JSONObject();
        try {
            json.put("mail", session.getCorreo());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        SocketManager.emitEmergencyMail(json);
    }


   @Override
   public void onDestroy() {
       super.onDestroy();
       customHandler = null;
   }

//Hilo que envia mensajes y foto
   private Runnable updateTimerThread2 = new Runnable(){
       public void run(){
           if(customHandler!=null){
               takePhoto(PanicService.this);
               enviarMensajes();
           }
       }
   };


   @Override public IBinder onBind(Intent intent) { return null; }

   private void uploadPhoto(File file) {

       if (file != null) {
           if(UploadingFilesService.uploadingStatus){
               UploadingFilesService.setPanicPhoto(file);
               UploadingFilesService.uploadPanicPhotos();
           }


           evento = new JSONObject();
           try {
               Utils.log(TAG, "Subiendo foto");
               Utils.log(TAG+" BASE64" , Utils.fileToBase64(file));//,

               evento.put("event", Utils.fileToBase64(file) );
               evento.put("type", "PICTURE");

           } catch (JSONException e) {
               e.printStackTrace();
           }
           Utils.log(TAG, ""+ evento);
           SocketManager.emitPhoneEvent(evento);

       }

   }


   @SuppressWarnings("deprecation")
   private void takePhoto(final Context context) {

       Utils.log(TAG, "take foto");
       final SurfaceView preview = new SurfaceView(context);
       SurfaceHolder holder = preview.getHolder();
//        deprecated setting, but required on Android versions prior to 3.0
       holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
       holder.addCallback(new SurfaceHolder.Callback() {

           @Override
           public void surfaceCreated(SurfaceHolder holder) {
               Utils.log(TAG,"Surface created");

               Camera camera = null;

               try {
                   camera = Camera.open();
                   Utils.log(TAG,"Opened camera");

                   try {
                       camera.setPreviewDisplay(holder);
                   } catch (IOException e) {
                       Utils.log(TAG, e.getMessage());
                   }

                   camera.startPreview();
                   Utils.log(TAG,"Started preview");

                   camera.takePicture(null, null, new Camera.PictureCallback() {

                       @Override
                       public void onPictureTaken(byte[] data, Camera camera) {
                           Utils.log(TAG,"Took picture");
                           uploadPhoto(PictureSaver.savePicture(data, getString(R.string.app_name)));
                           camera.release();
                       }
                   });
               } catch (Exception e) {
                   Utils.log(TAG, e.getMessage());
                   if (camera != null)
                       camera.release();
               }
           }

           @Override
           public void surfaceDestroyed(SurfaceHolder holder) {
           }

           @Override
           public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
           }
       });


       WindowManager wm = (WindowManager)context
               .getSystemService(Context.WINDOW_SERVICE);
       WindowManager.LayoutParams params = new WindowManager.LayoutParams(
               1, 1, //Must be at least 1x1
               WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
               0,
               //Don't know if this is a safe default
               PixelFormat.UNKNOWN);

       //Don't set the preview visibility to GONE or INVISIBLE
       wm.addView(preview, params);
   }

   private void enviarMensajes(){

       Location location  = gpsManager.obtainLastKnowLocation();

       try {
           SmsManager smsManager = SmsManager.getDefault();
           if(!session.getTelefono().equals("")) {
               smsManager.sendTextMessage(session.getTelefono(), null,
                       session.getMensaje() +
                                " con latitud: " +
                               location.getLatitude() +
                               " y longitud: " +
                               location.getLongitude()
                                , null, null);

               Utils.log(TAG, "SMS Sent!");
           }else{
               Utils.log(TAG, "No envia mensaje porque no hay tlf registrado");
           }
       } catch (Exception e) {
           Utils.log(TAG, "SMS failed");
           e.printStackTrace();
           Utils.log(TAG,"error sms "+e.getMessage());
       }

       if(customHandler!=null){
           customHandler.postDelayed(updateTimerThread2, Utils.getIntervalMinute(session.getIntervaloMsj()));
       }
   }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}