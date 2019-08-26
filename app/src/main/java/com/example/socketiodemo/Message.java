package com.example.socketiodemo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message {

    public static final int TYPE_MY_MESSAGE = 0;
    public static final int TYPE_MESSAGE = 1;
    public static final int TYPE_LOG = 2;
    public static final int TYPE_ACTION = 3;

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;
    @ColumnInfo(name = "type")
    private int mType;
    @ColumnInfo(name = "message")
    private String mMessage;
    @ColumnInfo(name = "userName")
    private String mUsername;
    /*@ColumnInfo(name = "my_message")
    private boolean myMessage;
*/
    public Message() {
    }


    public void setMType(int mType) {
        this.mType = mType;
    }

    public void setMMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setMUsername(String mUsername) {
        this.mUsername = mUsername;
    }

/*
    public void setMyMessage(boolean myMessage) {
        this.myMessage = myMessage;
    }
*/

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return mType;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return mMessage;
    }

    public String getUsername() {
        return mUsername;
    }
/*
    public boolean isMyMessage() {
        return myMessage;
    }*/

    public static class Builder {
        private final int mType;
        private String mUsername;
        private String mMessage;
        private boolean myMessage;

        public Builder(int type) {
            mType = type;
        }

        public Builder username(String username) {
            mUsername = username;
            return this;
        }

        public Builder message(String message) {
            mMessage = message;
            return this;
        }

        public Builder isMyMessage(boolean isMymessage) {
            myMessage = isMymessage;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.mType = mType;
            message.mUsername = mUsername;
            message.mMessage = mMessage;
//            message.myMessage = myMessage;
            return message;
        }
    }
}
