package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.LikeAdapter;
import com.mvvm.model.User;

import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {
    List<User> likes = new ArrayList<>();
    ImageView close;
    TextView windowname;
    private RecyclerView mListView;
    private LikeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_likes);

        mListView = (RecyclerView) findViewById(R.id.like_listView);
        mListView.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = this.getIntent();
        likes = (List<User>) intent.getExtras().getSerializable("list");
        close = findViewById(R.id.popup_backArrow);
        windowname = findViewById(R.id.popup_name);
        windowname.setText("Likes");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAdapter = new LikeAdapter(likes, this);
        mListView.setAdapter(mAdapter);

    }
}
