package com.software.ing.jaradtracking.Activities;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import com.software.ing.jaradtracking.interfaces.ChangeListener;
import com.software.ing.jaradtracking.services.GPService;
import com.software.ing.jaradtracking.services.InformationRecoveryService;
import com.software.ing.jaradtracking.services.PanicService;
//import com.software.ing.jaradtracking.services.SocketService;
import com.software.ing.jaradtracking.services.TimerService;
import com.software.ing.jaradtracking.utils.FilesUploaderManager;
import com.software.ing.jaradtracking.utils.SocketManager;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RedButtonActivity extends AppCompatActivity implements View.OnClickListener{

    @InjectView(R.id.bloqueo)
    FloatingActionButton bloqueo;
    @InjectView(R.id.mensajes)
    FloatingActionButton mensajes;
    @InjectView(R.id.aQuien)
    FloatingActionButton aQuien;
    @InjectView(R.id.config)
    FloatingActionMenu config;
    @InjectView(R.id.redButton)
    ImageView redButton;
    UserPreferencesManager userPreferencesManager;
    boolean activedService = false;
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    public static DevicePolicyManager devicePolicyManager;
    public static ComponentName demoDeviceAdmin;


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
        //pide permisos para el administrador de dispositivos
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, demoDeviceAdmin);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "concedenos el permiso para poder bloquear y desbloquear tu dispositivo");
        startActivityForResult(intent, ACTIVATION_REQUEST);

        bloqueo.setOnClickListener(this);
        mensajes.setOnClickListener(this);
        aQuien.setOnClickListener(this);
        redButton.setOnClickListener(this);



        redButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String msg = " ";
                if(!userPreferencesManager.isPanicoPress()){
                    userPreferencesManager.setPanicoPress(true);
                    startPressPanicService();
                    msg = "Boton de panico activo";
                }else{
                    userPreferencesManager.setPanicoPress(false);
                    stopPressPanicService();
                    msg = "Boton de panico inactivo";
                }
                return false;
            }
        });

        startService(new Intent(this, GPService.class));
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

            case R.id.redButton:
                String msg = " ";
                if(!userPreferencesManager.isPanico()){
                    startPanicService();
                    msg = "Boton de panico activo";
                }else{
                    stopPanicService();
                    msg = "Boton de panico inactivo";
                }
                devicePolicyManager.lockNow();
                Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_LONG).show();
                break;


        }
    }


    private void startPanicService()    {
        activedService = true;
        userPreferencesManager.setPanico(activedService);
        SocketManager.panicButtonOn(activedService);
        startService(new Intent(this, PanicService.class));
        startService(new Intent(this, InformationRecoveryService.class));
    }
    private void stopPanicService() {
        activedService = false;
        userPreferencesManager.setPanico(activedService);
        SocketManager.panicButtonOn(activedService);
        stopService(new Intent(this, PanicService.class));
        stopService(new Intent(this, InformationRecoveryService.class));
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
