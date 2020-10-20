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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.haerul.swipeviewpager.R;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view.ConfirmUnfollowActivity;
import com.mvvm.view.fragment.FollowsActivityCallback;
import com.mvvm.view_model.ProfileViewModel;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder>{
    private List<User> listdata;
    private Context context;
    private ProfileViewModel profileViewModel;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;
    FollowsActivityCallback callback;


    public LikeAdapter(List<User> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
        profileViewModel = ViewModelProviders.of((FragmentActivity) context).get(ProfileViewModel.class);
    }

    public LikeAdapter(List<User> listdata, Context context, FollowsActivityCallback callback) {
        this.listdata = listdata;
        this.context = context;
        profileViewModel = ViewModelProviders.of((FragmentActivity) context).get(ProfileViewModel.class);
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_like_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final User myListData = listdata.get(position);
        if(myListData.getId() == Session.getInstance().getUser().getId())
            holder.follow_btn.setVisibility(View.INVISIBLE); // cannot follow / unfollow himself

        holder.username.setText(myListData.getUsername());
        holder.fullname.setText(myListData.getFirstName()+ " " + myListData.getLastName());
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.avatar);
        downloadTask.execute(BASE_URL+myListData.getImage_url());
        if(Common.isInList(myListData, Session.getInstance().getUser().getFollowing()))
        {
            holder.follow_btn.setText("FOLLOWING");
            holder.follow_btn.setTextColor(context.getResources().getColor(R.color.black));
            holder.follow_btn.setBackground(context.getResources().getDrawable(R.drawable.btn_round_corners_unfollow));
        }

        holder.follow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(holder.follow_btn.getText().equals("FOLLOW"))
                {
                    // Follow user

                    // First, check if user in question has a private account
                    if(myListData.isPrivate())
                    {
                        // User is private, send a follow request
                        holder.follow_btn.setText("REQUESTED");
                        holder.follow_btn.setTextColor(context.getResources().getColor(R.color.black));
                        holder.follow_btn.setBackground(context.getResources().getDrawable(R.drawable.btn_round_corners_unfollow));
                    }

                    else
                    {
                        // User is not private, follow user immediately
                        profileViewModel.followUser(myListData.getId());
                        Session.getInstance().getUser().getFollowing().add(myListData);
                        holder.follow_btn.setText("FOLLOWING");
                        holder.follow_btn.setTextColor(context.getResources().getColor(R.color.black));
                        holder.follow_btn.setBackground(context.getResources().getDrawable(R.drawable.btn_round_corners_unfollow));
                        if(callback != null) callback.updateCount();
                    }


                }
                else if(holder.follow_btn.getText().equals("FOLLOWING"))
                {
                    // Unfollow user

                    // First, chack if user in question has a private account
                    if(myListData.isPrivate())
                    {
                        // User is private, display confirmation window
                        Intent intent = new Intent(context, ConfirmUnfollowActivity.class);
                        intent.putExtra("following", myListData);
                        context.startActivity(intent);
                    }
                    else
                    {
                        // User is not private, unfollow user immediately
                        profileViewModel.unfollowUser(myListData.getId());
                        Session.getInstance().getUser().getFollowing().remove(myListData);
                        holder.follow_btn.setText("FOLLOW");
                        holder.follow_btn.setTextColor(context.getResources().getColor(R.color.white));
                        holder.follow_btn.setBackground(context.getResources().getDrawable(R.drawable.btn_round_corners_follow));
                        if(callback != null) callback.updateCount();
                    }

                }
                else if(holder.follow_btn.getText().equals("REQUESTED"))
                {
                    // Cancel follow request
                    holder.follow_btn.setText("FOLLOW");
                    holder.follow_btn.setTextColor(context.getResources().getColor(R.color.white));
                    holder.follow_btn.setBackground(context.getResources().getDrawable(R.drawable.btn_round_corners_follow));
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username, fullname;
        public CircleImageView avatar;
        public Button follow_btn;
        public RelativeLayout linkLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.username = (TextView) itemView.findViewById(R.id.like_user_username);
            this.fullname = (TextView) itemView.findViewById(R.id.like_user_full_name);
            this.avatar = (CircleImageView) itemView.findViewById(R.id.like_profile_image);
            this.follow_btn = (Button) itemView.findViewById(R.id.like_follow_btn);
            this.linkLayout = itemView.findViewById(R.id.linkLayout);
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
