package com.example.agendamob;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {
    private EditText etName, etDate, etTime;
    private Button btnCreateEvent;
    private AppDatabase db;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        etName = findViewById(R.id.event_name);
        etDate = findViewById(R.id.event_date);
        etTime = findViewById(R.id.event_time);
        btnCreateEvent = findViewById(R.id.create_event);

        db = AppDatabase.getInstance(this);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USER")) {
            loggedInUser = (User) intent.getSerializableExtra("LOGGED_IN_USER");
        } else {
            Toast.makeText(this, "Erro: Usuário não está logado", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(CreateEventActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }

        btnCreateEvent.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (loggedInUser == null) {
            Toast.makeText(this, "Erro: Usuário não está logado", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(CreateEventActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy H:m");
            Date eventDate = dateFormat.parse(date + " " + time);
            String formattedDateTime = dateFormat.format(eventDate); // Formata data e hora para armazenar na classe Event
            new CreateEventTask().execute(new Event(name, formattedDateTime, loggedInUser.getId(), false)); // Cria o evento com a data formatada
        } catch (ParseException e) {
            Toast.makeText(this, "Erro ao parsear data/hora!", Toast.LENGTH_SHORT).show();
        }
    }

    private class CreateEventTask extends AsyncTask<Event, Void, Void> {
        @Override
        protected Void doInBackground(Event... events) {
            db.eventDao().insert(events[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(CreateEventActivity.this, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
