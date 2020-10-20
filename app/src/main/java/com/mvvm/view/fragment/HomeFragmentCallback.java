package com.mvvm.view.fragment;

import com.mvvm.model.User;

public interface HomeFragmentCallback {
    void updateMusicPlayer(String trackUrl, String track_icon, String track_name, User u);
}
