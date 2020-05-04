package io.github.ebmoccela.birthday_scheduler.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Event {
    @PrimaryKey(autoGenerate = true)
    protected int pk_id;

    @ColumnInfo(name="event_text")
    protected String text;

    @ColumnInfo(name="event_number")
    protected String number;

    @ColumnInfo(name="event_time")
    protected String time;

    @ColumnInfo(name="event_occurance")
    protected String occurance;

    //should be the whole date including timezone
    @ColumnInfo(name="event_datetime")
    protected String timestamp;

    @Ignore
    public Event(int pk_id, String text, String number, String time, String occurance, String timestamp) {
        this.pk_id = pk_id;
        this.text = text;
        this.number = number;
        this.time = time;
        this.occurance = occurance;
        this.timestamp = timestamp;
    }

    public Event(String text, String number, String time, String occurance, String timestamp) {
        this.text = text;
        this.number = number;
        this.time = time;
        this.occurance = occurance;
        this.timestamp = timestamp;
    }

    public int getPk_id() {
        return pk_id;
    }

    public void setPk_id(int pk_id) {
        this.pk_id = pk_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOccurance() {
        return occurance;
    }

    public void setOccurance(String occurance) {
        this.occurance = occurance;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
