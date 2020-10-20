package com.mvvm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.haerul.swipeviewpager.R;
import com.mvvm.model.User;
import com.mvvm.util.Common;
import com.mvvm.view.HomeActivity;
import com.mvvm.view.fragment.ProfileFragment;

import java.io.InputStream;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    private List<User> listdata;
    private Context context;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;


    // RecyclerView recyclerView;
    public SearchAdapter(List<User> listdata, Context context) {
        this.listdata = listdata;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.layout_user_listitem, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final User myListData = listdata.get(position);
        holder.username.setText(myListData.getUsername());
        holder.fullname.setText(myListData.getFirstName()+ " " + myListData.getLastName());
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(holder.avatar);
        downloadTask.execute(BASE_URL+myListData.getImage_url());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loadFragment(new ProfileFragment(context, myListData));
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
        public RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            this.username = (TextView) itemView.findViewById(R.id.search_user_username);
            this.fullname = (TextView) itemView.findViewById(R.id.search_user_full_name);
            this.avatar = (CircleImageView) itemView.findViewById(R.id.search_user_profile_image);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.search_relLayout);
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

    public void loadFragment(Fragment fragment) {
        FragmentManager fm = ((HomeActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main_container,fragment);
        fragmentTransaction.commit();
    }

}
