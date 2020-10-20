package com.mvvm.view.fragment;


import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.haerul.swipeviewpager.R;
import com.mvvm.adapter.LikeAdapter;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.util.Common;
import com.mvvm.util.Session;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class FollowsFragment extends Fragment implements FollowsFragmentCallback{

    View view;
    Context context;
    List<User> userList = new ArrayList<>();
    FollowsActivityCallback callback;
    boolean following;
    int user_id;
    private Handler handler = new Handler();

    private RecyclerView mListView;
    private LikeAdapter mAdapter;
    public FollowsFragment(Context context, List<User> users, FollowsActivityCallback callback, boolean following, int user_id) {
        this.context = context;
        this.userList = users;
        this.callback = callback;
        this.following = following;
        this.user_id = user_id;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // load data here
            handler.postDelayed(updater, 0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_follows, container, false);
        mListView = (RecyclerView) view.findViewById(R.id.follow_listView);
        mListView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new LikeAdapter(userList, context, callback);
        mListView.setAdapter(mAdapter);
        return view;
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            Call<ResponseBody> call = RetrofitClient.getInstance().getUserApi().getUserById(user_id);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            Session.getInstance().setUser(Parser.parseFullUser(jsonObject));
                            if(following)
                                userList = Parser.parseFullUser(jsonObject).getFollowing();
                            else
                                userList = Parser.parseFullUser(jsonObject).getFollowers();

                            mAdapter = new LikeAdapter(userList, context, callback);
                            mListView.setAdapter(mAdapter);
                            Common.followsFragmentCallback = FollowsFragment.this;

                        } catch (IOException e) {}
                        catch (JSONException e) { e.printStackTrace();}
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) { }
            });
        }
    };


    @Override
    public void updateAfterUnfollow() {
        handler.postDelayed(updater, 100);
    }
}
