package com.mvvm.view.fragment;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mvvm.adapter.PostAdapter;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.haerul.swipeviewpager.R;
import com.mvvm.view.ChatActivity;
import com.mvvm.view.HomeActivityCallback;
import com.mvvm.view_model.HomeViewModel;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeFragmentCallback {

    private ProgressDialog progressDialog;
    private RecyclerView mRecyclerView;
    private PostAdapter mAdapter;
    private HomeViewModel homeViewModel;
    private ImageView add;
    private List<Post> mList = new ArrayList<>();
    private View view;
    private Context appContext;

    /* Views */
    ImageView mpPlayPause, mpIcon;
    TextView mpCurrentTime, mpTotalTime, trackName, trackOwner;
    RelativeLayout defaultPlayerLayout, chatLayout;

    /* Music plaer */
    SeekBar mpSeekBar;
    MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    HomeActivityCallback callback;
    public HomeFragment(Context context,  HomeActivityCallback callback) {
        this.appContext = context;
        this.callback = callback;

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        progressDialog = new ProgressDialog(appContext);

        /* initialize views*/
        trackName = view.findViewById(R.id.mp_trackname);
        trackOwner = view.findViewById(R.id.mp_track_owner);
        defaultPlayerLayout = view.findViewById(R.id.default_player_layout);
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.post_recyclerView);
        mpIcon = view.findViewById(R.id.mp_avatar);
        mpPlayPause = view.findViewById(R.id.mp_play_btn);
        mpCurrentTime = view.findViewById(R.id.mp_current_time);
        mpTotalTime = view.findViewById(R.id.mp_total_time);
        mpSeekBar = view.findViewById(R.id.mp_seekbar);
        add = view.findViewById(R.id.home_add_post);
        chatLayout = view.findViewById(R.id.homeChatLayout);

        /* initialize recyclerview */
        mRecyclerView.setLayoutManager(new LinearLayoutManager(appContext));
        mAdapter = new PostAdapter(mList, appContext, this);
        mRecyclerView.setAdapter(mAdapter);

        /* observe live data */
        homeViewModel.getFollingPosts().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mList.addAll(posts);
                mAdapter.notifyDataSetChanged();
            }
        });

        /* initialize mediaplayer */
        mediaPlayer = new MediaPlayer();
        mpSeekBar.setMax(100);
        setMediaPlayerListeners();

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.loadFragmentcall(new ShareFragment(appContext, callback));
            }
        });
        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mediaPlayer.isPlaying())
        {mediaPlayer.stop();
        handler.removeCallbacks(updater);
        }
        //mediaPlayer.release();
    }

    private void showDialog()
    {
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Fetching track from server\n Please wait for a moment");
        progressDialog.show();
    }

    private class DownloadImageWithURLTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageWithURLTask(ImageView bmImage) {
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

    @Override
    public void updateMusicPlayer(String trackUrl, String track_icon, String track_name, User u) {
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        },1000);
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(mpIcon);
        downloadTask.execute(Common.BASE_TRACK_URL + track_icon);
        this.trackName.setText(track_name);
        this.trackOwner.setText(u.getUsername());
        prepareMediaPlayer(trackUrl);
    }
}
