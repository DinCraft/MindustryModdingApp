package com.dincraft.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dincraft.test.utils.Colorable;

public class Main extends AppCompatActivity implements Colorable {
    private LinearLayout linearLayout;
    private Button lessons, about, settings, exit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createInterface();
    }

    private void createInterface(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,10,10,10);
        params.gravity = Gravity.CENTER;
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        lessons = new Button(this);
        lessons.setText("Lessons");

        lessons.setLayoutParams(params);
        lessons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), Lessons.class);
                startActivity(intent);
            }
        });
        linearLayout.addView(lessons);
        about = new Button(this);
        about.setText("About");
        about.setLayoutParams(params);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), About.class);
                startActivity(intent);
            }
        });
        linearLayout.addView(about);
        settings = new Button(this);
        settings.setText("Settings");
        settings.setLayoutParams(params);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });
        linearLayout.addView(settings);
        exit = new Button(this);
        exit.setText("Exit");
        exit.setLayoutParams(params);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });
        linearLayout.addView(exit);
        setColorTheme();
        setContentView(linearLayout);
    }

    @Override
    public void onBackPressed() {
        System.exit(0);
        super.onBackPressed();
    }

    @Override
    public void setColorTheme() {
        SharedPreferences preferences = getSharedPreferences(Settings.PreferencesData.NAME, MODE_PRIVATE);
        if (!preferences.getString(Settings.PreferencesData.THEME,"").equals("")){
            if (preferences.getString(Settings.PreferencesData.THEME,"").equals(Settings.PreferencesData.Theme.LIGHT)) {
                linearLayout.setBackgroundColor(0xFFFFFFFF);
                lessons.setBackgroundColor(0xFF202020);
                lessons.setTextColor(0xFFFFFFFF);
                about.setBackgroundColor(0xFF202020);
                about.setTextColor(0xFFFFFFFF);
                settings.setBackgroundColor(0xFF202020);
                settings.setTextColor(0xFFFFFFFF);
                exit.setBackgroundColor(0xFF202020);
                exit.setTextColor(0xFFFFFFFF);
            }else {
                linearLayout.setBackgroundColor(0xFF202020);
                lessons.setBackgroundColor(0xFFFFFFFF);
                lessons.setTextColor(0xFF202020);
                about.setBackgroundColor(0xFFFFFFFF);
                about.setTextColor(0xFF202020);
                settings.setBackgroundColor(0xFFFFFFFF);
                settings.setTextColor(0xFF202020);
                exit.setBackgroundColor(0xFFFFFFFF);
                exit.setTextColor(0xFF202020);
            }
        }
    }
}