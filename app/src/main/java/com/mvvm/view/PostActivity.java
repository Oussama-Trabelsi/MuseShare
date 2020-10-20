package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.CommentAdapter;
import com.mvvm.model.Comment;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view_model.PostViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {
    RelativeLayout defaultPlayerLayout, likeLayout;
    ImageView back_btn, like_post;
    CircleImageView post_user_image, user_image;
    TextView post_user, date, description, track_name, likes, post_likes , username, like_tv ;
    EditText comment_text;
    Post p;
    PostViewModel postViewModel;
    /* Music plaer */
    SeekBar mpSeekBar;
    MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    ImageView mpPlayPause, mpIcon;
    TextView mpCurrentTime, mpTotalTime, trackOwner;

    private RecyclerView mRecyclerView;
    private CommentAdapter mAdapter;
    private List<Comment> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        p = (Post) bundle.getSerializable("post");

        initializeViews();

        if(intent.getStringExtra("comment") != null)
        {
            comment_text.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(PostActivity.INPUT_METHOD_SERVICE);
            imm.showSoftInput(comment_text, InputMethodManager.SHOW_IMPLICIT);
        }

        /* initialize recyclerview */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(PostActivity.this));
        mAdapter = new CommentAdapter(mList, PostActivity.this);
        mRecyclerView.setAdapter(mAdapter);

        /* observe live data */
        postViewModel.getPostComments(p).observe(this, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                mList.addAll(comments);
                mAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mediaPlayer.isPlaying())
        {mediaPlayer.stop();
            handler.removeCallbacks(updater);
        }
    }

    // initialize views
    private void initializeViews()
    {
        defaultPlayerLayout = findViewById(R.id.default_player_layout);
        if(defaultPlayerLayout.getVisibility() == View.VISIBLE) defaultPlayerLayout.setVisibility(View.INVISIBLE);
        back_btn = findViewById(R.id.singlePost_backArrow);
        post_user = findViewById(R.id.post_username);
        likeLayout = findViewById(R.id.post_likeLayout);
        date = findViewById(R.id.post_time);
        description = findViewById(R.id.post_description);
        track_name = findViewById(R.id.mp_trackname);
        likes = findViewById(R.id.post_likes);
        post_likes = findViewById(R.id.post_likes);
        like_post = findViewById(R.id.post_like);
        like_tv = findViewById(R.id.post_like_tv);
        post_user_image = findViewById(R.id.post_profile_image);
        user_image = findViewById(R.id.user_profile_image);
        username = findViewById(R.id.singlePost_username);
        trackOwner = findViewById(R.id.mp_track_owner);
        mpIcon = findViewById(R.id.mp_avatar);
        mpPlayPause = findViewById(R.id.mp_play_btn);
        mpCurrentTime = findViewById(R.id.mp_current_time);
        mpTotalTime = findViewById(R.id.mp_total_time);
        mpSeekBar = findViewById(R.id.mp_seekbar);
        comment_text = findViewById(R.id.singlePost_leave_comment);
        mRecyclerView = findViewById(R.id.comment_listView);

        username.setText(p.getUser().getUsername() + "\' s post");
        post_user.setText(p.getUser().getUsername());
        description.setText(p.getDescription());
        track_name.setText(p.getTrackName());
        trackOwner.setText(p.getUser().getLastName());
        try {
            date.setText(Common.getElapsedTime(p.getPostDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(post_user_image);
        downloadTask.execute(Common.BASE_PROFILE_IMAGE_URL + p.getUser().getImage_url());
        DownloadImageWithURLTask downloadTask2 = new DownloadImageWithURLTask(user_image);
        downloadTask2.execute(Common.BASE_PROFILE_IMAGE_URL + Session.getInstance().getUser().getImage_url());
        DownloadImageWithURLTask2 downloadTask3 = new DownloadImageWithURLTask2(mpIcon);
        downloadTask3.execute(Common.BASE_TRACK_URL + p.getIconUrl());
        post_likes.setText(p.getLikesCount() + " Likes");

        if(Common.isInList(Session.getInstance().getUser(), p.getLikes()))
        {
            like_post.setImageResource(R.drawable.ic_liked);
            like_tv.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        comment_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                // If the event is a key-down event on the "enter" button
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    postViewModel.commentPost(new Comment(comment_text.getText().toString()), p);
                    comment_text.getText().clear();

                    return true;
                }
                return false;
            }
        });

        likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, LikesActivity.class);
                intent.putExtra("list", (Serializable) p.getLikes());
                startActivity(intent);
            }
        });

        likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isInList(Session.getInstance().getUser(), p.getLikes()))
                {
                    postViewModel.likePost(p, Session.getInstance().getUser());
                    p.getLikes().remove(Session.getInstance().getUser());
                    like_post.setImageResource(R.drawable.ic_like);
                    like_tv.setTextColor(getResources().getColor(R.color.grey));
                    likes.setText(p.getLikes().size() + " likes");
                }
                else
                {
                    postViewModel.dislikePost(p, Session.getInstance().getUser());
                    p.getLikes().add(Session.getInstance().getUser());
                    like_post.setImageResource(R.drawable.ic_liked);
                    like_tv.setTextColor(getResources().getColor(R.color.colorAccent));
                    likes.setText(p.getLikes().size() + " likes");
                }
            }
        });
        /* initialize mediaplayer */
        mediaPlayer = new MediaPlayer();
        mpSeekBar.setMax(100);
        setMediaPlayerListeners();

        prepareMediaPlayer(p.getTrackUrl());
    }

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

    private class DownloadImageWithURLTask2 extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageWithURLTask2(ImageView bmImage) {
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

    /* MediaPlayer related */
    private void setMediaPlayerListeners()
    {
        mpPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    handler.removeCallbacks(updater);
                    mpPlayPause.setImageResource(R.drawable.ic_play_button);
                }
                else {
                    mediaPlayer.start();
                    mpPlayPause.setImageResource(R.drawable.ic_pause);
                    updateSeekBar();
                }
            }
        });

        mpSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                SeekBar seekBar = (SeekBar) view;
                int playPostion = (mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playPostion);
                mpCurrentTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                mpSeekBar.setSecondaryProgress(i);
            }
        });
    }

    private void prepareMediaPlayer(String trackUrl)
    {
        if(defaultPlayerLayout.getVisibility() == View.VISIBLE) defaultPlayerLayout.setVisibility(View.INVISIBLE);
        mpSeekBar.setProgress(0);
        mpCurrentTime.setText("0:00");
        if(mediaPlayer.isPlaying()) {mediaPlayer.stop();
            handler.removeCallbacks(updater);
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(Common.BASE_TRACK_URL + trackUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            updateSeekBar();
            mpPlayPause.setImageResource(R.drawable.ic_pause);
            mpTotalTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateSeekBar();
            long currentDuration = mediaPlayer.getCurrentPosition();
            mpCurrentTime.setText(milliSecondsToTimer(currentDuration));
        }
    };

    private void updateSeekBar(){
        if(mediaPlayer.isPlaying()){
            mpSeekBar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100));
            handler.postDelayed(updater, 1000);
        }
    }

    private String milliSecondsToTimer(long milliSeconds){
        String timerString = "";
        String secondsString = "";

        int hours = (int) (milliSeconds / (1000 * 60 * 60));
        int minutes = (int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);

        if(hours > 0) timerString = hours + ":";
        if(seconds < 10) secondsString = "0" + seconds;
        else secondsString = "" + seconds;

        timerString = timerString + minutes + ":" + secondsString;
        return timerString;
    }

}
