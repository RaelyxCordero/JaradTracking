package com.software.ing.jaradtracking.Activities;



import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
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
import permissions.dispatcher.PermissionUtils;

import com.software.ing.jaradtracking.interfaces.ChangeListener;
import com.software.ing.jaradtracking.utils.FilesUploaderManager;
import com.software.ing.jaradtracking.utils.PermissionsDispatcher;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;



public class RegisterActivity extends AppCompatActivity {
    @InjectView(R.id.username)
    EditText user;
    @InjectView(R.id.telefono)
    EditText telefono;
    @InjectView(R.id.registro)
    Button registrar;
    SocketManager socketManager;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    public static UserPreferencesManager preferencesManager;
    String TAG = "REGISTERACT";
    public static boolean dbAuth;
    boolean socket = false;
    FilesUploaderManager filesUploaderManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ButterKnife.inject(this);
        socketManager = SocketManager.newInstance(this);
        preferencesManager = new UserPreferencesManager(getApplicationContext());
        filesUploaderManager = new FilesUploaderManager(getApplicationContext());
        gcmIssues();
        onlyOnceActivity();
        dbAuth = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsDispatcher.onRequestPermissionsResult(RegisterActivity.this, requestCode, grantResults);

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 1234);

    }

    public void onlyOnceActivity(){
        SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("activity_executed", false)){
            Utils.log(TAG, "activity_executed false");
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
            public void onReceive(final Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    Utils.log(TAG, "token gcm: "+ intent.getStringExtra("token"));


                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    Utils.log(TAG, "gcm registration error");
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utils.log(TAG, "onResume" );
        PermissionsDispatcher.showDialogPermissions(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));


    }

    public void init(){
        //SE REGISTRA Y AUTENTICA EN DROPBOX
        if ( (PermissionUtils.hasSelfPermissions(this, PermissionsDispatcher.PERMISSIONS))
                && !preferencesManager.getToken().equals("")
                && !socket){
            SocketManager.startSocket();
            socket = true;
        }

        if(preferencesManager.getTokenDB() == null){
            if(!dbAuth){
                filesUploaderManager.initialize_session(null);
                dbAuth = true;
            }
        }


        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(user.getText().toString().equals("")) && !(telefono.getText().toString().equals(""))){
                    //REGISTRAR USUARIO
                    if(registerUser(user.getText().toString(), telefono.getText().toString())){
                        Utils.log(TAG, "REGISTRO USUARIO");
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Campos Vacios", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public boolean registerUser(String name, String tlf){

        JSONObject registerUser = new JSONObject();
        try {
            registerUser.put("nombre", name);
            registerUser.put("telefono", tlf);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Utils.log(TAG,"" + registerUser );
        Utils.log(TAG,"" + "llamando socket manager" );
        SocketManager.emitRegister(registerUser);

        startActivity(new Intent(getApplicationContext(), RedButtonActivity.class));


        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
