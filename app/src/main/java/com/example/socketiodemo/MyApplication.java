package com.example.socketiodemo;

import android.app.Application;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MyApplication extends Application {

    private static MyApplication mMyApplication;

    public static synchronized MyApplication getInstance(){
        return mMyApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mMyApplication = this;

        try {
            mSocket = IO.socket("https://socket-io-chat.now.sh/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

    }

    private Socket mSocket;



    public Socket getSocket() {
        return mSocket;
    }
}
