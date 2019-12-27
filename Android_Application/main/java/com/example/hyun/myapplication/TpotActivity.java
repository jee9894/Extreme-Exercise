package com.example.hyun.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class TpotActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tpot);
    }

    public void onOk1ButtonClicked(View v) {
        Intent intentTpot = getIntent();
        String id = intentTpot.getExtras().getString("id");
        String kind = intentTpot.getExtras().getString("kind");
        String set = intentTpot.getExtras().getString("set");

        Intent intent2 = new Intent(TpotActivity.this, SettingActivity.class);
        intent2.putExtra("id", id);
        intent2.putExtra("kind", kind);
        intent2.putExtra("set", set);

        startActivity(intent2);

        finish();
    }
}
