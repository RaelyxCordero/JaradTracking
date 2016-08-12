package com.software.ing.jaradtracking.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.software.ing.jaradtracking.Activities.RegisterActivity;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.interfaces.ChangeListener;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;

/**
 * Created by Raelyx on 2/7/2016.
 */
public class GCMRegistrationIntentService extends IntentService {
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
    public static final String REGISTRATION_ERROR = "RegistrationError";

    public GCMRegistrationIntentService() {
        super("");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        registerGCM();
    }

    private void registerGCM() {
        Intent registrationComplete = null;
        String token = null;
        try {
            InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
            token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            UserPreferencesManager session = new UserPreferencesManager(getApplicationContext());
//            session.setVariableChangeListener(new ChangeListener() {
//                @Override
//                public void onChange(String tokenThatHasChanged) {
//                    Log.e("TOKENONCHANGE", tokenThatHasChanged);
////                    startService(new Intent(getApplicationContext(), SocketService.class));
//                }
//            });
            session.setToken(token);
            Log.w("GCMRegIntentService", "token:" + token);
            //notify to UI that registration complete success
            registrationComplete = new Intent(REGISTRATION_SUCCESS);
            registrationComplete.putExtra("token", token);
        } catch (Exception e) {
            Log.w("GCMRegIntentService", "Registration error");
            registrationComplete = new Intent(REGISTRATION_ERROR);
        }
        //Send broadcast
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }
}
