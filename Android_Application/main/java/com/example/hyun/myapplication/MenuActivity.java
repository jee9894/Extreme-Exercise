package com.example.hyun.myapplication;

import android.content.Intent;
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

public class MenuActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_PUSH = 101;
    public static final int REQUEST_CODE_PULL = 102;
    public static final int REQUEST_CODE_DIPS = 103;
    public static final int REQUEST_CODE_ARM = 104;
    public static final int REQUEST_CODE_DB = 105;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_friends, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_friends:
                Intent intent = getIntent();
                String id = intent.getExtras().getString("id");

                Intent intent1 = new Intent(getApplicationContext(), FriendsActivity.class);
                intent1.putExtra("id", id);

                startActivity(intent1);

                Toast.makeText(this, "친구등록", Toast.LENGTH_SHORT).show();

                return true;

                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    public void onPushButtonClicked(View v) {
        Intent intent = getIntent();
        final String id = intent.getExtras().getString("id");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user").child(id).child("kind").child("push up");

            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 1;

                for(DataSnapshot postSnashot : dataSnapshot.getChildren()){
                    cnt++;
                }

                Intent intent1 = new Intent(getApplicationContext(), PushActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("kind", "push up");
                intent1.putExtra("set", Integer.toString(cnt-1));

                startActivityForResult(intent1, REQUEST_CODE_PUSH);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onPullButtonClicked(View v) {
        Intent intent = getIntent();
        final String id = intent.getExtras().getString("id");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user").child(id).child("kind").child("pull up");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 1;

                for(DataSnapshot postSnashot : dataSnapshot.getChildren()){
                    cnt++;
                }

                Intent intent1 = new Intent(getApplicationContext(), PullActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("kind", "pull up");
                intent1.putExtra("set", Integer.toString(cnt));

                startActivityForResult(intent1, REQUEST_CODE_PULL);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onDipsButtonClicked(View v) {
        Intent intent = getIntent();
        final String id = intent.getExtras().getString("id");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user").child(id).child("kind").child("dips");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 1;

                for(DataSnapshot postSnashot : dataSnapshot.getChildren()){
                    cnt++;
                }

                Intent intent1 = new Intent(getApplicationContext(), DipsActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("kind", "dips");
                intent1.putExtra("set", Integer.toString(cnt));

                startActivityForResult(intent1, REQUEST_CODE_DIPS);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onArmButtonClicked(View v) {
        Intent intent = getIntent();
        final String id = intent.getExtras().getString("id");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user").child(id).child("kind").child("arm");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cnt = 1;

                for(DataSnapshot postSnashot : dataSnapshot.getChildren()){
                    cnt++;
                }

                Intent intent1 = new Intent(getApplicationContext(), ArmActivity.class);
                intent1.putExtra("id", id);
                intent1.putExtra("kind", "arm");

                intent1.putExtra("set", Integer.toString(cnt));

                startActivityForResult(intent1, REQUEST_CODE_ARM);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onDbButtonClicked(View v) {
        Intent intent = getIntent();
        String id = intent.getExtras().getString("id");

        Intent intent1 = new Intent(getApplicationContext(), DbSelActivity.class);
        intent1.putExtra("id", id);

        startActivityForResult(intent1, REQUEST_CODE_DB);
    }

}
