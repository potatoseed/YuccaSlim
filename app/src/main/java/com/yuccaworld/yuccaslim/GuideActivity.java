package com.yuccaworld.yuccaslim;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        FloatingActionButton fab = findViewById(R.id.floatingActionButtonGuide);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean b = sharedPreferences.getBoolean(getString(R.string.key_show_guide_on_start),true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent todayIntent = new Intent(GuideActivity.this, TodayActivity.class);
                startActivity(todayIntent);
            }
        });
    }
}
