package com.mvvm.model;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable {
    private int id;
    private String content;
    private String status;
    private Date date;
    private User sender;
    private User reciever;
    private Conversation conversation;

    public Message(String content, User sender, User reciever, Conversation conversation) {
        this.content = content;
        this.sender = sender;
        this.reciever = reciever;
        this.conversation = conversation;
    }

    public Message(int id, String content, String status, Date date, User sender, User reciever, Conversation conversation) {
        this.id = id;
        this.content = content;
        this.status = status;
        this.date = date;
        this.sender = sender;
        this.reciever = reciever;
        this.conversation = conversation;
    }

    public Message(int id, String content, String status, Date date, User sender, User reciever) {
        this.id = id;
        this.content = content;
        this.status = status;
        this.date = date;
        this.sender = sender;
        this.reciever = reciever;
    }

    public Message(int id, String content, String status, Date date) {
        this.id = id;
        this.content = content;
        this.status = status;
        this.date = date;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReciever() {
        return reciever;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
