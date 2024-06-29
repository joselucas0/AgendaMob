package com.example.agendamob;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "events")
public class Event implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "dateTime")
    private String dateTime;

    @ColumnInfo(name = "userId")
    private int userId;

    @ColumnInfo(name = "completed")
    private boolean completed;

    @ColumnInfo(name = "alarmSound")
    private String alarmSound; // Novo campo para armazenar o som de alarme selecionado

    public Event(String name, String dateTime, int userId, boolean completed) {
        this.name = name;
        this.dateTime = dateTime;
        this.userId = userId;
        this.completed = completed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getAlarmSound() {
        return alarmSound;
    }

    public void setAlarmSound(String alarmSound) {
        this.alarmSound = alarmSound;
    }
}
