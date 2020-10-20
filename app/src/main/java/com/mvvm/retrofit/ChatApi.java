package com.mvvm.retrofit;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatApi {
    // get all conversations for a user
    @GET("conversation/find/{user_id}")
    Call<ResponseBody> getConversationsForUser(@Path("user_id") int user_id );

    // get last message for a conversation
    @GET("message/findlast/{conv_id}")
    Call<ResponseBody> getLastMessageForConversation(@Path("conv_id") int conv_id );

    // get conversation for two users
    @GET("message/getConversation/{sender_id}/{reciever_id}")
    Call<ResponseBody> getConversation(@Path("sender_id") int sender_id, @Path("reciever_id") int reciever_id );

    // send a message
    @POST("message/sendMessage")
    @FormUrlEncoded
    Call<ResponseBody> sendMessage(@Field("sender_id") int sender_id, @Field("reciever_id") int reciever_id, @Field("content") String content);
}
