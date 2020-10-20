package com.mvvm.model;

import com.mvvm.util.Session;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Comment implements Serializable {
    private int id;
    private String content;
    private Date date;
    private User user;
    private List<User> likes;
    private int likesCount;

    public Comment(String content) {
        this.content = content;
        this.user = Session.getInstance().getUser();
    }

    public Comment(int id, String content, Date date, User user, List<User> likes, int likesCount) {
        this.id = id;
        this.content = content;
        this.date = date;
        this.user = user;
        this.likes = likes;
        this.likesCount = likesCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getLikes() {
        return likes;
    }

    public void setLikes(List<User> likes) {
        this.likes = likes;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

}
