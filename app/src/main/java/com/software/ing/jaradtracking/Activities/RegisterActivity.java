package com.software.ing.jaradtracking.Activities;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.gcm.GCMRegistrationIntentService;

import butterknife.ButterKnife;
import butterknife.InjectView;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;


public class RegisterActivity extends AppCompatActivity {
    @InjectView(R.id.username)
    EditText user;
    @InjectView(R.id.telefono)
    EditText telefono;
    @InjectView(R.id.registro)
    Button registrar;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    Socket socket;
    String userDeviceToken;
    BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);

//        try{
//            socket = IO.socket(getString(R.string.ipServerSocket));
//        }catch(URISyntaxException e){
//            throw new RuntimeException(e);
//        }
//
//        socket.connect();
//        Log.w("SOCKET",""+ socket.connected());

        gcmIssues();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!(user.getText().toString().equals("")) && !(telefono.getText().toString().equals(""))){
//                    JSONObject registerUser = new JSONObject();
//                    try {
//                        registerUser.put("nombre", user.getText().toString());
//                        registerUser.put("telefono", telefono.getText().toString());
//                        registerUser.put("token", userDeviceToken);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    socket.emit("send:registro",registerUser);
//
//                    socket.on("new:device", new Emitter.Listener() {
//                        @Override
//                        public void call(Object... args) {
//                            Log.w("socketon",""+ args);
//                        }
//                    });
//                    Log.w("SOCKET", "" + registerUser);

                Intent tutorial = new Intent(getApplicationContext(), RedButtonActivity.class);
                startActivity(tutorial);
//                }else{
//                    Toast.makeText(getApplicationContext(), "Campos Vacios", Toast.LENGTH_LONG).show();
//                }


            }
        });

        //onlyOnceActivity();




    }

    public void onlyOnceActivity(){
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Intent intent = new Intent(this, RedButtonActivity.class);
            startActivity(intent);
            finish();
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }
    }

    public void gcmIssues(){
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    userDeviceToken = intent.getStringExtra("token");
                    //Toast.makeText(getApplicationContext(), "GCM token:" + userDeviceToken, Toast.LENGTH_LONG).show();
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    //Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
                } else {
                    //Tobe define
                }
            }
        };

        //Check status of Google play service in device
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if(ConnectionResult.SUCCESS != resultCode) {
            //Check type of error
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(getApplicationContext(), "Google Play Service is not install/enabled in this device!", Toast.LENGTH_LONG).show();
                //So notification
                GooglePlayServicesUtil.showErrorNotification(resultCode, getApplicationContext());
            } else {
                Toast.makeText(getApplicationContext(), "This device does not support for Google Play Service!", Toast.LENGTH_LONG).show();
            }
        } else {
            //Start service
            Intent itent = new Intent(this, GCMRegistrationIntentService.class);
            startService(itent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("RegisterActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("MainActivity", "onPause");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        socket.disconnect();
    }
}
