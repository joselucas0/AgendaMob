package com.example.agendamob;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {
    private EditText etName, etDate, etTime;
    private Button btnCreateEvent;
    private Spinner spinnerAlarmSound;
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
        spinnerAlarmSound = findViewById(R.id.spinner_alarm_sound);

        db = AppDatabase.getInstance(this);

        // Configurar o Spinner com os sons de alarme
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.alarm_sounds, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAlarmSound.setAdapter(adapter);

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
        String selectedSound = spinnerAlarmSound.getSelectedItem().toString();

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

            // Criar o evento com a data formatada, o som de alarme selecionado e o ID do usuário logado
            Event event = new Event(name, formattedDateTime, loggedInUser.getId(), false);
            event.setAlarmSound(selectedSound);

            new CreateEventTask().execute(event); // Executar AsyncTask para inserir o evento no banco de dados
        } catch (ParseException e) {
            Toast.makeText(this, "Erro ao parsear data/hora!", Toast.LENGTH_SHORT).show();
        }
    }

    private class CreateEventTask extends AsyncTask<Event, Void, Long> {
        @Override
        protected Long doInBackground(Event... events) {
            return db.eventDao().insert(events[0]); // Retorna o ID do evento inserido
        }

        @Override
        protected void onPostExecute(Long eventId) {
            if (eventId != -1) {
                Toast.makeText(CreateEventActivity.this, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show();

                // Navegar para a tela EventDetailsActivity após salvar o evento
                Intent intent = new Intent(CreateEventActivity.this, EventDetailsActivity.class);
                intent.putExtra("EVENT_ID", eventId); // Passar o ID do evento criado para a Activity de detalhes
                startActivity(intent);

                finish(); // Finalizar a atividade atual se necessário
            } else {
                Toast.makeText(CreateEventActivity.this, "Erro ao criar evento!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
