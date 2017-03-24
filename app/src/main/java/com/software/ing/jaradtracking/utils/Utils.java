package com.software.ing.jaradtracking.utils;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Utils {
    public static String fileToBase64(File file) {
        String strFile = null;
        InputStream is;
        try {
            is = new FileInputStream(file);
            byte[] data = IOUtils.toByteArray(is);//Convert any file, image or video into byte array
            is.close();
            strFile = Base64.encodeToString(data, Base64.NO_WRAP);//Convert byte array into string
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strFile;
    }


    public static long getIntervalMinute(String intervalo) {
        return Long.valueOf(intervalo)*60000; /** minuto*/
    }

    public static long getIntervalHour(String intervalo) {
        return Long.valueOf(intervalo)*3600000; /** hora*/
    }

    public static void log (String TAG, String msj){
        Log.e("TULOG" + TAG, msj);
    }
}
