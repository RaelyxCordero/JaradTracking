package com.software.ing.jaradtracking.Activities;



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
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.gcm.GCMRegistrationIntentService;

import butterknife.ButterKnife;
import butterknife.InjectView;
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
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.allRegister)
    ScrollView allRegister;
    @InjectView(R.id.progress)
    FrameLayout progress;
    SocketManager socketManager;
    String userDeviceToken;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    public static UserPreferencesManager preferencesManager;
    String TAG = "REGISTERACT";
    static String msg;
    public static Context _context;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.inject(this);
//        askManageOverlay();
        init();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permission, int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permission, grantResult);
        PermissionsDispatcher.onRequestPermissionsResult(RegisterActivity.this, requestCode, grantResult);

    }

    public void askManageOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {
                    finish();
                }else{
                    PermissionsDispatcher.showDialogPermissions(this);
                }
            }
        }
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
            public void onReceive(final Context context, Intent intent) {
                //Check type of intent filter
                if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
                    //Registration success
                    userDeviceToken = intent.getStringExtra("token");
                    preferencesManager.setToken(userDeviceToken);
                    preferencesManager.setVariableChangeListener(new ChangeListener() {
                        @Override
                        public void onChange(String tokenThatHasChanged) {
                            Log.e("TOKENONCHANGEregs", tokenThatHasChanged);
                            startSocketService();
                            progress.setVisibility(View.GONE);
                            allRegister.setVisibility(View.VISIBLE);

                        }
                    });

                    Toast.makeText(getApplicationContext(), "GCM token:" + userDeviceToken, Toast.LENGTH_LONG).show();
                } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
                    //Registration error
                    //Toast.makeText(getApplicationContext(), "GCM registration error!!!", Toast.LENGTH_LONG).show();
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

    private void startSocketService()    {
        Utils.log(TAG+"startsocket","ENTRO" );
        SocketManager.startSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Utils.log(TAG, "onResume" );


    }

    public void init(){
        socketManager = SocketManager.newInstance(this);
        preferencesManager = new UserPreferencesManager(getApplicationContext());
        gcmIssues();
        _context = getApplicationContext();
        allRegister.setVisibility(View.INVISIBLE);
        /////////////////////
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_SUCCESS));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(GCMRegistrationIntentService.REGISTRATION_ERROR));

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!(user.getText().toString().equals("")) && !(telefono.getText().toString().equals(""))){
                    JSONObject registerUser = new JSONObject();
                    try {
                        registerUser.put("nombre", user.getText().toString());
                        registerUser.put("telefono", telefono.getText().toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Utils.log(TAG,"" + registerUser );
                    Utils.log(TAG,"" + "llamando socket manager" );
                    SocketManager.emitRegister(registerUser); //REGISTRAR USUARIO
                        msg = "Registró Exitosamente";

                        //SE REGISTRA Y AUTENTICA EN DROPBOX
                        FilesUploaderManager filesUploaderManager = new FilesUploaderManager(getApplicationContext());
                        filesUploaderManager.initialize_session();
                        filesUploaderManager.iniciarHilo();
                        preferencesManager.setVariableChangeListener2(new ChangeListener() {
                            @Override
                            public void onChange(String tokenThatHasChanged) {
                                Utils.log(TAG, "tokenThatHasChanged: " + tokenThatHasChanged);
                                startActivity(new Intent(getApplicationContext(), RedButtonActivity.class));
                            }
                        });

                }else{
                    Toast.makeText(getApplicationContext(), "Campos Vacios", Toast.LENGTH_LONG).show();
                }


            }
        });

        onlyOnceActivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Utils.log(TAG,"" + "onPause" );
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
