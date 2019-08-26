package com.example.socketiodemo;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String UserName = "sami";
    //Socket mSocket;

    private ChatViewModel chatViewModel = new ChatViewModel();
    RecyclerView recyclerView;

    RecyclerView.Adapter adapter;
    List<Message> messages = new ArrayList<Message>();
    private Handler mTypingHandler = new Handler();


    EditText editText;
    ImageButton send;

    boolean isTyping = false;
    //boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatViewModel = ViewModelProviders.of(this).get(ChatViewModel.class);


        editText = findViewById(R.id.sendText);
        send = findViewById(R.id.sendButton);

        messages.clear();
        messages.addAll(chatViewModel.getMessages());

        adapter = new MessageAdapter(getApplicationContext(), messages);
        recyclerView = findViewById(R.id.recylcer);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                attemptSend();
                return false;
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!isTyping) {
                    isTyping = true;
                    chatViewModel.emmit("typing");
                }
                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, 600);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSend();
            }
        });


        chatViewModel.getNewMessage().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                String username = "";
                String message = "";
                try {
                    username = jsonObject.getString("username");
                    message = jsonObject.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                removeTyping(username);
                addMessage(username, message, false);
            }
        });

        chatViewModel.userJoined().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                String username = "";
                int numUsers = 0;
                try {
                    username = jsonObject.getString("username");
                    numUsers = jsonObject.getInt("numUsers");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addLog("joined  " + username);
                addParticipantsLog(numUsers);
            }
        });

        chatViewModel.userLeft().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                String username = "";
                int numUsers = 0;
                try {
                    username = jsonObject.getString("username");
                    numUsers = jsonObject.getInt("numUsers");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                addLog("Left  " + username);
                addParticipantsLog(numUsers);
                removeTyping(username);
            }
        });
        chatViewModel.onTyping().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                String username;
                try {
                    username = jsonObject.getString("username");
                } catch (JSONException e) {
                    Log.e("TAG", e.getMessage());
                    return;
                }
                addTyping(username);
            }
        });
        chatViewModel.stopTyping().observe(this, new Observer<JSONObject>() {
            @Override
            public void onChanged(JSONObject jsonObject) {
                String username;
                try {
                    username = jsonObject.getString("username");
                    Log.d("TAG", username);
                } catch (JSONException e) {
                    Log.e("TAG", e.getMessage());
                    return;
                }
                removeTyping(username);
            }
        });
        chatViewModel.on();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /*for (int i = messages.size() - 1; i >= 0; i--)
            if (messages.get(i).getType() == Message.TYPE_ACTION) messages.remove(i);

        chatViewModel.insertMessages(messages);*/
        chatViewModel.off();
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (int i = messages.size() - 1; i >= 0; i--)
            if (messages.get(i).getType() == Message.TYPE_ACTION) messages.remove(i);

        chatViewModel.insertMessages(messages);
        chatViewModel.off();
    }

    private void addMessage(String userName, String message, boolean isMymessage) {
        messages.add(new Message.Builder(isMymessage ? Message.TYPE_MY_MESSAGE : Message.TYPE_MESSAGE )
                .username(userName).message(message).isMyMessage(isMymessage).build());
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addLog(String message) {
        messages.add(new Message.Builder(Message.TYPE_LOG)
                .message(message).build());
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void addTyping(String username) {
        messages.add(new Message.Builder(Message.TYPE_ACTION)
                .username(username).build());
        adapter.notifyItemInserted(messages.size() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    private void removeTyping(String UserName) {
        for (int i = messages.size() - 1; i >= 0; i--) {
            if (messages.get(i).getType() == Message.TYPE_ACTION &&
                    messages.get(i).getUsername().equals(UserName)) {
                messages.remove(i);
                adapter.notifyItemRemoved(i);
            }
        }
    }

    private void addParticipantsLog(int numUsers) {
        addLog(getResources().getQuantityString(R.plurals.message_participants, numUsers, numUsers));
    }

    private void attemptSend() {
        if (UserName == null ) return;

        isTyping = false;

        String message = editText.getText().toString().trim();
        if (message.isEmpty()) return;

        editText.setText("");
        addMessage(UserName, message, true);
        chatViewModel.emmitMessage("new message", message);
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!isTyping) return;

            isTyping = false;
            chatViewModel.emmit("stop typing");
        }
    };
}
