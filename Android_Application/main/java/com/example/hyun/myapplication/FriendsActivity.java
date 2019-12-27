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

public class FriendsActivity extends AppCompatActivity {
    EditText mEditFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mEditFriends = (EditText)findViewById(R.id.editFriends);
    }

    public void onFriendsRegisterButtonClicked(View v) {
        switch(v.getId()) {
            case R.id.btnFriends: {
                Intent intent1 = getIntent();
                final String id = intent1.getExtras().getString("id");

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference("user");

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                        while(child.hasNext()) {
                            if (child.next().getKey().equals(mEditFriends.getText().toString())) {
                                myRef.child(id).child("friends").child(mEditFriends.getText().toString()).setValue(" ");

                                Toast.makeText(getApplicationContext(), "친구등록을 완료했습니다.", Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                                intent.putExtra("id", id);

                                startActivity(intent);
                                finish();

                                break;
                            }

                            else {
                                Toast.makeText(FriendsActivity.this, "해당 사용자가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                break;
            }
        }
    }
}
