package com.software.ing.jaradtracking.utils;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.software.ing.jaradtracking.Activities.RedButtonActivity;
import com.software.ing.jaradtracking.Activities.RegisterActivity;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.interfaces.ChangeListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Raelyx on 27/7/2016.
 */



public class FilesUploaderManager {

    public DropboxAPI<AndroidAuthSession> mDBApi;
    public Handler customHandler;
    public String APP_KEY;
    public String APP_SECRET;
    public Context _context;
    public ChangeListener changeListener;
    public String accessToken = null;
    public String TAG = "FilesUploaderManager";
    DropboxAPI.Entry response;
    public FilesUploaderManager(Context context){
        _context = context;
        customHandler = new Handler();
        APP_KEY = context.getResources().getString(R.string.DropboxAppKey);
        APP_SECRET = context.getResources().getString(R.string.DropboxAppSecret);
    }

    public void initialize_session(){

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startOAuth2Authentication(_context);
    }

    public void initialize_session(String token){

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().setOAuth2AccessToken(token); //setear token las veces posteriores
    }

    public void iniciarHilo(){
        if(customHandler!=null){
            customHandler.postDelayed(updateTimerThread, 10000);
        }
    }

    Runnable updateTimerThread = new Runnable(){

        public void run(){
            if(customHandler!=null){
                Utils.log(TAG, "entro al hilo");
                if(accessToken == null){
                    Utils.log(TAG, "accessToken == null");
                    if (mDBApi.getSession().authenticationSuccessful()) {
                        Utils.log(TAG, "authenticationSuccessful");
                        try {
                            // Required to complete auth, sets the access token on the session
                            mDBApi.getSession().finishAuthentication();
                            UserPreferencesManager userPreferencesManager = new UserPreferencesManager(_context);
                            accessToken = mDBApi.getSession().getOAuth2AccessToken();
                            Utils.log(TAG, "Dropbox access token: " + accessToken);
                            userPreferencesManager.setTokenDB(accessToken);
                            Utils.log(TAG, "userPreferencesManager access token: " + userPreferencesManager.getTokenDB());

                        } catch (IllegalStateException e) {
                            Log.i("DbAuthLog", "Error authenticating", e);
                        }

                    }
                    customHandler.postDelayed(this, 10000);
                }else {
                    customHandler = null;
                }

            }
        }
    };

    public void uploadFiles(){

        new Upload().execute();

    }
    /*
DropboxAPI.Entry response = null;
                            try {
                                response = mDBApi.createFolder("/Downloads/");
                                response = mDBApi.createFolder("/Gallery/");
                            } catch (DropboxException e) {
                                e.printStackTrace();
                            }
 */

    /**
     *  Asynchronous method to upload any file to dropbox
     */
    public class Upload extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){ }

        protected String doInBackground(String... arg0) {
            DropboxAPI.Entry response = null;

            try {

                String path = Environment.getExternalStorageDirectory().toString() + _context.getResources().getString(R.string.download_path);
                Log.d("FILES", "Path: " + path);

                UserPreferencesManager userPreferencesManager = new UserPreferencesManager(_context);
                if(!userPreferencesManager.isFiles()){

                    try {
                        response = mDBApi.createFolder("/Downloads/");
                        response = mDBApi.createFolder("/Gallery/");
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                    userPreferencesManager.setFiles(true);
                }

                File file = new File(path);
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++)                {
                    Log.d("FILES", "FileName:" + files[i].getName());
                    FileInputStream inputStream = new FileInputStream(files[i]);
                    if (!files[i].isDirectory()) {
                        response = mDBApi.putFile("/Downloads/" + files[i].getName(), inputStream,
                                files[i].length(), null, null);
                        Log.e("FILES", "The uploaded file's rev is: " + response.rev);
                    }
                }

                String path2 = Environment.getExternalStorageDirectory().toString() + _context.getResources().getString(R.string.galery_path);
                Log.d("FILES", "Path2: " + path2);
                File file2 = new File(path2);
                File files2[] = file2.listFiles();

                for (int i = 0; i < files2.length; i++)
                {
                    Log.d("FILES", "FileName:" + files2[i].getName());
                    FileInputStream inputStream = new FileInputStream(files2[i]);
                    if (!files2[i].isDirectory()) {

                        response = mDBApi.putFile("Gallery/" + files2[i].getName(), inputStream,
                                files2[i].length(), null, null);
                        Log.e("FILES", "The uploaded file's rev is: " + response.rev);
                    }
                }


            } catch (Exception e){

                e.printStackTrace();
            }
            assert response != null;
            return response.rev;
        }



        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()){
                Utils.log("DbExampleLog", "The uploaded file's rev is: " + result);
            }
        }
    }

}
