package com.mvvm.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mvvm.model.Post;
import com.mvvm.repository.PostRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private PostRepository postRepository = PostRepository.getInstance();

    public MutableLiveData<List<Post>> getFollingPosts() {
        return postRepository.getFollingPosts();
    }

}
