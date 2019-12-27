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

import java.util.Iterator;

public class LoginActivity extends AppCompatActivity {
    EditText mEditId;
    EditText mEditPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditId = (EditText)findViewById(R.id.editId);
        mEditPwd = (EditText)findViewById(R.id.editPwd);
    }

    public void onLoginButtonClicked(View v) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("user");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> child = dataSnapshot.getChildren().iterator();

                while(child.hasNext()) {
                    if(child.next().getKey().equals(mEditId.getText().toString())) {
                        String id = dataSnapshot.child(mEditId.getText().toString()).child("pwd").getValue().toString();

                        if(id.equals(mEditPwd.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "로그인!", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            intent.putExtra("id", mEditId.getText().toString());
                            startActivity(intent);

                            return;
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "비밀번호 오류", Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }
                }
                Toast.makeText(getApplicationContext()," 존재하지 않는 아이디",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void onGoJoinButtonClicked(View v) {
        Intent intent10 = new Intent(this, JoinActivity.class);
        startActivity(intent10);
    }
}
