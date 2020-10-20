package com.mvvm.model;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role;
    private String bio;
    private String image_url;
    private boolean isPrivate;
    private List<User> followers;
    private List<User> following;
    private int followersCount;
    private int followingCount;
    private int postsCount;
    private boolean isOnline;


    public User() { }

    public User(int id, String username, String firstName, String lastName, String email, String password, String role, String bio, int followersCount, int followingCount, String image_url, boolean isPrivate, boolean isOnline, int postsCount) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.image_url = image_url;
        this.bio = bio;
        this.isPrivate = isPrivate;
        this.isOnline = isOnline;
        this.postsCount = postsCount;
    }

    public User(int id, String username, String firstName, String lastName, String email, String password, String role, String bio, int followersCount, int followingCount, String image_url, boolean isPrivate, boolean isOnline, int postsCount, List<User> followers, List<User> following) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.image_url = image_url;
        this.bio = bio;
        this.isPrivate = isPrivate;
        this.isOnline = isOnline;
        this.postsCount = postsCount;
        this.followers = followers;
        this.following = following;
    }


    public User(int id, String username, String firstName, String lastName, String email, String password, String role, String bio, int followersCount, int followingCount, String image_url, boolean isPrivate, List<User> following, int postsCount) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.image_url = image_url;
        this.bio = bio;
        this.isPrivate = isPrivate;
        this.following = following;
        this.postsCount = postsCount;
    }
//
    public User(String username, String firstName, String lastName, String email, String password, String role, int followersCount, int followingCount, String image_url) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.image_url = image_url;
    }

    /* All but password */
    public User(int id, String username, String firstName, String lastName, String email, String role, String bio,
                String image_url, boolean isPrivate, List<User> followers, List<User> following, int followersCount, int followingCount) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
        this.bio = bio;
        this.image_url = image_url;
        this.isPrivate = isPrivate;
        this.followers = followers;
        this.following = following;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public int getPostsCount() { return postsCount; }

    public void setPostsCount(int postsCount) { this.postsCount = postsCount; }
}
