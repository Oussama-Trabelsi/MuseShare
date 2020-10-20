package com.mvvm.retrofit;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface PostApi {

    @GET("post")
    Call<ResponseBody> getAllPosts();

    @GET("post/{post_id}")
    Call<ResponseBody> getPostLikes(@Path("post_id") int post_id);

    @GET("post/find/{user_id}")
    Call<ResponseBody> getAllPostsForUser(@Path("user_id") int user_id);

    // like a post
    @POST("post/like/{id}/{user_id}")
    Call<ResponseBody> likePost(
            @Path("id") int id, @Path("user_id") int user_id
    );

    // dislike a post
    @POST("post/dislike/{id}/{user_id}")
    Call<ResponseBody> dislikePost(
            @Path("id") int id, @Path("user_id") int user_id
    );

    // add a comment to a post
    @FormUrlEncoded
    @POST("comment")
    Call<ResponseBody> commentPost(
            @Field("content") String content,
            @Field("user_id") int user_id,
            @Field("post_id") int post_id
    );

    // fetch comments to a specific post
    @GET("comment/find/{post_id}")
    Call<ResponseBody> getPostComments(@Path("post_id") int post_id);

    @Multipart
    @POST("post/upload")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file
    );

}
