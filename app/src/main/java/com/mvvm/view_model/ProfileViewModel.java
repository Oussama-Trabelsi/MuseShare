package com.mvvm.view_model;


import androidx.lifecycle.ViewModel;

import com.mvvm.model.User;
import com.mvvm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileViewModel extends ViewModel {

    private UserRepository userRepository = UserRepository.getInstance();

    public List<User> getUserFollowers(int user_id)
    {
        return userRepository.getUserFollowers(user_id);
    }

    public List<User> getUserFollowing(int user_id)
    {
        return userRepository.getUserFollowing(user_id);
    }

    public void followUser(int follow_id)
    {
        userRepository.followUser(follow_id);
    }

    public void unfollowUser(int follow_id)
    {
        userRepository.unfollowUser(follow_id);
    }
}
