package com.mvvm.view.fragment;


import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.haerul.swipeviewpager.R;
import com.mvvm.view.HomeActivityCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShareFragment extends Fragment {

    Context context;
    View view;
    ImageView back_btn;
    TextView windowname;

    HomeActivityCallback callback;

    public ShareFragment(Context appContext, HomeActivityCallback callback) {
        // Required empty public constructor
        this.context = appContext;
        this.callback = callback;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_share, container, false);
        windowname = view.findViewById(R.id.singlePost_username);
        back_btn = view.findViewById(R.id.singlePost_backArrow);

        windowname.setText("Share a track");
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.loadFragmentcall(new HomeFragment(context, callback));
            }
        });
        return view;
    }

}
