package com.mvvm.view.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.GridImageAdapter;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Common;
import com.mvvm.util.Session;
import com.mvvm.view.EditProfileActivity;
import com.mvvm.view.FollowsActivity;
import com.mvvm.view.HomeActivity;
import com.mvvm.view_model.PostViewModel;
import com.mvvm.view_model.ProfileViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private PostViewModel postViewModel;
    private List<Post> mList = new ArrayList<>();
    private View view;
    private Context appContext;
    GridView gridView;
    TextView username, name, bio, postCount, followersCount, followingCount;
    Button editProfile_btn;
    LinearLayout followersLayout, followingLayout;
    User user;
    FrameLayout frameLayout;
    CircleImageView img;
    String BASE_URL = Common.BASE_PROFILE_IMAGE_URL;


    public ProfileFragment(Context context, User u) {
        // Required empty public constructor
        this.appContext = context;
        user = u;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupToolbar();

        if(user.getId() == Session.getInstance().getUser().getId()) {
            Call<ResponseBody> call = RetrofitClient.getInstance().getUserApi().getUserById(user.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            user = Parser.parseFullUser(jsonObject);
                            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(img);
                            downloadTask.execute(BASE_URL + user.getImage_url());
                            username.setText(user.getUsername());
                            postCount.setText(String.valueOf(user.getPostsCount()));
                            followersCount.setText(String.valueOf(user.getFollowersCount()));
                            followingCount.setText(String.valueOf(user.getFollowingCount()));
                            name.setText(user.getFirstName() + " " + user.getLastName());
                            bio.setText(user.getBio());

                        } catch (IOException e) {}
                        catch (JSONException e) { e.printStackTrace();}
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) { }
            });

        }

        username = view.findViewById(R.id.profile_username);
        name = view.findViewById(R.id.profile_fullName);
        bio = view.findViewById(R.id.profile_bio);
        followersCount = view.findViewById(R.id.profile_followers_count);
        followingCount = view.findViewById(R.id.profile_following_count);
        postCount = view.findViewById(R.id.profile_post_count);
        editProfile_btn = view.findViewById(R.id.profile_edit_btn);
        followersLayout = view.findViewById(R.id.followersLayout);
        followingLayout = view.findViewById(R.id.followingLayout);
        postViewModel = ViewModelProviders.of(this).get(PostViewModel.class);
        gridView = (GridView) view.findViewById(R.id.gridView);
        frameLayout = view.findViewById(R.id.profile_frameLayout);

        username.setText(user.getUsername());
        followersCount.setText(String.valueOf(user.getFollowersCount()));
        followingCount.setText(String.valueOf(user.getFollowingCount()));
        postCount.setText(String.valueOf(user.getPostsCount()));
        name.setText(user.getFirstName() + " " + user.getLastName());
        bio.setText(user.getBio());

        editProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.home_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });


        followersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), FollowsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                intent.putExtra("mode","followers");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(getActivity(), FollowsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", user);
                intent.putExtras(bundle);
                intent.putExtra("mode","following");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });




        // grid layout
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/3;
        gridView.setColumnWidth(imageWidth);
        final GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, "", mList);
        gridView.setAdapter(adapter);

        /* observe live data */
        postViewModel.getPostsForUser(user.getId()).observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(List<Post> posts) {
                mList.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });

        img = (CircleImageView) view.findViewById(R.id.profile_profile_image);
        DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(img);
        downloadTask.execute(BASE_URL+user.getImage_url());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(user.getId() == Session.getInstance().getUser().getId()) {
            Call<ResponseBody> call = RetrofitClient.getInstance().getUserApi().getUserById(user.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            user = Parser.parseFullUser(jsonObject);
                            DownloadImageWithURLTask downloadTask = new DownloadImageWithURLTask(img);
                            downloadTask.execute(BASE_URL + user.getImage_url());
                            username.setText(user.getUsername());
                            postCount.setText(String.valueOf(user.getPostsCount()));
                            followersCount.setText(String.valueOf(user.getFollowersCount()));
                            followingCount.setText(String.valueOf(user.getFollowingCount()));
                            name.setText(user.getFirstName() + " " + user.getLastName());
                            bio.setText(user.getBio());

                        } catch (IOException e) {}
                        catch (JSONException e) { e.printStackTrace();}
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) { }
            });

        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
        ((HomeActivity) getActivity()).setSupportActionBar(toolbar);
        /* got stuff to do here don't forget */

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
