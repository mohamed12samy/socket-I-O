package com.example.socketiodemo;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Repository {

    private static Repository mRepository;

    private static boolean isConnected;
    private static String UserName = "sami";
    final MutableLiveData<JSONObject> newMessage = new MutableLiveData<>();
    final MutableLiveData<JSONObject> userJoined = new MutableLiveData<>();
    final MutableLiveData<JSONObject> userLeft = new MutableLiveData<>();
    final MutableLiveData<JSONObject> Typing = new MutableLiveData<>();
    final MutableLiveData<JSONObject> stopTyping = new MutableLiveData<>();

    private Socket mSocket;
    private final Handler handler;

    Emitter.Listener onNewMessage;
    Emitter.Listener onUserJoined;
    Emitter.Listener onUserLeft;
    Emitter.Listener onTyping;
    Emitter.Listener onStopTyping;
    private Repository() {
        handler = new Handler(MyApplication.getInstance().getMainLooper());

    }

    public static synchronized Repository getInstance() {
        if (mRepository == null) {
            mRepository = new Repository();
        }
        return mRepository;
    }

    public void on(){
        mSocket = MyApplication.getInstance().getSocket();
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("new message", onNewMessage);
        mSocket.on("user joined", onUserJoined);
        mSocket.on("user left", onUserLeft);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.connect();
    }
    public void off(){
        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("user joined", onUserJoined);
        mSocket.off("user left", onUserLeft);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }
    public void emit(String event){
        mSocket.emit(event);
    }
    public void emitMessage(String event,String message){
        mSocket.emit(event,message);
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!isConnected) {
                        if (null != UserName)
                            mSocket.emit("add user", UserName);
                        Toast.makeText(MyApplication.getInstance().getApplicationContext(),
                                "connect", Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i("TAG", "diconnected");
                    isConnected = false;
                    Toast.makeText(MyApplication.getInstance(),
                            "Disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("TAG", "Error connecting");
                    Toast.makeText(MyApplication.getInstance(),
                            "Error connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    LiveData<JSONObject> getNewMessage() {
         onNewMessage = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        newMessage.postValue(data);
                    }
                });
            }
        };
        return newMessage;
    }

    public LiveData<JSONObject> UserJoined() {

        onUserJoined = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        userJoined.postValue(data);
                    }
                });
            }
        };
        return userJoined;
    }

    public LiveData<JSONObject> userLeft() {
        onUserLeft = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        userLeft.postValue(data);
                    }
                });
            }
        };

        return userLeft;
    }
    public LiveData<JSONObject> Typing() {
        onTyping = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        Typing.postValue(data);
                    }
                });
            }
        };
        return Typing;
    }
    public LiveData<JSONObject> stopTyping() {
        onStopTyping = new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        stopTyping.postValue(data);
                    }
                });
            }
        };
        return stopTyping;
    }

    private void runOnUiThread(Runnable r) {
        handler.post(r);
    }

    public void insertMessages(List<Message> messages){
        MessagesDatabase.getInstance().dao().insertMessages(messages);
    }

    public List<Message> getMessages()
    {
        List<Message> messages = MessagesDatabase.getInstance().dao().getMessages();

        return messages;
    }
}
