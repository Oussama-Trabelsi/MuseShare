package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.CoversationAdapter;
import com.mvvm.adapter.MessageAdapter;
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
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConversationActivity extends AppCompatActivity {
    ImageView back_arrow, submit_message;
    EditText message;
    CircleImageView reciever_avatar;
    User u;
    MessageAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ChatViewModel chatViewModel;
    private List<Message> mList = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);

        Intent intent = this.getIntent();
        u = (User) intent.getExtras().getSerializable("reciever");

        back_arrow = findViewById(R.id.chat_backArrow);
        reciever_avatar = findViewById(R.id.profile_image);
        submit_message = findViewById(R.id.submit_message);
        message = findViewById(R.id.message_text);

        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(reciever_avatar);
        downloadTask.execute(Common.BASE_PROFILE_IMAGE_URL + u.getImage_url());

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        submit_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!message.getText().toString().matches("")) {
                    chatViewModel.sendMessage(u.getId(), message.getText().toString().trim());
                    message.setText("");
                }
            }
        });


        mRecyclerView = findViewById(R.id.messages_recyclerView);

        /* initialize recyclerviews */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ConversationActivity.this));
        mAdapter = new MessageAdapter(mList, ConversationActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        handler.postDelayed(updater, 0);


    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            List<Message> updatedList = chatViewModel.getConversation(u.getId()).getValue();
            if(updatedList!=null && mList !=null) {
                if (updatedList.size() > mList.size())
                    Toast.makeText(ConversationActivity.this, "mew GOT mail", Toast.LENGTH_SHORT).show();
            }

            Call<ResponseBody> call = RetrofitClient.getInstance().getChatApi().getConversation(Session.getInstance().getUser().getId(),u.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().string());
                            if (jsonArray.length() > 0) {
                                try {
                                    mList.clear();
                                    mList.addAll(Parser.parseMessageList(jsonArray));
                                    mAdapter.notifyDataSetChanged();

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
                    Toast.makeText(ConversationActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });

            handler.postDelayed(this, 3000);
        }
    };
    // Load image from server url
    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageWithURLTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String pathToFile = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(pathToFile).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

    }
}
