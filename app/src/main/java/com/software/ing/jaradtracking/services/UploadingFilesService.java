package com.software.ing.jaradtracking.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.interfaces.ChangeListener;
import com.software.ing.jaradtracking.utils.FilesUploaderManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by Raelyx on 06/08/2016.
 */
public class UploadingFilesService extends Service {

    DropboxAPI.Entry responseDownloads;
    DropboxAPI.Entry responsePhotos;
    public static File panicPhoto;
    public static DropboxAPI.Entry responsePhotosPanic;
    public static DropboxAPI<AndroidAuthSession> mDBApi = null;
    public static UserPreferencesManager session;
    FilesUploaderManager filesUploaderManager;
    public static String TAG = "UploadingFilesService";
    public static boolean uploadingStatus = false;


    @Override
    public IBinder onBind(Intent intent) {return null;}

    public static void setmDBApi(DropboxAPI<AndroidAuthSession> mDBApiM) {
        mDBApi = mDBApiM;
    }

    public static DropboxAPI<AndroidAuthSession> getmDBApi() {
        return mDBApi;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        session = new UserPreferencesManager(this);
        filesUploaderManager = new FilesUploaderManager(this);
        mDBApi = null;

        Utils.log(TAG, "onCreate");
        Utils.log(TAG, "dropbox token: " + session.getTokenDB());
        filesUploaderManager.initialize_session(session.getTokenDB());

        filesUploaderManager.setmDBAPIChangeListener(new ChangeListener() {
            @Override
            public void onChange(DropboxAPI<AndroidAuthSession> mDBAPIHasChanged) {
                Utils.log(TAG, "cambió mDBApi");

                uploadingStatus = true;
                uploadPhotos();
                uploadDownload();
            }
        });
    }



    public void uploadDownload(){
        Utils.log(TAG,"trying to upload files");
        new UploadFiles().execute();
    }

    public void uploadPhotos(){
        Utils.log(TAG,"trying to upload photos");
        new UploadPhotos().execute();
    }

    public static void uploadPanicPhotos(){
        new UploadPhotosPanic().execute();
    }

    public static void setPanicPhoto(File panicPhoto) {
        UploadingFilesService.panicPhoto = panicPhoto;
    }

    public class UploadPhotos extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... arg0) {
            responsePhotos = null;

            try {
                if(!session.isFilesPhotos()){
                    try {
                        responsePhotos = mDBApi.createFolder("/Gallery/");
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                    session.setFilesPhotos(true);
                }

                String path = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.galery_path);
                Utils.log(TAG, "Path galery: " + path);
                File file = new File(path);
                File files[] = file.listFiles();

                for (int i = 0; i < files.length; i++)
                {
                    Utils.log(TAG, "FileName:" + files[i].getName());
                    FileInputStream inputStream = new FileInputStream(files[i]);
                    if (!files[i].isDirectory()) {
                        responsePhotos = mDBApi.putFileOverwrite("Gallery/" +files[i].getName(),inputStream,files[i].length(),null);
                        Utils.log(TAG, "The uploaded photo's rev is: " + responsePhotos.rev);

                    }
                }

            } catch (Exception e){
                e.printStackTrace();
            }

            return responsePhotos.rev;
        }


        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()){
                Utils.log("DbExampleLog", "The uploaded photo's rev is: " + result);
            }
        }

    }

    public static class UploadPhotosPanic extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... arg0) {
            responsePhotosPanic = null;

            try {
                if(!session.isFilesPanic()){
                    try {
                        responsePhotosPanic = mDBApi.createFolder("/Panic Photos/");
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                    session.setFilesPanic(true);
                }


                String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Jarad Tracking/";
                Utils.log(TAG, "Path jarad: " + path);
                File file = new File(path);
                File files[] = file.listFiles();

                for (int i = 0; i < files.length; i++)
                {
                    Utils.log(TAG, "FileName:" + files[i].getName());
                    FileInputStream inputStream = new FileInputStream(files[i]);
                    if (!files[i].isDirectory()) {
                        responsePhotosPanic = mDBApi.putFileOverwrite("Panic Photos/" +files[i].getName(),inputStream,files[i].length(),null);
                        Utils.log(TAG, "The uploaded panic photo's rev is: " + responsePhotosPanic.rev);

                    }
                }


//
//                FileInputStream inputStream = new FileInputStream(panicPhoto);
//
//                //put the file to dropbox
//                Utils.log(TAG, "panicPhoto"+ panicPhoto.getName());
//                responsePhotosPanic = mDBApi.putFile(panicPhoto.getName(), inputStream,
//                        panicPhoto.length(), null, null);
//                Utils.log(TAG, "The uploaded panic photo's rev is: " + responsePhotosPanic.rev);

            } catch (Exception e){
                e.printStackTrace();
            }

            return responsePhotosPanic.rev;
        }


        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()){
                Utils.log("DbExampleLog", "The uploaded panic photo's rev is: " + result);
            }
        }

    }

    public class UploadFiles extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... arg0) {
            responseDownloads = null;

            try {
                if(!session.isFilesDownloads()){
                    try {
                        responseDownloads = mDBApi.createFolder("/Downloads/");
                    } catch (DropboxException e) {
                        e.printStackTrace();
                    }
                    session.setFilesDownloads(true);
                }


                try {
                    String path = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.download_path);
                    Utils.log(TAG, "Path downloads: " + path);
                    File file = new File(path);
                    File files[] = file.listFiles();


                    for (int i = 0; i < files.length; i++)                {
                        Utils.log(TAG, "FileName:" + files[i].getName());
                        FileInputStream inputStream = new FileInputStream(files[i]);
                        if (!files[i].isDirectory()) {
                            responseDownloads = mDBApi.putFileOverwrite("/Downloads/" + files[i].getName(), inputStream,
                                    files[i].length(), null);
                            Utils.log(TAG, "The uploaded file's rev is: " + responseDownloads.rev);


                        }
                    }
                }catch (Exception e){      e.printStackTrace();                }


            } catch (Exception e){
                e.printStackTrace();
            }

            return responseDownloads.rev;
        }

        @Override
        protected void onPostExecute(String result) {
            if(!result.isEmpty()){
                Utils.log("DbExampleLog", "The uploaded file's rev is: " + result);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadingStatus = false;
    }
}
