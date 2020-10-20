package com.mvvm.view_model;


import android.app.Application;


import androidx.lifecycle.ViewModel;

import com.mvvm.model.User;
import com.mvvm.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private UserRepository userRepository = UserRepository.getInstance();
    private List<User> userList = new ArrayList<User>();

    public List<User> getUserList(String keyword)
    {
        userList = userRepository.searchUsers(keyword);
        return userList;
    }
}
