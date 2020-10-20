package com.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.haerul.swipeviewpager.R;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import com.mvvm.repository.PostRepository;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view.LikesActivity;
import com.mvvm.view.PostActivity;
import com.mvvm.view.fragment.HomeFragmentCallback;

import org.joda.time.Interval;
import org.joda.time.Period;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private List<Post> listdata;
    private PostRepository postRepository = PostRepository.getInstance();
    private Context context;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;
    String TRACK_URL = Common.BASE_TRACK_URL;
    private HomeFragmentCallback callback;

    public PostAdapter(List<Post> listdata, Context context, HomeFragmentCallback callback) {
        this.listdata = listdata;
        this.context = context;
        this.callback = callback;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_post_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Post myListData = listdata.get(position);

        if(Common.isInList(Session.getInstance().getUser(), myListData.getLikes()))
        {
            holder.like_post.setImageResource(R.drawable.ic_liked);
            holder.like_tv.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        holder.username.setText(myListData.getUser().getUsername());
        try {
            holder.date.setText(Common.getElapsedTime(myListData.getPostDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.description.setText(myListData.getDescription());
        holder.track_name.setText(myListData.getTrackName());
        holder.likes.setText(myListData.getLikesCount() + " likes");
        holder.comments.setText(myListData.getCommentsCount() + " comments");
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.avatar);
        downloadTask.execute(BASE_URL+myListData.getUser().getImage_url());

        DownloadImageWithURLTask2 downloadTask2 = new DownloadImageWithURLTask2(holder.track_icon);
        downloadTask2.execute(TRACK_URL+myListData.getIconUrl());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(context, PostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", myListData);
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

        holder.load_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.updateMusicPlayer(myListData.getTrackUrl(), myListData.getIconUrl(), myListData.getTrackName(), myListData.getUser());
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // open likes dialog

                Call<ResponseBody> call = RetrofitClient.getInstance().getPostApi().getPostLikes(myListData.getId());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 200) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body().string());
                                Intent intent = new Intent(context, LikesActivity.class);
                                intent.putExtra("list", (Serializable) Parser.parseUserList(jsonObject.getJSONArray("likes")));
                                context.startActivity(intent);


                            } catch (IOException e) {}
                            catch (JSONException e) { e.printStackTrace();}
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) { }
                });




            }
        });

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isInList(Session.getInstance().getUser(), myListData.getLikes()))
                {
                    postRepository.dilikePost(myListData, Session.getInstance().getUser());
                    myListData.getLikes().remove(Session.getInstance().getUser());
                    holder.like_post.setImageResource(R.drawable.ic_like);
                    holder.like_tv.setTextColor(context.getResources().getColor(R.color.grey));
                    holder.likes.setText(String.valueOf(myListData.getLikes().size() + " likes"));
                }
                else
                {
                    postRepository.likePost(myListData, Session.getInstance().getUser());
                    myListData.getLikes().add(Session.getInstance().getUser());
                    holder.like_post.setImageResource(R.drawable.ic_liked);
                    holder.like_tv.setTextColor(context.getResources().getColor(R.color.colorAccent));
                    holder.likes.setText(String.valueOf(myListData.getLikes().size() + " likes"));
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", myListData);
                intent.putExtra("comment","true");
                intent.putExtras(bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayout, likeLayout, comment;
        public TextView username, date, description, track_name, likes, comments, like_tv ;
        public CircleImageView avatar;
        public ImageView track_icon, load_track, like_post;

        public ViewHolder(View itemView) {
            super(itemView);
            this.username = (TextView) itemView.findViewById(R.id.post_username);
            this.avatar = (CircleImageView) itemView.findViewById(R.id.post_profile_image);
            this.relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relLayout3);
            this.likeLayout = (RelativeLayout)itemView.findViewById(R.id.post_likeLayout);
            this.track_icon = (ImageView) itemView.findViewById(R.id.post_track_icon);
            this.date = (TextView) itemView.findViewById(R.id.post_time);
            this.description = (TextView) itemView.findViewById(R.id.post_description);
            this.track_name = (TextView) itemView.findViewById(R.id.post_track_name);
            this.likes = (TextView) itemView.findViewById(R.id.post_track_likes);
            this.comments = (TextView) itemView.findViewById(R.id.post_track_comments);
            this.load_track = (ImageView) itemView.findViewById(R.id.post_load_track);
            this.like_post = (ImageView) itemView.findViewById(R.id.post_like);
            this.comment = (RelativeLayout) itemView.findViewById(R.id.comment_layout);
            this.like_tv = (TextView) itemView.findViewById(R.id.post_like_tv);
        }
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

}
