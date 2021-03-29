package com.entwickler.spacex.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {PersonRoom.class}, version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {

    public abstract DAO dao();

}