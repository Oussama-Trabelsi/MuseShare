package com.haerul.swipeviewpager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Callback;


import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    EditText username;
    EditText password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = findViewById(R.id.username_login);
        password = findViewById(R.id.password_login);


        findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    public void loginAction(View view) {
        Call<ResponseBody> call = RetrofitClient
                .getInstance()
                .getUserApi()
                .login(username.getText().toString(),
                        password.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if(response.code() == 200) {
                    Toast.makeText(LoginActivity.this, "Login success !", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        User u = new User(jsonObject.getJSONObject("user").getInt("user_id"),
                                jsonObject.getJSONObject("user").getString("username"),
                                jsonObject.getJSONObject("user").getString("user_firstName"),
                                jsonObject.getJSONObject("user").getString("user_lastName"),
                                jsonObject.getJSONObject("user").getString("user_email"),
                                jsonObject.getJSONObject("user").getString("user_password"),
                                jsonObject.getJSONObject("user").getString("role"),
                                jsonObject.getJSONObject("user").getString("user_bio"),
                                jsonObject.getJSONObject("user").getInt("followersCount"),
                                jsonObject.getJSONObject("user").getInt("followingCount"),
                                jsonObject.getJSONObject("user").getString("profileImgUrl"),
                                jsonObject.getJSONObject("user").getBoolean("isPrivate"),
                                Parser.parseUserList(jsonObject.getJSONObject("user").getJSONArray("following")),
                                jsonObject.getJSONObject("user").getInt("postsCount"));
                        // add user to session and move to next activity
                        Session.getInstance().setUser(u);
                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);

                    }
                    catch (IOException e)
                    {

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Error : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
