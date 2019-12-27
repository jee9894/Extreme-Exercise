package com.example.hyun.myapplication;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class ArmActivity extends AppCompatActivity {
    Adapter_arm adapter;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arm);
        viewPager = (ViewPager) findViewById(R.id.view);
        adapter = new Adapter_arm(this);
        viewPager.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                Intent intent1 = getIntent();
                String id = intent1.getExtras().getString("id");
                String kind = intent1.getExtras().getString("kind");
                String set = intent1.getExtras().getString("set");

                Intent intent2 = new Intent(ArmActivity.this, SettingActivity.class);
                intent2.putExtra("id", id);
                intent2.putExtra("kind", kind);
                intent2.putExtra("set", set);

                startActivity(intent2);

                Toast.makeText(getApplicationContext(), "기본설정", Toast.LENGTH_LONG).show();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBicepsButtonClicked(View v) {
        Intent intent3 = getIntent();
        String id = intent3.getExtras().getString("id");
        String kind = intent3.getExtras().getString("kind");
        String set = intent3.getExtras().getString("set");
        String nps = intent3.getExtras().getString("nps");
        String tpot = intent3.getExtras().getString("tpot");

        Intent intent4 = new Intent(getApplicationContext(), MainActivity.class);
        intent4.putExtra("id", id);
        intent4.putExtra("kind", kind);
        intent4.putExtra("set", set);
        intent4.putExtra("nps", nps);
        intent4.putExtra("tpot", tpot);
        intent4.putExtra("detail", "biceps");//이두

        startActivity(intent4);
    }

    public void onBrachialButtonClicked(View v) {
        Intent intent3 = getIntent();
        String id = intent3.getExtras().getString("id");
        String kind = intent3.getExtras().getString("kind");
        String set = intent3.getExtras().getString("set");
        String nps = intent3.getExtras().getString("nps");
        String tpot = intent3.getExtras().getString("tpot");

        Intent intent4 = new Intent(getApplicationContext(), MainActivity.class);
        intent4.putExtra("id", id);
        intent4.putExtra("kind", kind);
        intent4.putExtra("set", set);
        intent4.putExtra("nps", nps);
        intent4.putExtra("tpot", tpot);
        intent4.putExtra("detail", "brachial");//측면

        startActivity(intent4);
    }
}