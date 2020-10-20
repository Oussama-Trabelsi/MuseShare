package com.mvvm.model;

import java.util.List;

public class Conversation {
    private int id;
    private User firs_user;
    private User second_user;
    private List<Message> messages;
    private Message last_message;

    public Conversation(int id, User firs_user, User second_user, List<Message> messages) {
        this.id = id;
        this.firs_user = firs_user;
        this.second_user = second_user;
        this.messages = messages;
    }

    public Conversation(int id, User firs_user, User second_user) {
        this.id = id;
        this.firs_user = firs_user;
        this.second_user = second_user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getFirs_user() {
        return firs_user;
    }

    public void setFirs_user(User firs_user) {
        this.firs_user = firs_user;
    }

    public User getSecond_user() {
        return second_user;
    }

    public void setSecond_user(User second_user) {
        this.second_user = second_user;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Message getLast_message() {
        return last_message;
    }

    public void setLast_message(Message last_message) {
        this.last_message = last_message;
    }
}
