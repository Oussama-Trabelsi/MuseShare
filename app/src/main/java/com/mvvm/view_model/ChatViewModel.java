package com.mvvm.view_model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mvvm.model.Conversation;
import com.mvvm.model.Message;
import com.mvvm.repository.ChatRepository;

import java.util.List;

public class ChatViewModel extends ViewModel {
    private ChatRepository chatRepository = ChatRepository.getInstance();

    public MutableLiveData<List<Conversation>> getConversations() {
        return chatRepository.getConversations();
    }

    public MutableLiveData<List<Message>> getConversation(int reciever_id) {
        return chatRepository.getConversation(reciever_id);
    }

    public void sendMessage(int reciever_id, String content)
    {
        chatRepository.sendMessage(reciever_id, content);
    }

}
