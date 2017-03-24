package com.software.ing.jaradtracking.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
import com.software.ing.jaradtracking.services.UploadingFilesService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by Raelyx on 27/7/2016.
 */

public class FilesUploaderManager {

    public static DropboxAPI<AndroidAuthSession> mDBApi;
    public Handler customHandler;
    public String APP_KEY;
    public String APP_SECRET;
    public Context _context;
    public String accessToken = null;
    public String TAG = "FilesUploaderManager";
    ChangeListener changeListener;

    public FilesUploaderManager(Context context){
        _context = context;
        APP_KEY = context.getResources().getString(R.string.DropboxAppKey);
        APP_SECRET = context.getResources().getString(R.string.DropboxAppSecret);

    }

    public DropboxAPI<AndroidAuthSession> getmDBApi() {
        return mDBApi;
    }

    public void initialize_session(String token){

        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);

        if (token != null) {
            session.setOAuth2AccessToken(token);
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            Utils.log(TAG, "token recibido: " + token);
            UploadingFilesService.setmDBApi(mDBApi);

        }else {
            mDBApi = new DropboxAPI<AndroidAuthSession>(session);
            mDBApi.getSession().startOAuth2Authentication(_context);
            customHandler = new Handler();
            customHandler.postDelayed(updateTimerThread, 10000);
        }
    }

    public void setmDBAPIChangeListener(ChangeListener variableChangeListener) {
        this.changeListener = variableChangeListener;
        if( UploadingFilesService.getmDBApi() != null) {
            Utils.log(TAG, "mDBApi cambió ");
            this.changeListener.onChange(UploadingFilesService.getmDBApi());
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
                    customHandler.postDelayed(updateTimerThread, 10000);
                }else {
                    customHandler = null;
                }

            }
        }
    };


}
