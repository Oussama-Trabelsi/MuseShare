package com.mvvm.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Post implements Serializable {
    private int id;
    private String description;
    private String trackUrl;
    private String iconUrl;
    private String trackName;
    private Date postDate;
    private User user;
    private int likesCount;
    private int commentsCount;
    private List<User> likes;
    private List<Comment> comments;


    public Post() {
    }

    public Post(int id, String description, String trackUrl, String iconUrl, String trackName, Date postDate, User user, int likesCount, int commentsCount, List<User> likes) {
        this.id = id;
        this.description = description;
        this.trackUrl = trackUrl;
        this.iconUrl = iconUrl;
        this.trackName = trackName;
        this.postDate = postDate;
        this.user = user;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.likes = likes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
