package com.example.socketiodemo;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<JSONObject> newMesaage = new MutableLiveData<>();
    private MutableLiveData<JSONObject> userJoined = new MutableLiveData<>();
    private MutableLiveData<JSONObject> userLeft = new MutableLiveData<>();
    private MutableLiveData<JSONObject> Typing = new MutableLiveData<>();
    private MutableLiveData<JSONObject> stopTyping = new MutableLiveData<>();

    private Repository mRepository;

    public ChatViewModel(){
        mRepository = Repository.getInstance();
    }

    public void on(){
        mRepository.on();
    }
    public void off(){
        mRepository.off();
    }
    public void emmit(String event){
        mRepository.emit(event);
    }
    public void emmitMessage(String event,String message){
        mRepository.emitMessage(event,message);
    }
    public LiveData<JSONObject> getNewMessage(){

        newMesaage = (MutableLiveData<JSONObject>) mRepository.getNewMessage();
        return newMesaage;
    }

    public LiveData<JSONObject> userJoined(){

        userJoined = (MutableLiveData<JSONObject>) mRepository.UserJoined();
        return userJoined;
    }
    public LiveData<JSONObject> userLeft(){

        userLeft = (MutableLiveData<JSONObject>) mRepository.userLeft();
        return userLeft;
    }
    public LiveData<JSONObject> onTyping(){

        Typing = (MutableLiveData<JSONObject>) mRepository.Typing();
        return Typing;
    }
    public LiveData<JSONObject> stopTyping(){

        stopTyping = (MutableLiveData<JSONObject>) mRepository.stopTyping();
        return stopTyping;
    }

    public void insertMessages(List<Message> list ){
        mRepository.insertMessages(list);
    }
    public List<Message> getMessages(){
        return mRepository.getMessages();
    }
}
