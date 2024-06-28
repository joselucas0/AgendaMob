package com.example.agendamob;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDao {
    @Insert
    void insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(int id);

    @Query("SELECT * FROM events WHERE date = :date AND userId = :userId")
    List<Event> getEventsByDateAndUserId(String date, int userId);

    @Query("UPDATE events SET done = :done WHERE id = :eventId")
    void updateEventStatus(int eventId, boolean done);

    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteEventById(int eventId);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents(); // Adiciona este método para buscar todos os eventos

}
