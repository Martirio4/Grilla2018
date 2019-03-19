package com.nomad.mrg5s.View.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

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

        int SPLASH_DISPLAY_LENGHT = 1500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

              Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
              SplashActivity.this.startActivity(mainIntent);
              SplashActivity.this.finish();

            }
        }, SPLASH_DISPLAY_LENGHT);

    }


}
