package com.software.ing.jaradtracking.Activities;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.fragments.AquienDialogFragment;
import com.software.ing.jaradtracking.fragments.BloqueoDialogFragment;
import com.software.ing.jaradtracking.fragments.MensajesDialogFragment;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_button);
        ButterKnife.inject(this);

        bloqueo.setOnClickListener(this);
        mensajes.setOnClickListener(this);
        aQuien.setOnClickListener(this);
        redButton.setOnClickListener(this);


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
                Toast.makeText(getApplicationContext(), "RedButtonClicked",Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void startPanicService()
    {
//        startService(new Intent(this, PanicService.class));
    }
    private void stopPanicService() {
//        stopService(new Intent(this, PanicService.class));
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
