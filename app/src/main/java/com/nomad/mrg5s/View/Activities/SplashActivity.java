package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.nomad.mrg5s.BuildConfig;
import com.nomad.mrg5s.R;


public class SplashActivity extends AppCompatActivity {

    //DURACION DE DE LA ESPERA


    @Override
    protected void onCreate(Bundle splash) {
        super.onCreate(splash);
        setContentView(R.layout.activity_splash);
        TextView version=findViewById(R.id.versionApp);
        version.setText(BuildConfig.VERSION_NAME);

        int SPLASH_DISPLAY_LENGHT = 2000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

             // avisarPruebas();
                irALogin();

            }
        }, SPLASH_DISPLAY_LENGHT);

    }
    private void avisarPruebas() {
        new MaterialDialog.Builder(SplashActivity.this)
                .title(SplashActivity.this.getString(R.string.advertencia))
                .buttonsGravity(GravityEnum.CENTER)
                .cancelable(false)
                .contentColor(ContextCompat.getColor(SplashActivity.this, R.color.primary_text))
                .backgroundColor(ContextCompat.getColor(SplashActivity.this, R.color.tile1))
                .titleColor(ContextCompat.getColor(SplashActivity.this, R.color.tile4))
                .content(getResources().getString(R.string.versionDePrueba))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        irALogin();
                    }
                })
                .show();
    }

    private void irALogin(){
        Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
        SplashActivity.this.startActivity(mainIntent);
        SplashActivity.this.finish();
    }


}
