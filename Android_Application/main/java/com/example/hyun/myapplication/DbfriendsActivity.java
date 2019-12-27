package com.example.hyun.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

public class DbfriendsActivity extends AppCompatActivity {
    EditText mEditDbFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dbfriends);

        mEditDbFriends = (EditText)findViewById(R.id.editDbFriends);
    }

    public void onDbFriendsButtonClicked(View v) {
        Intent intent2 = getIntent();
        String id = intent2.getExtras().getString("id");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user");

        myRef.child(id).child("friends").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    if(child.next().getKey().equals(mEditDbFriends.getText().toString())) {

                        Intent intent3 = new Intent(getApplicationContext(), DbActivity.class);
                        intent3.putExtra("id", mEditDbFriends.getText().toString());

                        String friends_id = mEditDbFriends.getText().toString();
                        Toast.makeText(getApplicationContext(), friends_id + "의 운동정보를 참조합니다.", Toast.LENGTH_LONG).show();

                        startActivity(intent3);
                        finish();
                    }

                    else
                        Toast.makeText(getApplicationContext(),"해당 아이디의 친구가 없습니다.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}