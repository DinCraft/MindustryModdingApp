package com.dincraft.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dincraft.test.utils.LessonData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Lessons extends InternetActivity {
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private TextView[] textViews;
    private ArrayList<LessonData> lessons;

    private void createInterface(){
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 10, 20, 10);
        textViews = new TextView[lessons.size()];
        for (int i = 0; i < textViews.length; i++) {
            textViews[i] = new TextView(this);
            textViews[i].setPadding(0,0,0,15);
            textViews[i].setText(lessons.get(i).getName());
            textViews[i].setOnClickListener(getListener(lessons.get(i).getRelativeLink()));
            textViews[i].setTextSize(32);
            linearLayout.addView(textViews[i]);
        }
        scrollView = new ScrollView(this);
        scrollView.addView(linearLayout);
        setContentView(scrollView);
    }

    public View.OnClickListener getListener(String s){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(getApplicationContext(), Lesson.class);
                Bundle b = new Bundle();
                b.putString("link", s);
                intent.putExtras(b);
                startActivity(intent);
            }
        };
    }

    public void load(){
        lessons = new ArrayList<>();
        SharedPreferences settings = getSharedPreferences(Settings.PreferencesData.NAME, MODE_PRIVATE);
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String fileData = "";
                try {
                    String langId = settings.getString(Settings.PreferencesData.LANGUAGE.toString(),"en");
                    String fileUrl = "https://raw.githubusercontent.com/DinCraft/MindustryModdingGuide/main/"+langId+".txt";
                    URL url = new URL(fileUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append("\n");
                        }
                        reader.close();
                        fileData = stringBuilder.toString();
                    } else {
                        Log.e("DownloadFileTask", "Failed to download file. Response Code: " + responseCode);
                        return "";
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    return "";
                }
                return fileData;
            }

            @Override
            protected void onPostExecute(String content) {
                if (!content.equals("")){
                    String[] lessonsData = content.split("\n");
                    for (int i = 0; i < lessonsData.length; i++) {
                        String[] ld = lessonsData[i].split(";");
                        lessons.add(new LessonData(ld[0], ld[1]));
                    }
                    createInterface();
                    setColorTheme();
                }
            }
        };
        task.execute();
    }

    @Override
    public void setColorTheme() {
        super.setColorTheme();
        if (!connected){
            return;
        }
        SharedPreferences settings = getSharedPreferences(Settings.PreferencesData.NAME, MODE_PRIVATE);
        if (!settings.getString(Settings.PreferencesData.THEME,"").equals("")){
            if (settings.getString(Settings.PreferencesData.THEME,"").equals(Settings.PreferencesData.Theme.LIGHT)) {
                linearLayout.setBackgroundColor(0xFFFFFFFF);
                scrollView.setBackgroundColor(0xFFFFFFFF);
                for (int i = 0; i < textViews.length; i++) {
                    textViews[i].setTextColor(0xFF202020);
                }
            }else {
                linearLayout.setBackgroundColor(0xFF202020);
                scrollView.setBackgroundColor(0xFF202020);
                for (int i = 0; i < textViews.length; i++) {
                    textViews[i].setTextColor(0xFFFFFFFF);
                }
            }
        }
    }

    @Override
    public void networkAvailable() {
        super.networkAvailable();
        load();
    }
}