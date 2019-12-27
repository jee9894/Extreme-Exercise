package com.example.hyun.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static java.lang.Thread.sleep;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Intent intent5 = getIntent();
                String id = intent5.getExtras().getString("id");
                String kind = intent5.getExtras().getString("kind");
                String set = intent5.getExtras().getString("set");
                String detail = intent5.getExtras().getString("detail");

                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("user").child(id).child("kind").child(kind).child(detail).child("Ex"+set);

                myRef.child("correction").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                        while (child.hasNext()) {
                            if(child.next().getKey().equals("correction")) {
                                String correct = dataSnapshot.child("correction").getValue().toString();

                                if (correct.equals("yes")) {
                                    return;
                                }
                            }
                            break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Intent intent6 = new Intent(getApplicationContext(), ResultActivity.class);
                intent6.putExtra("id", id);
                intent6.putExtra("kind", kind);
                intent6.putExtra("set", set);
                intent6.putExtra("detail", detail);

                startActivity(intent6);
                finish();
            }
        };
        handler.sendEmptyMessageDelayed(0, 1000);
    }
}
