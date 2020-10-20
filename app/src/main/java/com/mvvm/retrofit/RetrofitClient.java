package com.mvvm.retrofit;

import com.mvvm.util.Common;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String BASE_URL = Common.BASE_URL;
    private static RetrofitClient mInstance;
    private Retrofit retrofit;

    private RetrofitClient()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(){
        if(mInstance == null){
            mInstance = new RetrofitClient();
        }
        return mInstance;
    }

    public UserApi getUserApi(){
        return retrofit.create(UserApi.class);
    }
    public PostApi getPostApi() { return retrofit.create(PostApi.class); }
    public ChatApi getChatApi() { return retrofit.create(ChatApi.class); }
}
