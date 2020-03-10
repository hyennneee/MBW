package com.example.mbw.accountActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mbw.MainActivity;
import com.example.mbw.R;
import com.example.mbw.accountData.SignInData;
import com.example.mbw.accountData.SignInResponse;
import com.example.mbw.network.RetrofitClient;
import com.example.mbw.network.ServiceApi;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    ServiceApi service;
    TextView signUp;
    EditText email, passwd;
    String strEmail, strPasswd;
    String token = null, userName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        signUp = findViewById(R.id.signUpView);
        email = findViewById(R.id.signInEmail);
        passwd = findViewById(R.id.signInPassword);
        service = RetrofitClient.getClient().create(ServiceApi.class);
    }
    public void onClickSignIn(View v){
        //id, passwd 서버와 일치하면 main으로 넘어가게
        attemptSignIn();
    }

    public void attemptSignIn(){
        boolean failed = false;
        View focusView = null;
        strEmail = email.getText().toString();
        strPasswd = passwd.getText().toString();
        //틀렸으면 오류메세지 띄우고 다시 로그인하게
        if(strEmail.isEmpty()){
            email.setError("이메일을 입력하세요");
            failed = true;
            focusView = email;
        }
        else if(!isEmailValid(strEmail)){
            email.setError("유효한 이메일 형식을 입력하세요");
            failed = true;
            focusView = email;
        }
        if(strPasswd.isEmpty()){
            passwd.setError("비밀번호를 입력하세요");
            failed = true;
            if(focusView == null)
                focusView = passwd;
        }
        if(failed){
           focusView.requestFocus();
        }
        else{
            startSignIn(new SignInData(strEmail, strPasswd));
        }
    }

    public void onClickCreateAccount(View v){
        //클릭되면 signup_activity로 넘어가게
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }



    private void startSignIn(SignInData data) {
        service.userSignIn(data).enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                SignInResponse result = response.body();
                boolean success = response.isSuccessful();
                if(success) {
                    int code = result.getCode();

                    if (code == 200) {   //로그인 성공
                        JsonElement userData = result.getData();
                        String infoArr[] = new String[2];
                        try {
                            //JsonObject대신 JsonElement로
                            infoArr[0] = userData.getAsJsonObject().get("token").getAsString();
                            infoArr[1] = userData.getAsJsonObject().get("name").getAsString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                        intent.putExtra("USER_INFO", infoArr);
                        startActivity(intent);
                    }
                }
                else {
                    try {
                        setError(response);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                Toast.makeText(SignInActivity.this, "로그인 에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("로그인 에러 발생", t.getMessage());
            }
        });
    }

    //로그인 실패했을 경우(statusCode가 200이 아니면 데이터가 errorBody로 넘어감. 그래서 다시 데이터를 parsing 해야됨
    public void setError(Response<SignInResponse> response) throws IOException {
        Gson gson = new Gson();
        SignInResponse result = gson.fromJson(response.errorBody().string(), SignInResponse.class);
        int code = result.getCode();

        View focusView = null;
        if (code == 404) {   //존재하지 않는 계정
            focusView = email;
            email.setError(result.getMessage());
        }
        else if (code == 401) {    //비밀번호 불일치
            focusView = passwd;
            passwd.setError(result.getMessage());
        }
        else {   //그 외 오류들
            Toast.makeText(SignInActivity.this, result.getMessage(), Toast.LENGTH_SHORT).show();
        }

        focusView.requestFocus();
    }

    boolean isEmailValid(String email){
        if(email.contains("@"))
            return true;
        else
            return false;
    }
}
