package com.mvvm.repository;

import androidx.lifecycle.MutableLiveData;

import com.mvvm.model.Conversation;
import com.mvvm.model.Message;
import com.mvvm.retrofit.ChatApi;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Session;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRepository {
    private static ChatRepository instance;
    private ChatApi chatApi;
    Message m;

    MutableLiveData<List<Message>> coversationMutableLiveData = new MutableLiveData<>();

    public static ChatRepository getInstance()
    {
        if(instance == null) instance = new ChatRepository();
        return instance;
    }

    public ChatRepository() {
        chatApi = RetrofitClient.getInstance().getChatApi();
    }

    public MutableLiveData<List<Conversation>> getConversations() {
        final MutableLiveData<List<Conversation>> coversationsMutableLiveData = new MutableLiveData<>();

        Call<ResponseBody> call = chatApi.getConversationsForUser(Session.getInstance().getUser().getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            try {
                                coversationsMutableLiveData.setValue(Parser.parseConversationList(jsonArray));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        response.body().close();

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return coversationsMutableLiveData;
    }

    public MutableLiveData<List<Message>> getConversation(int reciever_id) {

        coversationMutableLiveData = new MutableLiveData<>();
        Call<ResponseBody> call = chatApi.getConversation(Session.getInstance().getUser().getId(),reciever_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            try {
                                coversationMutableLiveData.setValue(Parser.parseMessageList(jsonArray));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        response.body().close();

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        return coversationMutableLiveData;
    }

    public void sendMessage(int reciever_id, String content)
    {
        Call<ResponseBody> call = chatApi.sendMessage(Session.getInstance().getUser().getId(), reciever_id, content);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


}
