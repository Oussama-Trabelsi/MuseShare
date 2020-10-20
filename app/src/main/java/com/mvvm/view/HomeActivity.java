package com.mvvm.view;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.haerul.swipeviewpager.R;
import com.mvvm.util.Session;
import com.mvvm.view.fragment.HomeFragment;
import com.mvvm.view.fragment.ProfileFragment;
import com.mvvm.view.fragment.SearchFragment;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class HomeActivity extends AppCompatActivity implements HomeActivityCallback {

    private static final String TAG = "HomeActivity";

    MeowBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        loadFragment(new HomeFragment(this, this));
        setupBottomNavigationView();



    }



    public void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_main_container,fragment);
        fragmentTransaction.commit();
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView()
    {
        bottomNavigation = findViewById(R.id.buttomNavigation);
        bottomNavigation.add(new MeowBottomNavigation.Model(1, R.drawable.ic_home));
        bottomNavigation.add(new MeowBottomNavigation.Model(2, R.drawable.ic_search));
        bottomNavigation.add(new MeowBottomNavigation.Model(3, R.drawable.ic_heart_shape_outline));
        bottomNavigation.add(new MeowBottomNavigation.Model(4, R.drawable.ic_profile));
        bottomNavigation.setOnClickMenuListener(new Function1<MeowBottomNavigation.Model, Unit>() {
            @Override
            public Unit invoke(MeowBottomNavigation.Model model) {
                switch (model.getId()) {
                    case 1:
                         loadFragment(new HomeFragment(HomeActivity.this, HomeActivity.this));
                        break;

                    case 2:
                        loadFragment(new SearchFragment(HomeActivity.this));
                        break;

                    case 3:
                        //loadFragment(new SearchFragment(SocialActivity.this));
                        break;


                    case 4:
                        loadFragment(new ProfileFragment(HomeActivity.this, Session.getInstance().getUser()));
                        break;
                }
                return null;
            }
        });
    }


    @Override
    public void loadFragmentcall(Fragment fragment) {
        loadFragment(fragment);
    }
}
