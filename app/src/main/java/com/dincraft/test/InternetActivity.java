package com.dincraft.test;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dincraft.test.utils.Colorable;
import com.dincraft.test.utils.NetworkStateReceiver;

public abstract class InternetActivity extends AppCompatActivity implements Colorable, NetworkStateReceiver.NetworkStateReceiverListener {
    private TextView networkConnection;
    private LinearLayout linearLayout;
    protected boolean connected;
    protected NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connected = false;
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
    }

    protected void networkConnectionLayout(){
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 10, 20, 10);
        networkConnection = new TextView(this);
        networkConnection.setText("No Internet!");
        networkConnection.setTextSize(32);
        linearLayout.addView(networkConnection);
        setContentView(linearLayout);
        setColorTheme();
    }

    @Override
    public void setColorTheme() {
        if (!connected){
            SharedPreferences settings = getSharedPreferences(Settings.PreferencesData.NAME, MODE_PRIVATE);
            if (!settings.getString(Settings.PreferencesData.THEME,"").equals("")){
                if (settings.getString(Settings.PreferencesData.THEME,"").equals(Settings.PreferencesData.Theme.LIGHT)) {
                    linearLayout.setBackgroundColor(0xFFFFFFFF);
                    networkConnection.setTextColor(0xFF202020);
                }else {
                    linearLayout.setBackgroundColor(0xFF202020);
                    networkConnection.setTextColor(0xFFFFFFFF);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent intent = new Intent(getApplicationContext(), Main.class);
        startActivity(intent);
        super.onBackPressed();
    }

    @Override
    public void networkUnavailable() {
        if (!connected){
            networkConnectionLayout();
        }
    }

    @Override
    public void networkAvailable() {
        connected = true;
    }
}