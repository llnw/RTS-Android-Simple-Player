package com.llnwrts.app;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import org.jetbrains.annotations.NotNull;

public class ConfigActivity extends AppCompatActivity {
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        setDefaults();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setDefaults() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        EditText hostField = findViewById(R.id.hostField);
        hostField.setText(getPref(getString(R.string.host)));
        hostField.addTextChangedListener(generateTextWatcher(getString(R.string.host)));

        EditText appField = findViewById(R.id.appField);
        appField.setText(getPref(getString(R.string.app)));
        appField.addTextChangedListener(generateTextWatcher(getString(R.string.app)));

        EditText streamNameField = findViewById(R.id.streamNameField);
        streamNameField.setText(getPref(getString(R.string.streamName)));
        streamNameField.addTextChangedListener(generateTextWatcher(getString(R.string.streamName)));

        EditText mvUrlField = findViewById(R.id.mvUrlField);
        mvUrlField.setText(getPref(getString(R.string.mvUrl)));
        mvUrlField.addTextChangedListener(generateTextWatcher(getString(R.string.mvUrl)));
    }

    @NotNull
    private TextWatcher generateTextWatcher(String key) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            public void afterTextChanged(Editable s) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(key, s.toString());
                editor.apply();
            }
        };
    }

    private String getPref(String key) {
        return sharedPref.getString(key, "");
    }
}