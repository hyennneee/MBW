package com.example.mbw.accountActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mbw.R;
import com.example.mbw.accountData.SignUpData;
import com.example.mbw.accountData.SignUpResponse;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText userName, email, password, confirmPasswd;
    ServiceApi service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        userName = findViewById(R.id.userName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPasswd = findViewById(R.id.confirmPasswd);
        service = RetrofitClient.getClient().create(ServiceApi.class);
    }
    public void onClickSignUp(View v){
        attemptSignIn();
    }

    public void attemptSignIn(){
        String strUserName = userName.getText().toString();
        String strEmail = email.getText().toString();
        String strPassWd = password.getText().toString();
        String strConfirmPassWd = confirmPasswd.getText().toString();

        boolean failed = false;
        View focusView = null;

        if( strUserName.isEmpty()){
            userName.setError("사용자 이름을 입력하세요");
            focusView = userName;
            failed = true;
        }
        if(strEmail.isEmpty()){
            email.setError("이메일을 입력하세요");
            if(focusView == null)
                focusView = email;
            failed = true;
        }
        else if(!isEmailValid(strEmail)){
            email.setError("유효한 이메일 형식을 입력하세요");
        }
        if (strPassWd.isEmpty()) {
            password.setError("비밀번호를 입력하세요.");
            if(focusView == null)
                focusView = password;
            failed = true;
        }
        if(strConfirmPassWd.isEmpty()){
            confirmPasswd.setError("확인 비밀번호를 입력하세요");
            if(focusView == null)
                focusView = confirmPasswd;
            failed = true;
        }
        else if(!strConfirmPassWd.equals(strPassWd)){
            confirmPasswd.setError("비밀번호가 일치하지 않습니다");
            if(focusView == null)
                focusView = confirmPasswd;
            failed = true;
        }
        if(failed)
            focusView.requestFocus();
        else{
            startSignUp(new SignUpData(strEmail, strUserName, strPassWd));
        }
    }

    private void startSignUp(SignUpData data) {
        service.userSignUp(data).enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                SignUpResponse result = response.body();
                if(response.isSuccessful()) {
                    if (result.getCode() == 200) {  //회원가입 성공
                        Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                        startActivity(intent);
                    }
                }
                else{   //회원가입 실패

                    int code = 0;
                    Gson gson = new Gson();
                    try {
                        SignUpResponse errorResult = gson.fromJson(response.errorBody().string(), SignUpResponse.class);
                        code = errorResult.getCode();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(code == 409){
                        email.setError("이미 존재하는 계정입니다");
                        email.requestFocus();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "회원가입 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("회원가입 에러 발생", t.getMessage());
            }
        });
    }
    boolean isEmailValid(String email){
        if(email.contains("@"))
            return true;
        else
            return false;
    }
}