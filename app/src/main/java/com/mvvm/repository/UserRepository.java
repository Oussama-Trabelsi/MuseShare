package com.mvvm.repository;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.retrofit.UserApi;
import com.mvvm.util.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

public class UserRepository
{
    private static UserRepository instance;
    private UserApi userApi;
    List<User> mUserList;
    boolean result = false;

    public static UserRepository getInstance()
    {
        if(instance == null) instance = new UserRepository();
        return instance;
    }

    public UserRepository() {
        mUserList = new ArrayList<User>();
        userApi = RetrofitClient.getInstance().getUserApi();
    }

    /* Elastic search for users */
    public List<User> searchUsers(String keyword)
    {
        Call<ResponseBody> call = userApi.searchUsers(keyword);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            mUserList = Parser.parseUserList(jsonArray);
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
        return mUserList;
    }


    /* get follows */
    public List<User> getUserFollowers(int user_id)
    {
        final MutableLiveData<List<User>> mlist = new MutableLiveData<>();
        Call<ResponseBody> call = userApi.getUserFollows(user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.getJSONArray("followers").length()>0)
                        {
                            mlist.setValue(Parser.parseUserList(jsonObject.getJSONArray("followers")));
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
        return mlist.getValue();
    }

    public List<User> getUserFollowing(int user_id)
    {
        Call<ResponseBody> call = userApi.getUserFollows(user_id);
        final MutableLiveData<List<User>> mlist = new MutableLiveData<>();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.getJSONArray("following").length()>0)
                        {
                            mlist.setValue(Parser.parseUserList(jsonObject.getJSONArray("following")));
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });
        return mlist.getValue();
    }

    /* Update user info */
    public boolean updateUser(int id, String username, String firstName,
                              String lastName, String email, String bio,
                              boolean isPrivate){
        Call<ResponseBody> call = userApi.updateUser(id, username, firstName, lastName, email, bio, isPrivate);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 204) result = true;
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });

        return result;
    }

    /* Update user profile photo */
    private boolean updateImageUrl(int id, String imageUrl){
        result = false;
        Call<ResponseBody> call = userApi.updateImageUrl(id, imageUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 204) result = true;
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });

        return result;
    }

    public boolean uploadProfilePhoto(MultipartBody.Part body)
    {
        result = false;
        Call<ResponseBody> call = userApi.uploadPhoto(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        updateImageUrl(Session.getInstance().getUser().getId(), jsonObject.getString("filename"));
                            result = true;
                            Session.getInstance().getUser().setImage_url(jsonObject.getString("filename"));

                    }
                    catch (IOException e)
                    {
                    } catch (JSONException e) {

                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
        return result;
    }

    public void followUser(int follow_id)
    {
        Call<ResponseBody> call = userApi.followUser(Session.getInstance().getUser().getId(), follow_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 204) {

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void unfollowUser(int follow_id)
    {
        Call<ResponseBody> call = userApi.unfollowUser(Session.getInstance().getUser().getId(), follow_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.code() == 204) {

                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

}
