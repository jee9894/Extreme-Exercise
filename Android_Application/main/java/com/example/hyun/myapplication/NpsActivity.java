package com.example.hyun.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class NpsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nps);
    }

    public void onOkButtonClicked(View v) {
        Intent intentNps = getIntent();
        String id = intentNps.getExtras().getString("id");
        String kind = intentNps.getExtras().getString("kind");
        String set = intentNps.getExtras().getString("set");

        Intent intent2 = new Intent(NpsActivity.this, SettingActivity.class);
        intent2.putExtra("id", id);
        intent2.putExtra("kind", kind);
        intent2.putExtra("set", set);

        startActivity(intent2);

        finish();
    }
}
