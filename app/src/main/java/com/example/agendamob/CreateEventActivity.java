package com.example.agendamob;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventDateEditText;
    private EditText eventTimeEditText;
    private Button selectSoundButton;
    private Button createEventButton;
    private Uri selectedSoundUri;

    private static final int REQUEST_CODE_PICK_SOUND = 1;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        eventNameEditText = findViewById(R.id.event_name);
        eventDateEditText = findViewById(R.id.event_date);
        eventTimeEditText = findViewById(R.id.event_time);
        selectSoundButton = findViewById(R.id.select_sound);
        createEventButton = findViewById(R.id.create_event);

        eventDateEditText.setOnClickListener(v -> showDatePickerDialog());
        eventTimeEditText.setOnClickListener(v -> showTimePickerDialog());

        selectSoundButton.setOnClickListener(v -> selectSound());

        createEventButton.setOnClickListener(v -> createEvent());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            eventDateEditText.setText(String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1));
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
            eventTimeEditText.setText(String.format("%02d:%02d", hourOfDay, minute1));
        }, hour, minute, true);
        timePickerDialog.show();
    }

    private void selectSound() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Escolha um som para o alarme");
        startActivityForResult(intent, REQUEST_CODE_PICK_SOUND);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_SOUND && resultCode == RESULT_OK && data != null) {
            selectedSoundUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (selectedSoundUri != null) {
                Toast.makeText(this, "Som selecionado: " + selectedSoundUri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void createEvent() {
        String eventName = eventNameEditText.getText().toString();
        String eventDate = eventDateEditText.getText().toString();
        String eventTime = eventTimeEditText.getText().toString();

        if (eventName.isEmpty() || eventDate.isEmpty() || eventTime.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidDate(eventDate)) {
            Toast.makeText(this, "Data inválida, por favor, insira novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isValidTime(eventTime)) {
            Toast.makeText(this, "Hora inválida, por favor, insira novamente", Toast.LENGTH_SHORT).show();
            return;
        }

        // Continue com a lógica para criar o evento
        Toast.makeText(this, "Evento criado com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private boolean isValidDate(String date) {
        try {
            dateFormat.setLenient(false);
            Date parsedDate = dateFormat.parse(date);
            return parsedDate != null;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidTime(String time) {
        try {
            timeFormat.setLenient(false);
            Date parsedTime = timeFormat.parse(time);
            return parsedTime != null;
        } catch (ParseException e) {
            return false;
        }
    }
}
