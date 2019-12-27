package com.example.hyun.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class DbSelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbsel);
    }

    public void onMyButtonClicked(View v) {
        Intent intent1 = getIntent();
        String id = intent1.getExtras().getString("id");

        Intent intent3 = new Intent(this, DbActivity.class);
        intent3.putExtra("id", id);
        startActivity(intent3);
    }

    public void onFriendsButtonClicked(View v) {
        Intent intent1 = getIntent();
        String id = intent1.getExtras().getString("id");

        Intent intent2 = new Intent(this, DbfriendsActivity.class);
        intent2.putExtra("id", id);
        startActivity(intent2);
    }
}
