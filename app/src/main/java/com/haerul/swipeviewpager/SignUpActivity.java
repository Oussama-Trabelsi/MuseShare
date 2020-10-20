package com.haerul.swipeviewpager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mvvm.retrofit.RetrofitClient;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {
    EditText username;
    EditText firstName;
    EditText lastName;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.register_username);
        firstName = findViewById(R.id.register_firstName);
        lastName = findViewById(R.id.register_lastName);
        email = findViewById(R.id.register_email);
        password = findViewById(R.id.register_password);

        findViewById(R.id.back_to_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
    }

    public void RegisterAction(View view) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getUserApi()
                .createUser(username.getText().toString(),
                        password.getText().toString(),
                        email.getText().toString(),
                        firstName.getText().toString(),
                        lastName.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 201)
                    Toast.makeText(SignUpActivity.this, "Register success !", Toast.LENGTH_LONG).show();
                else {
                    try {
                        Toast.makeText(SignUpActivity.this, response.body().string(), Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Error : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
