package com.dincraft.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dincraft.test.utils.Colorable;
import com.dincraft.test.utils.LessonData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Settings extends InternetActivity {
    private SharedPreferences settings;
    private SharedPreferences.Editor prefEditor;
    private LinearLayout linearLayout;
    private TextView setColorTheme, setLanguage;
    private RadioGroup colorThemeGroup, languagesGroup;
    private RadioButton light, dark;
    private ArrayList<PreferencesData.Language> languagesList;
    private ArrayList<RadioButton> languages;
    private Button apply;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getSharedPreferences(PreferencesData.NAME, MODE_PRIVATE);
        prefEditor = settings.edit();
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void createInterface(){
        linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        setColorTheme = new TextView(this);
        setColorTheme.setTextSize(32);
        setColorTheme.setText("Theme");
        linearLayout.addView(setColorTheme);
        colorThemeGroup = new RadioGroup(this);
        light = new RadioButton(this);
        light.setText("Light");
        colorThemeGroup.addView(light);
        dark = new RadioButton(this);
        dark.setText("Dark");
        colorThemeGroup.addView(dark);

        colorThemeGroup.setOnCheckedChangeListener((group, id)->{
            RadioButton button = findViewById(group.getCheckedRadioButtonId());
            if (button!=null){
                if (button.getText().equals("Light")){
                    prefEditor.putString(PreferencesData.THEME, PreferencesData.Theme.LIGHT);
                }else {
                    prefEditor.putString(PreferencesData.THEME, PreferencesData.Theme.DARK);
                }
            }
        });
        linearLayout.addView(colorThemeGroup);

        setLanguage = new TextView(this);
        setLanguage.setTextSize(32);
        setLanguage.setText("Language");
        linearLayout.addView(setLanguage);
        if (languagesList!=null){
            languagesGroup = new RadioGroup(this);
            languages = new ArrayList<>();
            String lang = settings.getString(PreferencesData.LANGUAGE,"English");
            for (PreferencesData.Language language: languagesList){
                if (language.ID.equals(settings.getString(PreferencesData.LANGUAGE,"en"))){
                    lang = language.SHOWN_NAME;
                }
            }
            for (PreferencesData.Language language: languagesList){
                RadioButton langBtn = new RadioButton(this);
                langBtn.setText(language.SHOWN_NAME);
                languagesGroup.addView(langBtn);
                languages.add(langBtn);
                System.out.println(1);
                if (langBtn.getText().equals(lang)){
                    langBtn.setChecked(true);
                }
            }

            languagesGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton button = findViewById(group.getCheckedRadioButtonId());
                    if (button!=null){
                        for (PreferencesData.Language lang: languagesList){
                            if (button.getText().equals(lang.SHOWN_NAME)){
                                prefEditor.putString(PreferencesData.LANGUAGE, lang.ID);
                            }
                        }
                    }
                }
            });
            linearLayout.addView(languagesGroup);
        }


        apply = new Button(this);
        apply.setText("Apply");
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefEditor.apply();
                finish();
                Intent intent = new Intent(getApplicationContext(), Main.class);
                startActivity(intent);
            }
        });
        linearLayout.addView(apply);
    }

    private void load(){
        @SuppressLint("StaticFieldLeak") AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String result = "";
                try {
                    String fileUrl = "https://raw.githubusercontent.com/DinCraft/MindustryModdingGuide/main/languages.txt";
                    URL url = new URL(fileUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append("\n");
                        }
                        result = stringBuilder.toString();
                        reader.close();
                    } else {
                        Log.e("DownloadFileTask", "Failed to download file. Response Code: " + responseCode);
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String fileContent) {
                if (!fileContent.equals("")){
                    languagesList = new ArrayList<>();
                    String[] lines = fileContent.split("\n");
                    for (String line: lines){
                        String[] langData = line.split(";");
                        languagesList.add((new PreferencesData()).new Language(langData[0], langData[1]));
                        System.out.println(langData[0]+" "+langData[1]);
                    }
                }
                createInterface();
                setColorTheme();
                setContentView(linearLayout);
            }
        };
        task.execute();
    }

    @Override
    protected void networkConnectionLayout() {

    }

    @Override
    public void setColorTheme(){
        if (!settings.getString(PreferencesData.THEME,"").equals("")){
            if (settings.getString(PreferencesData.THEME,"").equals(PreferencesData.Theme.LIGHT)) {
                linearLayout.setBackgroundColor(0xFFFFFFFF);
                setColorTheme.setBackgroundColor(0xFFFFFFFF);
                setColorTheme.setTextColor(0xFF202020);
                setLanguage.setBackgroundColor(0xFFFFFFFF);
                setLanguage.setTextColor(0xFF202020);
                light.setTextColor(0xFF202020);
                dark.setTextColor(0xFF202020);
                apply.setBackgroundColor(0xFF202020);
                apply.setTextColor(0xFFFFFFFF);
                if (languages!=null){
                    for(RadioButton button: languages){
                        button.setTextColor(0xFF202020);
                    }
                }
                light.setChecked(true);
            }else {
                linearLayout.setBackgroundColor(0xFF202020);
                setColorTheme.setBackgroundColor(0xFF202020);
                setColorTheme.setTextColor(0xFFFFFFFF);
                setLanguage.setBackgroundColor(0xFF202020);
                setLanguage.setTextColor(0xFFFFFFFF);
                light.setTextColor(0xFFFFFFFF);
                dark.setTextColor(0xFFFFFFFF);
                apply.setBackgroundColor(0xFFFFFFFF);
                apply.setTextColor(0xFF202020);
                if (languages!=null){
                    for(RadioButton button: languages){
                        button.setTextColor(0xFFFFFFFF);
                    }
                }
                dark.setChecked(true);
            }
        }
    }

    public class PreferencesData {
        public static final String NAME = "settings";
        public static final String THEME = "theme";
        public static final String LANGUAGE = "language";
        public class Theme {
            static final String LIGHT = "light";
            static final String DARK = "dark";
        }
        public class Language {
            final String SHOWN_NAME;
            final String ID;
            Language(String ID, String SHOWN_NAME){
                this.SHOWN_NAME = SHOWN_NAME;
                this.ID = ID;
            }
        }
    }

    @Override
    public void networkAvailable() {
        super.networkAvailable();
        load();
    }
}
