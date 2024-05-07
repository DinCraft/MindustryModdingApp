package com.dincraft.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Lesson extends InternetActivity {
    private String link;
    private String content;
    private LinearLayout linearLayout;
    private ScrollView scrollView;
    private ArrayList<TextView> textViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        link = getIntent().getExtras().getString("link");
    }

    private void loadContent(){
        SharedPreferences settings = getSharedPreferences(Settings.PreferencesData.NAME, MODE_PRIVATE);
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    String langId = settings.getString(Settings.PreferencesData.LANGUAGE.toString(),"en");
                    String fileUrl = "https://raw.githubusercontent.com/DinCraft/MindustryModdingGuide/main/"+link+"/"+langId+".json";
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
                        content = stringBuilder.toString();
                    } else {
                        Log.e("DownloadFileTask", "Failed to download file. Response Code: " + responseCode);
                        return false;
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean success) {
                textViews = new ArrayList<>();
                if (success){
                    addContent();
                    setColorTheme();
                }
            }
        };
        task.execute();
    }

    private void createInterface(){
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 10, 20, 10);
        scrollView = new ScrollView(this);
        scrollView.addView(linearLayout);
    }

    private void addContent(){
        try {
            JSONObject json = new JSONObject(content);
            if (json.has("tags")) {
                JSONArray tags = json.getJSONArray("tags");
                for (int i = 0; i < tags.length(); i++) {
                    JSONObject object = tags.getJSONObject(i);
                    if (object.has("type")) {
                        String type = object.getString("type");
                        if (type.equals("text")) {
                            if (object.has("text")) {
                                addText(object.getString("text"));
                                System.out.println("text");
                            }
                        }else if(type.equals("image")){
                            if (object.has("link")) {
                                addImage(object.getString("link"));
                                System.out.println("image");
                            }
                        }else if(type.equals("link")){
                            if (object.has("text") & object.has("link")) {
                                addLink(object.getString("text"),object.getString("link"));
                                System.out.println("link");
                            }
                        }
                    }
                }
            }else{
                System.out.println("\"tags\" key is missing!");
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void addImage(String name){
        ImageView imageView = new ImageView(this);
        HorizontalScrollView imageScroll = new HorizontalScrollView(this);
        Picasso.get().load("https://raw.githubusercontent.com/DinCraft/MindustryModdingGuide/main/"+link+"/"+name).into(imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageScroll.addView(imageView);
        imageScroll.setPadding(0,0,0,10);
        linearLayout.addView(imageScroll);
    }

    private void addText(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(32);
        textView.setPadding(5,0,5,10);
        textViews.add(textView);
        linearLayout.addView(textView);
    }

    private void addLink(String text, String link){
        TextView textView = new TextView(this);
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);
        textView.setTextSize(32);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(myIntent);
            }
        });
        textView.setPadding(5,0,5,10);
        textViews.add(textView);
        linearLayout.addView(textView);
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
                for (TextView textView: textViews) {
                    textView.setTextColor(0xFF202020);
                }
            }else {
                linearLayout.setBackgroundColor(0xFF202020);
                scrollView.setBackgroundColor(0xFF202020);
                for (TextView textView: textViews) {
                    textView.setTextColor(0xFFFFFFFF);
                }
            }
        }
    }

    @Override
    public void networkAvailable() {
        super.networkAvailable();
        loadContent();
        createInterface();
        setContentView(scrollView);
    }
}
