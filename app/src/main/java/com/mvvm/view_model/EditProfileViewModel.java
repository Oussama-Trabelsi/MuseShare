package com.mvvm.view_model;

import androidx.lifecycle.ViewModel;

import com.mvvm.repository.UserRepository;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileViewModel extends ViewModel {
    private UserRepository userRepository = UserRepository.getInstance();

    public boolean updateUser(int id, String username, String firstName,
                           String lastName, String email, String bio,
                           boolean isPrivate)
    {
        return userRepository.updateUser(id, username, firstName, lastName, email, bio, isPrivate);
    }

    public boolean uploadProfilePhoto(MultipartBody.Part body)
    {
        return userRepository.uploadProfilePhoto(body);
    }



}
