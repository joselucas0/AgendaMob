package com.example.agendamob;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class EventDetailsActivity extends AppCompatActivity {
    private long eventId;
    private AppDatabase db;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("EVENT_ID")) {
            eventId = intent.getLongExtra("EVENT_ID", -1);
        }

        if (intent != null && intent.hasExtra("LOGGED_IN_USER")) {
            loggedInUser = (User) intent.getSerializableExtra("LOGGED_IN_USER");
        }

        if (eventId == -1) {
            Toast.makeText(this, "Erro: ID do evento inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db = AppDatabase.getInstance(this);

        loadEventDetails(eventId);
    }

    private void loadEventDetails(long eventId) {
        new LoadEventDetailsTask().execute(eventId);
    }

    private class LoadEventDetailsTask extends AsyncTask<Long, Void, Event> {
        @Override
        protected Event doInBackground(Long... longs) {
            long eventId = longs[0];
            return db.eventDao().getEventById(eventId);
        }

        @Override
        protected void onPostExecute(Event event) {
            if (event != null) {
                // Atualiza a UI com os detalhes do evento
                TextView eventNameView = findViewById(R.id.event_name);
                TextView eventDateView = findViewById(R.id.event_date);
                TextView eventUserIdView = findViewById(R.id.event_user_id);
                TextView eventCompletedView = findViewById(R.id.event_completed);
                TextView eventAlarmSoundView = findViewById(R.id.event_alarm_sound);

                eventNameView.setText(event.getName());
                eventDateView.setText(event.getDateTime());
                eventUserIdView.setText("User ID: " + event.getUserId());
                eventCompletedView.setText("Status: " + (event.isCompleted() ? "Completed" : "Not Completed"));
                eventAlarmSoundView.setText("Alarm Sound: " + event.getAlarmSound());

                // Define o título da ActionBar com o nome do evento
                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(event.getName());
                }

            } else {
                Toast.makeText(EventDetailsActivity.this, "Erro: Evento não encontrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
