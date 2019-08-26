package com.example.socketiodemo;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import java.util.List;

@Dao
public interface MeesagesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(List<Message> movies);


    @Query("SELECT * FROM messages")
    List<Message> getMessages();

}
