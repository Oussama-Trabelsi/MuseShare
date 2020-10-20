package com.mvvm.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.mvvm.model.Comment;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.PostApi;
import com.mvvm.retrofit.RetrofitClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostRepository {
    private static PostRepository instance;
    private PostApi postApi;

    public static PostRepository getInstance()
    {
        if(instance == null) instance = new PostRepository();
        return instance;
    }

    public PostRepository() {
        postApi = RetrofitClient.getInstance().getPostApi();
    }

    public MutableLiveData<List<Post>> getFollingPosts() {
        final MutableLiveData<List<Post>> postsMutableLiveData = new MutableLiveData<>();

        Call<ResponseBody> call = postApi.getAllPosts();
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            try {
                                postsMutableLiveData.setValue(Parser.parsePostsList(jsonArray));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return postsMutableLiveData;
    }

    public MutableLiveData<List<Post>> getUserPosts(int user_id) {
        final MutableLiveData<List<Post>> postsMutableLiveData = new MutableLiveData<>();

        Call<ResponseBody> call = postApi.getAllPostsForUser(user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            try {
                                postsMutableLiveData.setValue(Parser.parsePostsList(jsonArray));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return postsMutableLiveData;
    }

    public void likePost(Post p, User u)
    {
        Call<ResponseBody> call = RetrofitClient.getInstance().getPostApi().likePost(p.getId(), u.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void dilikePost(Post p, User u)
    {
        Call<ResponseBody> call = postApi.dislikePost(p.getId(), u.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 204) {
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public void commentPost(Comment comment, Post post)
    {
        Call<ResponseBody> call = postApi.commentPost(comment.getContent(), comment.getUser().getId(), post.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 204) {
                }

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    public MutableLiveData<List<Comment>> getPostComments(Post post) {
        final MutableLiveData<List<Comment>> commentsMutableLiveData = new MutableLiveData<>();

        Call<ResponseBody> call = postApi.getPostComments(post.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            try {
                                commentsMutableLiveData.setValue(Parser.parseCommentList(jsonArray));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return commentsMutableLiveData;
    }






}
