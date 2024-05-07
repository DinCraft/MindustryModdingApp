package com.dincraft.test;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dincraft.test.utils.Colorable;

public class About extends AppCompatActivity implements Colorable {
    private TextView[] textViews;
    private Data[] data;
    private ScrollView scrollView;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setData();
        createInterface();
        setColorTheme();
        setContentView(scrollView);
    }

    private void setData(){
        data = new Data[2];
        for (int i = 0; i < data.length; i++) {
            data[i] = new Data();
        }
        data[0].title = "About";
        data[0].content = "This app was designed by DinCraft to help you with modding for Mindustry.";
        data[1].title = "Contribution";
        data[1].content = "There are 3 ways of contribution to this project: making new tutorials, suggesting changes to existing ones or translating them. If you want to contribute send me message on this address: idiniyar2006@mail.ru";
    }

    private void createInterface(){
        scrollView = new ScrollView(this);
        linearLayout = new LinearLayout(this);
        scrollView.addView(linearLayout);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        textViews = new TextView[data.length*2];
        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = new TextView(this);
            textViews[i].setPadding(10,10,10,10);
            if (i%2==0){
                textViews[i].setTextSize(32);
                textViews[i].setText(data[i/2].title);
            }else {
                textViews[i].setTextSize(28);
                textViews[i].setText(data[i/2].content);
            }
            linearLayout.addView(textViews[i]);
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
    public void setColorTheme() {
        SharedPreferences preferences = getSharedPreferences(Settings.PreferencesData.NAME, MODE_PRIVATE);
        if (!preferences.getString(Settings.PreferencesData.THEME,"").equals("")){
            if (preferences.getString(Settings.PreferencesData.THEME,"").equals(Settings.PreferencesData.Theme.LIGHT)) {
                for (int i = 0; i < textViews.length; i++) {
                    textViews[i].setBackgroundColor(0xFFFFFFFF);
                    textViews[i].setTextColor(0xFF202020);
                }
            }else {
                for (int i = 0; i < textViews.length; i++) {
                    textViews[i].setBackgroundColor(0xFF202020);
                    textViews[i].setTextColor(0xFFFFFFFF);
                }
            }
        }
    }

    private class Data {
        private String title;
        private String content;
    }
}
