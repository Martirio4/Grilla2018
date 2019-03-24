package com.nomad.mrg5s;
import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

import io.realm.Realm;

public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

       Realm.init(this);
       FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}