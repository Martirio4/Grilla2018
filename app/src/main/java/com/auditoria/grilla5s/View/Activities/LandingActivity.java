package com.auditoria.grilla5s.View.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.auditoria.grilla5s.R;

import io.realm.Realm;

public class LandingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Realm.init(getApplicationContext());
    }
}
