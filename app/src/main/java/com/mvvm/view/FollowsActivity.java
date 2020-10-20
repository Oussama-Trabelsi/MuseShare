package com.mvvm.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.tabs.TabLayout;
import com.haerul.swipeviewpager.R;
import com.mvvm.model.User;
import com.mvvm.retrofit.Parser;
import com.mvvm.retrofit.RetrofitClient;
import com.mvvm.adapter.SectionsPagerAdapter;
import com.mvvm.util.Session;
import com.mvvm.view.fragment.FollowsActivityCallback;
import com.mvvm.view.fragment.FollowsFragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FollowsActivity extends AppCompatActivity implements FollowsActivityCallback {
    private ViewPager mViewPager;
    TextView username;
    TabLayout tabLayout;
    SectionsPagerAdapter adapter;
    User u;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follows);

        username = (TextView) findViewById(R.id.follows_username);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);


        final Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        u = (User) bundle.getSerializable("user");
        username.setText(u.getUsername());
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        ImageView backArrow = (ImageView) findViewById(R.id.follows_backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        Call<ResponseBody> call = RetrofitClient.getInstance().getUserApi().getUserFollows(u.getId());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        List<User> followers = new ArrayList<>();
                        List<User> following = new ArrayList<>();
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(jsonObject.getJSONArray("followers").length()>0)
                        {
                            followers = Parser.parseUserList(jsonObject.getJSONArray("followers"));

                        }

                        if(jsonObject.getJSONArray("following").length()>0)
                        {
                            following = Parser.parseUserList(jsonObject.getJSONArray("following"));

                        }

                        setupViewPager(followers, following);
                        if(intent.getStringExtra("mode").equals("following"))
                            mViewPager.setCurrentItem(1);


                    } catch (IOException e) {}
                    catch (JSONException e) { e.printStackTrace();}
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) { }
        });


    }


    /**
     * Responsible for adding the 3 tabs: Share, Home, Messages
     */
    private void setupViewPager(List<User> followers, List<User> following){

        adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new FollowsFragment(FollowsActivity.this, followers, this, false, u.getId())); //index 0
        adapter.addFragment(new FollowsFragment(FollowsActivity.this, following, this, true, u.getId())); //index 1
        mViewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(u.getFollowersCount() + " Followers");
        tabLayout.getTabAt(1).setText(u.getFollowingCount() + " Following");
    }

    @Override
    public void updateCount() {
        handler.postDelayed(updater, 200);
    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            Call<ResponseBody> call = RetrofitClient.getInstance().getUserApi().getUserById(u.getId());
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());

                            if(u.getId() == Session.getInstance().getUser().getId()) {
                                tabLayout.getTabAt(1).setText(Parser.parseUser(jsonObject).getFollowingCount() + " Following");
                            }

                        } catch (IOException e) {}
                        catch (JSONException e) { e.printStackTrace();}
                    }
                }
                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) { }
            });
        }
    };
}
