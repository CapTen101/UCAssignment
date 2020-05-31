package com.example.ucassignment;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {toDoItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract toDoDAO todoDao();
}
