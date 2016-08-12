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
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.software.ing.jaradtracking.Activities.RegisterActivity;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.utils.FilesUploaderManager;
import com.software.ing.jaradtracking.utils.GPSManager;
import com.software.ing.jaradtracking.utils.PictureSaver;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;





public class PanicService extends Service implements LocationListener {

   private Handler customHandler;
   private UserPreferencesManager session;
   private boolean delayed = false;
    private GPSManager gpsManager;
    SocketManager socketManager;
    FilesUploaderManager filesUploaderManager;

   @Override
   public void onCreate() {
       super.onCreate();
       gpsManager = new GPSManager(this);
       SocketManager.setAplicationContext(this);
       session = new UserPreferencesManager(this);
       customHandler = new Handler();
       enviarMensajes();
       delayed = true;
       filesUploaderManager = new FilesUploaderManager(getApplicationContext());
       filesUploaderManager.initialize_session(session.getTokenDB());
       filesUploaderManager.uploadFiles();
//       socketManager.startSocket();

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

   private static void showMessage(String message) {
       Log.i("PANIC SERVICE", message);
   }

   @Override public IBinder onBind(Intent intent) { return null; }

   private void uploadPhoto(File file) {
       showMessage("Subiendo imagen");
        Log.w("BASE64" , Utils.fileToBase64(file));//,
//               alarma.getId(),
//               String.valueOf(gpsManager.getLocation().getLatitude()),
//               String.valueOf(gpsManager.getLocation().getLongitude()),
//                       mUploadCallBack);
   }


   @SuppressWarnings("deprecation")
   private void takePhoto(final Context context) {
       final SurfaceView preview = new SurfaceView(context);
       SurfaceHolder holder = preview.getHolder();
//        deprecated setting, but required on Android versions prior to 3.0
       holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

       holder.addCallback(new SurfaceHolder.Callback() {
           @Override
           //The preview must happen at or after this point or takePicture fails
           public void surfaceCreated(SurfaceHolder holder) {
               showMessage("Surface created");

               Camera camera = null;

               try {
                   camera = Camera.open();
                   showMessage("Opened camera");

                   try {
                       camera.setPreviewDisplay(holder);
                   } catch (IOException e) {
                       showMessage(e.getMessage());
                   }

                   camera.startPreview();
                   showMessage("Started preview");

                   camera.takePicture(null, null, new Camera.PictureCallback() {

                       @Override
                       public void onPictureTaken(byte[] data, Camera camera) {
                           showMessage("Took picture");
                           uploadPhoto(PictureSaver.savePicture(data, getString(R.string.app_name)));
                           camera.release();
                       }
                   });
               } catch (Exception e) {
                   showMessage(e.getMessage());
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

       try {
               SmsManager smsManager = SmsManager.getDefault();
           if(session.getTelefono() != null) {
               smsManager.sendTextMessage(session.getTelefono(), null,
                       session.getMensaje() + " con coordenadas: " +
                               gpsManager.getLocation().getLatitude() + ", " +
                               gpsManager.getLocation().getLongitude(), null, null);
               Toast.makeText(getApplicationContext(), "SMS Sent!",
                       Toast.LENGTH_LONG).show();
           }
       } catch (Exception e) {
           Toast.makeText(getApplicationContext(),
                   "SMS faild, please try again later!",
                   Toast.LENGTH_LONG).show();
           e.printStackTrace();
           showMessage("error sms "+e.getMessage());
       }
       showMessage("envia mensaje");
       if(customHandler!=null){
           customHandler.postDelayed(updateTimerThread2, Utils.getInterval(session.getIntervalo()));
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