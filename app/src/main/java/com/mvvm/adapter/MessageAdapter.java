package com.mvvm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.haerul.swipeviewpager.R;
import com.mvvm.model.Message;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.util.Session;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    private List<Message> listdata;
    private Context context;

    public MessageAdapter(List<Message> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_message_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Message myListData = listdata.get(position);
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.sender_avatar);
        if(myListData.getSender().getId() == Session.getInstance().getUser().getId())
        {
            // Current user sent the message
            holder.sent_messageMe.setText(myListData.getContent());
            holder.relativeLayout.setVisibility(View.INVISIBLE);
            holder.relativeLayoutMe.setVisibility(View.VISIBLE);
        }
        else
        {
            // message was recieved
            holder.sent_message.setText(myListData.getContent());
            downloadTask.execute(Common.BASE_PROFILE_IMAGE_URL + myListData.getSender().getImage_url());
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.relativeLayoutMe.setVisibility(View.INVISIBLE);
            holder.sent_message.setTextColor(context.getResources().getColor(R.color.black));
            holder.sent_message.setBackground(context.getResources().getDrawable(R.drawable.tv_round_corners));

        }


    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView sent_message, sent_messageMe;
        public CircleImageView sender_avatar;
        public RelativeLayout relativeLayout, relativeLayoutMe;

        public ViewHolder(View itemView) {
            super(itemView);
            sender_avatar = itemView.findViewById(R.id.sender_profile_image);
            sent_message = itemView.findViewById(R.id.sent_message);
            sent_messageMe = itemView.findViewById(R.id.sent_messageMe);
            relativeLayout = itemView.findViewById(R.id.messageRelLayout);
            relativeLayoutMe = itemView.findViewById(R.id.messageRelLayoutMe);
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
