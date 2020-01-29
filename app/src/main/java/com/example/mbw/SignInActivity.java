package com.example.mbw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {

    TextView signUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signUp = findViewById(R.id.signUpView);
    }
    public void onSignInClick(View v){
        //id, passwd 서버와 일치하면 main으로 넘어가게
        //틀렸으면 오류메세지 띄우고 다시 로그인하게
        //Intent intent = null;
        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void onSignUpClick(View v){
        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_LONG).show();
        //클릭되면 signup_activity로 넘어가게
    }
}