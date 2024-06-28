package com.example.agendamob;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private LinearLayout layoutEventos;
    private AppDatabase db;
    private User loggedInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        calendarView = findViewById(R.id.calendarView4);
        layoutEventos = findViewById(R.id.layoutEventos);
        Button btnAdicionarEvento = findViewById(R.id.buttonAdicionarE);
        Button btnVoltar = findViewById(R.id.buttonVoltar);

        db = AppDatabase.getInstance(this);

        btnAdicionarEvento.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Criação de evento ainda não implementada", Toast.LENGTH_SHORT).show();
        });

        btnVoltar.setOnClickListener(v -> {
            loggedInUser = null;
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            loadEvents(selectedDate);
        });

        // Recebe o usuário logado ao iniciar a atividade
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("LOGGED_IN_USER")) {
            loggedInUser = (User) intent.getSerializableExtra("LOGGED_IN_USER");
        }

        // Verifica se o usuário está logado antes de carregar os eventos
        if (loggedInUser != null) {
            long currentTime = System.currentTimeMillis();
            calendarView.setDate(currentTime, false, true);
            loadEvents(getFormattedDate(currentTime));
        } else {
            Toast.makeText(this, "Erro: Usuário não está logado", Toast.LENGTH_SHORT).show();
            // Redireciona para a tela de login
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void loadEvents(String date) {
        layoutEventos.removeAllViews();
        new LoadEventsTask().execute(date);
    }

    private void markEventAsDone(int eventId) {
        new MarkEventAsDoneTask().execute(eventId);
    }

    private void deleteEvent(int eventId) {
        new DeleteEventTask().execute(eventId);
    }

    private String getFormattedDate(long date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
        return sdf.format(new java.util.Date(date));
    }

    private class LoadEventsTask extends AsyncTask<String, Void, List<Event>> {
        @Override
        protected List<Event> doInBackground(String... strings) {
            String date = strings[0];
            return db.eventDao().getEventsByDateAndUserId(date, loggedInUser.getId());
        }

        @Override
        protected void onPostExecute(List<Event> events) {
            for (Event event : events) {
                LinearLayout eventLayout = new LinearLayout(MainActivity.this);
                eventLayout.setOrientation(LinearLayout.HORIZONTAL);
                eventLayout.setPadding(8, 8, 8, 8);

                TextView eventNameView = new TextView(MainActivity.this);
                eventNameView.setText(event.getName());
                eventNameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                Button btnFeito = new Button(MainActivity.this);
                btnFeito.setText("Feito");
                btnFeito.setOnClickListener(v -> markEventAsDone(event.getId()));

                Button btnExcluir = new Button(MainActivity.this);
                btnExcluir.setText("Excluir");
                btnExcluir.setOnClickListener(v -> deleteEvent(event.getId()));

                eventLayout.addView(eventNameView);
                eventLayout.addView(btnFeito);
                eventLayout.addView(btnExcluir);

                layoutEventos.addView(eventLayout);
            }
        }
    }

    private class MarkEventAsDoneTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            int eventId = integers[0];
            Event event = db.eventDao().getEventById(eventId);
            event.setDone(true);
            db.eventDao().update(event);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Evento concluído!", Toast.LENGTH_SHORT).show();
            loadEvents(getFormattedDate(calendarView.getDate()));
        }
    }

    private class DeleteEventTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... integers) {
            int eventId = integers[0];
            Event event = db.eventDao().getEventById(eventId);
            db.eventDao().delete(event);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "Evento excluído!", Toast.LENGTH_SHORT).show();
            loadEvents(getFormattedDate(calendarView.getDate()));
        }
    }
}




