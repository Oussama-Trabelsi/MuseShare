package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.CoversationAdapter;
import com.mvvm.adapter.FollowingAdapter;
import com.mvvm.model.Conversation;
import com.mvvm.model.Message;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view_model.ChatViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    ImageView back_arrow, newMessage;
    private RecyclerView mRecyclerView;
    private RecyclerView mConvRecyclerView;
    private ChatViewModel chatViewModel;
    private FollowingAdapter mAdapter;
    private CoversationAdapter conversationAdapter;
    private List<Conversation> mList = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

        //Toast.makeText(ChatActivity.this, Common.sortCoversation(), Toast.LENGTH_SHORT).show();
        back_arrow = findViewById(R.id.chat_backArrow);
        newMessage = findViewById(R.id.newMessage);
        mRecyclerView = findViewById(R.id.following_recyclerView);
        mConvRecyclerView = findViewById(R.id.convetsation_recyclerView);

        /* initialize recyclerviews */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.HORIZONTAL, false));
        sortByOnline(Session.getInstance().getUser().getFollowing());
        mAdapter = new FollowingAdapter(Session.getInstance().getUser().getFollowing(), ChatActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        mConvRecyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        conversationAdapter = new CoversationAdapter(mList, ChatActivity.this);
        mConvRecyclerView.setAdapter(conversationAdapter);

        /* observe live data */
        chatViewModel.getConversations().observe(this, new Observer<List<Conversation>>() {
            @Override
            public void onChanged(List<Conversation> conversations) {
                filterConversations(conversations);
                conversationAdapter.notifyDataSetChanged();
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        // Setup a thread to continously update recyclerview if needed
        handler.postDelayed(updater, 5000);




    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {


            Call<ResponseBody> call = RetrofitClient.getInstance().getChatApi().getConversationsForUser(Session.getInstance().getUser().getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            if (jsonArray.length() > 0) {
                                try {
                                    mList.clear();
                                    filterConversations(Parser.parseConversationList(jsonArray));
                                    conversationAdapter.notifyDataSetChanged();
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

            handler.postDelayed(this, 5000);
        }
    };

    private  void filterConversations(List<Conversation> conversations)
    {
        List<Conversation> filteredList = new ArrayList<>();
        if(conversations!= null) {
            for (Conversation c : conversations) {
                if (!c.getMessages().isEmpty())
                    filteredList.add(c);
            }

            this.mList.addAll(filteredList);

        }

    }

    private static void sortByOnline(List<User> users)
    {
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User u1, User u2) {
                boolean b1 = !u1.isOnline();
                boolean b2 = !u2.isOnline();
                if (b1 && !b2) {
                    return +1;
                }
                if (!b1 && b2) {
                    return -1;
                }
                return 0;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // set online logic
        //TODO:some logic to set isOnline

        Toast.makeText(this, "User reconnected !", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // set offline + last login logic
        Toast.makeText(this, "User disconnected !", Toast.LENGTH_SHORT).show();
    }

}
