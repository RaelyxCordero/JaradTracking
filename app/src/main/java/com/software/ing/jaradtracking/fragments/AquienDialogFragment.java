package com.software.ing.jaradtracking.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.software.ing.jaradtracking.R;
import com.software.ing.jaradtracking.utils.UserPreferencesManager;

import java.util.List;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.ButterKnife;
import butterknife.InjectView;
//import uneg.software.sebu.utils.UserPreferencesManager;


public class AquienDialogFragment extends DialogFragment implements View.OnClickListener, Validator.ValidationListener {
    public static final String TAG = AquienDialogFragment.class.getSimpleName();
    @InjectView(R.id.aQuienMensaje)
    EditText aQuienMensaje;
    @InjectView(R.id.aQuienCorreo)
    EditText aQuienCorreo;
    @InjectView(R.id.guardarConfigAquien)
    Button guardarConfigAquien;
    private Validator validator;
    UserPreferencesManager session;


    public Dialog onCreateDialog(Bundle savedInstanceState)    {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.Animation_AppCompat_DropDownUp;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config_aquien, null);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        ButterKnife.inject(this, view);
        session=new UserPreferencesManager(getActivity());

        aQuienMensaje.setText(session.getTelefono());
        aQuienCorreo.setText(session.getCorreo());
        guardarConfigAquien.setOnClickListener(this);
        validator = new Validator(this);
        validator.setValidationListener(this);
        return view;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.guardarConfigAquien:
                session.setCorreo(aQuienCorreo.getText().toString());
                session.setTelefono(aQuienMensaje.getText().toString());
                this.dismiss();
                break;
        }
    }



    @Override
    public void onValidationSucceeded() {
        session.setCorreo(aQuienCorreo.getText().toString());
        session.setTelefono(aQuienMensaje.getText().toString());
        this.dismiss();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }


}