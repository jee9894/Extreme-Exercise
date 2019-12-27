package com.example.hyun.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class JoinActivity extends AppCompatActivity{
    EditText mjoinId;
    EditText mjoinPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mjoinId = (EditText)findViewById(R.id.joinId);
        mjoinPwd = (EditText)findViewById(R.id.joinPwd);
    }

    public void onJoinButtonClicked(View v) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while (child.hasNext()){

                    if (child.next().getKey().equals(mjoinId.getText().toString())) {
                        Toast.makeText(JoinActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }
                        myRef.child(mjoinId.getText().toString()).child("pwd").setValue(mjoinPwd.getText().toString());

                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat CurDateFormat = new SimpleDateFormat("가입일 : yyyy년 MM월 dd일 hh시 mm분");

                        String strCurDate = CurDateFormat.format(date);
                        myRef.child(mjoinId.getText().toString()).child("date").setValue(strCurDate);

                        Toast.makeText(JoinActivity.this, "회원가입 완료!", Toast.LENGTH_SHORT).show();
                        Intent intent11 = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent11);

                        finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
