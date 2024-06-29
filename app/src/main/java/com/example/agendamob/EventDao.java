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
    Long insert(Event event);

    @Update
    void update(Event event);

    @Delete
    void delete(Event event);

    @Query("SELECT * FROM events WHERE id = :id")
    Event getEventById(long id);

    @Query("SELECT * FROM events WHERE dateTime LIKE :date AND userId = :userId")
    List<Event> getEventsByDateAndUserId(String date, int userId);

    @Query("UPDATE events SET completed = :done WHERE id = :eventId")
    void updateEventStatus(int eventId, boolean done);

    @Query("DELETE FROM events WHERE id = :eventId")
    void deleteEventById(int eventId);

    @Query("SELECT * FROM events")
    List<Event> getAllEvents();
}
