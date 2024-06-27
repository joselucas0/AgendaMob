package com.example.agendamob;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private CalendarView calendarView;
    private LinearLayout layoutEventos;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicia a tela de login ao abrir o aplicativo
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment())
                .commit();

        calendarView = findViewById(R.id.calendarView4);
        layoutEventos = findViewById(R.id.layoutEventos);
        Button btnAdicionarEvento = findViewById(R.id.buttonAdicionarE);
        Button btnVoltar = findViewById(R.id.buttonVoltar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnAdicionarEvento.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateEventActivity.class);
            startActivity(intent);
        });

        btnVoltar.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            loadEvents(selectedDate);
        });

        // Load today's events initially
        long currentTime = System.currentTimeMillis();
        calendarView.setDate(currentTime, false, true);
        loadEvents(getFormattedDate(currentTime));
    }

    private void loadEvents(String date) {
        layoutEventos.removeAllViews();
        db.collection("events")
                .whereEqualTo("date", date)
                .whereEqualTo("userId", mAuth.getCurrentUser().getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String eventId = document.getId();
                            String eventName = document.getString("name");

                            LinearLayout eventLayout = new LinearLayout(MainActivity.this);
                            eventLayout.setOrientation(LinearLayout.HORIZONTAL);
                            eventLayout.setPadding(8, 8, 8, 8);

                            TextView eventNameView = new TextView(MainActivity.this);
                            eventNameView.setText(eventName);
                            eventNameView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

                            Button btnFeito = new Button(MainActivity.this);
                            btnFeito.setText("Feito");
                            btnFeito.setOnClickListener(v -> markEventAsDone(eventId));

                            Button btnExcluir = new Button(MainActivity.this);
                            btnExcluir.setText("Excluir");
                            btnExcluir.setOnClickListener(v -> deleteEvent(eventId));

                            eventLayout.addView(eventNameView);
                            eventLayout.addView(btnFeito);
                            eventLayout.addView(btnExcluir);

                            layoutEventos.addView(eventLayout);
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Erro ao carregar eventos", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void markEventAsDone(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.update("done", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Evento concluído!", Toast.LENGTH_SHORT).show();
                    loadEvents(getFormattedDate(calendarView.getDate()));
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erro ao marcar evento como concluído", Toast.LENGTH_SHORT).show());
    }

    private void deleteEvent(String eventId) {
        DocumentReference eventRef = db.collection("events").document(eventId);
        eventRef.delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Evento excluído!", Toast.LENGTH_SHORT).show();
                    loadEvents(getFormattedDate(calendarView.getDate()));
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erro ao excluir evento", Toast.LENGTH_SHORT).show());
    }

    private String getFormattedDate(long date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("d/M/yyyy");
        return sdf.format(new java.util.Date(date));
    }
}
