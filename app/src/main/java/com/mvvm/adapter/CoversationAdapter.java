package com.mvvm.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
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
import com.mvvm.model.Conversation;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view.ConversationActivity;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CoversationAdapter extends RecyclerView.Adapter<CoversationAdapter.ViewHolder>{
    private List<Conversation> listdata;
    private Context context;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;


    // RecyclerView recyclerView;
    public CoversationAdapter(List<Conversation> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_conversation_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Conversation myListData = listdata.get(position);
        final User u;
        if(myListData.getFirs_user().getId() != Session.getInstance().getUser().getId()) u = myListData.getFirs_user();
        else u = myListData.getSecond_user();

        Call<ResponseBody> call = RetrofitClient.getInstance().getChatApi().getLastMessageForConversation(myListData.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        myListData.setLast_message(Parser.parseMessage(jsonObject));
                        holder.content.setText(myListData.getLast_message().getContent());
                        holder.date.setText(Common.getDateAMPM(myListData.getLast_message().getDate()));

                        if(myListData.getLast_message().getSender().getId() == Session.getInstance().getUser().getId())
                        {
                            // last message sent by logged in user
                            holder.seen.setImageResource(R.drawable.ic_delivered);
                            holder.seen.setVisibility(View.VISIBLE);
                            holder.content.setTypeface(holder.content.getTypeface(), Typeface.NORMAL);
                            holder.content.setTextColor(context.getResources().getColor(R.color.grey));
                        }
                        else
                        {
                            if(myListData.getLast_message().getStatus().equals("seen")
                            && myListData.getLast_message().getSender().getId() != Session.getInstance().getUser().getId())
                            {
                                holder.seen.setVisibility(View.INVISIBLE);
                                holder.content.setTextColor(context.getResources().getColor(R.color.grey));
                            }
                            else
                            {
                                holder.seen.setImageResource(R.drawable.ic_circle_new);
                                holder.seen.setVisibility(View.VISIBLE);
                                holder.content.setTypeface(holder.content.getTypeface(), Typeface.BOLD);
                                holder.content.setTextColor(context.getResources().getColor(R.color.black));
                            }
                        }

                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();} catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });

        holder.username.setText(u.getUsername());
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.avatar);
        downloadTask.execute(Common.BASE_PROFILE_IMAGE_URL + u.getImage_url());
        if(!u.isOnline()) holder.online.setVisibility(View.INVISIBLE);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ConversationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("reciever", u);
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
        public TextView username, content, date;
        public CircleImageView avatar;
        public ImageView seen, online;
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.username = (TextView) itemView.findViewById(R.id.conv_username);
            this.content = (TextView) itemView.findViewById(R.id.content);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.seen = (ImageView) itemView.findViewById(R.id.circle_new);
            this.online = (ImageView) itemView.findViewById(R.id.followingisOnlineCircle);
            this.avatar = (CircleImageView) itemView.findViewById(R.id.following_user_profile_image);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.conv_relLayout);
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
