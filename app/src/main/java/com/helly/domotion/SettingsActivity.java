package com.helly.domotion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private EditText EditUrl;
    private EditText EditUser;
    private CheckBox CBPassword;
    private EditText EditTimeout;
    private CheckBox CBLogScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getIds();
        getSettings();
        addListeners();
    }

    private void getIds() {
        EditUrl = findViewById(R.id.editSettingsUrl);
        EditUser = findViewById(R.id.editSettingsUser);
        CBPassword = findViewById(R.id.checkBoxSettingsPassword);
        EditTimeout = findViewById(R.id.editSettingsTimeout);
        CBLogScroll = findViewById(R.id.checkBoxSettingsLogScroll);

        sharedPref = getApplication().getSharedPreferences(getString(R.string.shared_pref),Context.MODE_PRIVATE);
    }

    @SuppressLint("SetTextI18n")
    private void getSettings() {
        String SettingsUrl = sharedPref.getString(getString(R.string.saved_url), getString(R.string.settings_default_url));
        String SettingsUser = sharedPref.getString(getString(R.string.saved_user), getString(R.string.settings_default_user));
        boolean SettingsPassword = sharedPref.getBoolean(getString(R.string.saved_password), true);
        int SettingsTimeout = sharedPref.getInt(getString(R.string.saved_timeout), Integer.parseInt(getString(R.string.settings_default_timeout)));
        boolean SettingsLogScroll = sharedPref.getBoolean(getString(R.string.saved_log_scroll), true);

        EditUrl.setText(SettingsUrl);
        EditUser.setText(SettingsUser);
        CBPassword.setChecked(SettingsPassword);
        EditTimeout.setText(Integer.toString(SettingsTimeout));
        CBLogScroll.setChecked(SettingsLogScroll);
    }

    private void addListeners() {
        EditUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_url), editable.toString());
                editor.apply();
            }
        });

        EditUser.addTextChangedListener((new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.saved_user), editable.toString());
                editor.apply();
            }
        }));

        CBPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.saved_password), b);
                editor.apply();
            }
        });

        EditTimeout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.saved_timeout), Integer.parseInt(editable.toString()));
                editor.apply();
            }
        });

        CBLogScroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.saved_log_scroll), b);
                editor.apply();
            }
        });
    }

}
