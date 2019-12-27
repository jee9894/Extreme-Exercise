package com.example.hyun.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    EditText mEditTime;
    EditText mEditSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mEditSet = (EditText)findViewById(R.id.editSet);
        mEditTime = (EditText)findViewById(R.id.editTime);
    }

    public void onNpsButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.question1: {
                Intent intent2 = getIntent();
                String id = intent2.getExtras().getString("id");
                String kind = intent2.getExtras().getString("kind");
                String set = intent2.getExtras().getString("set");

                Intent intentNps = new Intent(SettingActivity.this, NpsActivity.class);
                intentNps.putExtra("id", id);
                intentNps.putExtra("kind", kind);
                intentNps.putExtra("set", set);

                startActivity(intentNps);

                break;
            }
        }
    }

    public void onTpotButtonClicked(View v) {
        switch (v.getId()) {
            case R.id.question2: {
                Intent intent2 = getIntent();
                String id = intent2.getExtras().getString("id");
                String kind = intent2.getExtras().getString("kind");
                String set = intent2.getExtras().getString("set");

                Intent intentTpot = new Intent(SettingActivity.this, TpotActivity.class);
                intentTpot.putExtra("id", id);
                intentTpot.putExtra("kind", kind);
                intentTpot.putExtra("set", set);

                startActivity(intentTpot);

                break;
            }
        }
    }

    public void onSettingClicked(View v) {
        switch (v.getId()) {
            case R.id.btnSend: {
                Intent intent2 = getIntent();
                String id = intent2.getExtras().getString("id");
                String kind = intent2.getExtras().getString("kind");
                String set = intent2.getExtras().getString("set");

                if (mEditSet.getText().toString().equals("") || mEditTime.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "미입력 란이 존재합니다!", Toast.LENGTH_LONG).show();
                }

                else {
                    Toast.makeText(getApplicationContext(), "기본설정을 완료했습니다.", Toast.LENGTH_LONG).show();

                    Intent intent3 = new Intent(SettingActivity.this, PushActivity.class);
                    intent3.putExtra("id", id);
                    intent3.putExtra("kind", kind);
                    intent3.putExtra("set", set);
                    intent3.putExtra("nps", mEditSet.getText().toString());
                    intent3.putExtra("tpot", mEditTime.getText().toString());

                    startActivity(intent3);
                    finish();

                    break;
                }
            }
        }
    }
}
