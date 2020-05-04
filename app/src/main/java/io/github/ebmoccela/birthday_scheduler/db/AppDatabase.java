package io.github.ebmoccela.birthday_scheduler.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Event.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if(instance != null){
            return instance;
        }
        instance = Room.databaseBuilder(context, AppDatabase.class, "event-db").build();
        return instance;
    }

    public abstract EventDAO eventDAO();
}
