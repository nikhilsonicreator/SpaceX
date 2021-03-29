package com.entwickler.spacex.Room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    public void personInsertion(PersonRoom personRoom);

    @Query("Select * From PersonRoom")
    List<PersonRoom> getPerson();

    @Query("Delete From PersonRoom")
    void deleteAll();
}
