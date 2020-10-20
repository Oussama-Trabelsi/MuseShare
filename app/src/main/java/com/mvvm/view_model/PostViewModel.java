package com.mvvm.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mvvm.model.Comment;
import com.mvvm.model.Post;
import com.mvvm.model.User;
import com.mvvm.repository.PostRepository;

import java.util.List;

public class PostViewModel extends ViewModel {
    private PostRepository postRepository = PostRepository.getInstance();

    public MutableLiveData<List<Post>> getPostsForUser(int user_id) {
        return postRepository.getUserPosts(user_id);
    }

    public void commentPost(Comment comment, Post post)
    {
        postRepository.commentPost(comment, post);
    }

    public void likePost(Post post, User user)
    {
        postRepository.likePost(post, user);
    }

    public void dislikePost(Post post, User user)
    {
        postRepository.dilikePost(post, user);
    }

    public MutableLiveData<List<Comment>> getPostComments(Post post) {
        return postRepository.getPostComments(post);
    }
}
