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

import androidx.recyclerview.widget.RecyclerView;

import com.haerul.swipeviewpager.R;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.view.ConversationActivity;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class FollowingAdapter extends RecyclerView.Adapter<FollowingAdapter.ViewHolder>{
    private List<User> listdata;
    private Context context;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;


    // RecyclerView recyclerView;
    public FollowingAdapter(List<User> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_user_followinglistitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final User myListData = listdata.get(position);
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.avatar);
        downloadTask.execute(BASE_URL+myListData.getImage_url());
        holder.firstname.setText(myListData.getFirstName());
        if(!myListData.isOnline()) holder.online_circle.setVisibility(View.INVISIBLE);
        else holder.online_circle.setVisibility(View.VISIBLE);

        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ConversationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reciever", myListData);
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
        public CircleImageView avatar;
        public ImageView online_circle;
        public TextView firstname;

        public ViewHolder(View itemView) {
            super(itemView);
            this.avatar = (CircleImageView) itemView.findViewById(R.id.following_user_profile_image);
            this.online_circle = (ImageView) itemView.findViewById(R.id.followingisOnlineCircle);
            this.firstname = itemView.findViewById(R.id.following_name);
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
