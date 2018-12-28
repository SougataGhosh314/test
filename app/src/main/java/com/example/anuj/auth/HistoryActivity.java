package com.example.anuj.auth;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.anuj.auth.SessionManagement.ServiceManager;

public class HistoryActivity extends AppCompatActivityExt {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //Utils.darkenStatusBar(this, R.color.colorPrimary);
    }


}