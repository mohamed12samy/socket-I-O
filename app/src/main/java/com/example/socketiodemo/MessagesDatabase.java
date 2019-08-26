package com.example.socketiodemo;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Message.class,version = 1)
public abstract class MessagesDatabase extends RoomDatabase{


        private static MessagesDatabase INSTANCE;

        public abstract MeesagesDao dao();

        public static MessagesDatabase getInstance()
        {
            if(INSTANCE == null){
                INSTANCE = Room.databaseBuilder(MyApplication.getInstance().getApplicationContext(), MessagesDatabase.class,
                        "messages_database").allowMainThreadQueries().build();
            }
            return INSTANCE;
        }


}
