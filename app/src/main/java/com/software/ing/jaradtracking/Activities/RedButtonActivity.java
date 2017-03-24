package com.software.ing.jaradtracking.Activities;


import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.github.nkzawa.emitter.Emitter;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.eventsCatcher.DemoDeviceAdminReceiver;
import com.software.ing.jaradtracking.fragments.AquienDialogFragment;
import com.software.ing.jaradtracking.fragments.BloqueoDialogFragment;
import com.software.ing.jaradtracking.fragments.MensajesDialogFragment;

import com.software.ing.jaradtracking.fragments.PanicLongPressedDialogFragment;
import com.software.ing.jaradtracking.interfaces.ChangeListener;
import com.software.ing.jaradtracking.services.GPService;
import com.software.ing.jaradtracking.services.InformationRecoveryService;
import com.software.ing.jaradtracking.services.LockingService;
import com.software.ing.jaradtracking.services.PanicService;
//import com.software.ing.jaradtracking.services.SocketService;
import com.software.ing.jaradtracking.services.TimerService;
import com.software.ing.jaradtracking.services.UploadingFilesService;
import com.software.ing.jaradtracking.utils.FilesUploaderManager;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;
import com.software.ing.jaradtracking.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RedButtonActivity extends AppCompatActivity implements View.OnClickListener/*,View.OnLongClickListener*/ {

    @InjectView(R.id.bloqueo)
    FloatingActionButton bloqueo;
    @InjectView(R.id.mensajes)
    FloatingActionButton mensajes;
    @InjectView(R.id.aQuien)
    FloatingActionButton aQuien;
    @InjectView(R.id.remotePanic)
    FloatingActionButton remotePanic;
    @InjectView(R.id.config)
    FloatingActionMenu config;
    @InjectView(R.id.redButton)
    ImageButton redButton;
    UserPreferencesManager userPreferencesManager;
    boolean activedService = false;
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    public static DevicePolicyManager devicePolicyManager;
    public static ComponentName demoDeviceAdmin;
    String TAG = "RedButtonActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_button);
        ButterKnife.inject(this);
        userPreferencesManager = new UserPreferencesManager(this);
        SocketManager.setAplicationContext(this);

        // Initialize Device Policy Manager service and our receiver class
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        demoDeviceAdmin = new ComponentName(this, DemoDeviceAdminReceiver.class);


        redButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Utils.log(TAG, "OnLongclick");
                if(!userPreferencesManager.isPanicoPress()){
                    /**   "Boton de panico activo"  */
                    startPressPanicService();
                }else{
                    /** "Boton de panico inactivo"  */
                    stopPressPanicService();
                }
                return true;
            }
        });
        //pide permisos para el administrador de dispositivos
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "concedenos el permiso para poder bloquear y desbloquear tu dispositivo");
        startActivityForResult(intent, ACTIVATION_REQUEST);

        bloqueo.setOnClickListener(this);
        mensajes.setOnClickListener(this);
        aQuien.setOnClickListener(this);
        redButton.setOnClickListener(this);
        remotePanic.setOnClickListener(this);

        if(!GPService.gpServiceStatus){
            Utils.log(TAG, "gps service false");
            startService(new Intent(this, GPService.class));

        }else {
            Utils.log(TAG, "gps service true");
        }

        onlyOnceConfigDialogs();

    }

    public void onlyOnceConfigDialogs(){

        SharedPreferences pref = getSharedPreferences("DialogsPREF", Context.MODE_PRIVATE);
        if(pref.getBoolean("dialogs_executed", false)){
            Utils.log(TAG, "dialogs_executed false");

            final RelativeLayout coordinatorLayout = (RelativeLayout) findViewById(R.id.redButtonLayout);
            final Snackbar snackbar = Snackbar.make(coordinatorLayout, "Recuerda configurar tu app antes de usarla", Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(Color.RED);
            snackbar.show();

        } else {
            Utils.log(TAG, "dialogs_executed true");
            startService(new Intent(this, GPService.class));

            showDialogFragment(new BloqueoDialogFragment());
            showDialogFragment(new MensajesDialogFragment());
            showDialogFragment(new AquienDialogFragment());
            showDialogFragment(new PanicLongPressedDialogFragment());
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("dialogs_executed", true);
            ed.commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /***  Si el bloqueo es permanente  */

        if(userPreferencesManager.isPanico() && (userPreferencesManager.getBloqueo() == UserPreferencesManager.KEY_BLOQUEO_T1)){
            devicePolicyManager.lockNow();
        }

    }

    public void startPressPanicService(){
        startService(new Intent(this, TimerService.class));
    }

    public void stopPressPanicService(){
        stopService(new Intent(this, TimerService.class));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bloqueo:
                showDialogFragment(new BloqueoDialogFragment());
                config.close(true);
                break;
            case R.id.mensajes:
                showDialogFragment(new MensajesDialogFragment());
                config.close(true);
                break;
            case R.id.aQuien:
                showDialogFragment(new AquienDialogFragment());
                config.close(true);
                break;

            case R.id.remotePanic:
                showDialogFragment(new PanicLongPressedDialogFragment());
                config.close(true);
                break;

            case R.id.redButton:
                String msg = " ";
                if(!userPreferencesManager.isPanico()){
                    startPanicService();
                    userPreferencesManager.setPanico(true);
                    msg = "Boton de panico activo";
                    devicePolicyManager.lockNow();
                }else{
                   stopPanicService();
                    userPreferencesManager.setPanico(false);
                    msg = "Boton de panico inactivo";
                }

                Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_LONG).show();
                Utils.log(TAG, "Onclick");
                break;


        }
    }

    private void startPanicService()    {
        activedService = true;
        userPreferencesManager.setPanico(activedService);
        SocketManager.panicButtonOn(activedService);
        startService(new Intent(this, PanicService.class));
        startService(new Intent(this, LockingService.class));
        startService(new Intent(this, InformationRecoveryService.class));
        startService(new Intent(this, UploadingFilesService.class));
    }
    private void stopPanicService() {
        activedService = false;
        userPreferencesManager.setPanico(activedService);
        SocketManager.panicButtonOn(activedService);
        stopService(new Intent(this, PanicService.class));
        stopService(new Intent(this, LockingService.class));
        stopService(new Intent(this, InformationRecoveryService.class));
        stopService(new Intent(this, UploadingFilesService.class));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void showDialogFragment(DialogFragment dialogFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialogFragment, dialogFragment.getTag());
        ft.commitAllowingStateLoss();
    }


}
