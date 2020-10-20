package com.mvvm.retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface UserApi {
    // Sign up
    @FormUrlEncoded
    @POST("user")
    Call<ResponseBody> createUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("email") String email,
            @Field("firstname") String firstName,
            @Field("lastname") String lastName
    );

    // Login
    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
    );

    // Elastic search
    @GET("user/search/{param}")
    Call<ResponseBody> searchUsers(
            @Path("param") String parameter
    );

    // Update user info
    @FormUrlEncoded
    @PATCH("user/{id}/{private}")
    Call<ResponseBody> updateUser(
            @Path("id") int id,
            @Field("username") String username,
            @Field("user_firstName") String firstName,
            @Field("user_lastName") String lastName,
            @Field("user_email") String email,
            @Field("user_bio") String bio,
            @Path("private") boolean isPrivate
    );

    // Update user profile photo url
    @PATCH("user/updatePhoto/{id}/{ImageUrl}")
    Call<ResponseBody> updateImageUrl(
            @Path("id") int id, @Path("ImageUrl") String imageUrl
    );

    // Upload user profile photo to server
    @Multipart
    @POST("user/upload_image")
    Call<ResponseBody> uploadPhoto(
            @Part MultipartBody.Part image
    );

    // Get user followers and following
    @GET("user/getFollows/{user_id}")
    Call<ResponseBody> getUserFollows(@Path("user_id") int user_id);

    // Follow a user
    @POST("user/follow/{id}/{follow_id}")
    Call<ResponseBody> followUser(@Path("id") int usr_id, @Path("follow_id") int follow_id);

    // Follow a user
    @DELETE("user/unfollow/{id}/{follow_id}")
    Call<ResponseBody> unfollowUser(@Path("id") int usr_id, @Path("follow_id") int follow_id);

    // get one user
    @GET("user/{id}")
    Call<ResponseBody> getUserById(@Path("id") int user_id);

}


