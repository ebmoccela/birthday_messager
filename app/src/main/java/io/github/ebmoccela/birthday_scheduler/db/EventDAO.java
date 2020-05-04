package io.github.ebmoccela.birthday_scheduler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDAO {

    @Insert
    void insert(Event ... events);

    @Query("SELECT * FROM Event")

    LiveData<List<Event>> getAll();

    @Update
    void update(Event ... events);

    @Delete
    void delete(Event ... events);

    //view details of event

}
