package com.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haerul.swipeviewpager.R;
import com.mvvm.model.Comment;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view.LikesActivity;

import java.io.InputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    private List<Comment> listdata;
    private Context context;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;

    public CommentAdapter(List<Comment> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_comment_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Comment myListData = listdata.get(position);
        holder.username.setText(myListData.getUser().getUsername());
        holder.content.setText(myListData.getContent());
        if(myListData.getUser().getId() == Session.getInstance().getUser().getId())
            holder.delete_btn.setVisibility(View.VISIBLE);
        else
            holder.delete_btn.setVisibility(View.INVISIBLE);

        try {
            holder.date.setText(Common.getElapsedTimeForComment(myListData.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.avatar);
        downloadTask.execute(BASE_URL+myListData.getUser().getImage_url());
        try {
            holder.date.setText(Common.getElapsedTimeForComment(myListData.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.likesCount.setText(String.valueOf(myListData.getLikesCount()));

        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.likesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LikesActivity.class);
                intent.putExtra("list", (Serializable) myListData.getLikes());
                context.startActivity(intent);
            }
        });


    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, content, date, like, likesCount;
        public RelativeLayout likesLayout;
        public CircleImageView avatar;
        public ImageView delete_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            this.username = (TextView) itemView.findViewById(R.id.comment_username);
            this.content = (TextView) itemView.findViewById(R.id.comment_content);
            this.date = (TextView) itemView.findViewById(R.id.comment_date);
            this.like = (TextView) itemView.findViewById(R.id.comment_like_tv);
            this.avatar = (CircleImageView) itemView.findViewById(R.id.user_profile_image);
            this.delete_btn = (ImageView) itemView.findViewById(R.id.comment_delete);
            this.likesLayout = (RelativeLayout) itemView.findViewById(R.id.commentLikesLayout);
            this.likesCount = (TextView) itemView.findViewById(R.id.commentLikesCount);
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

}
