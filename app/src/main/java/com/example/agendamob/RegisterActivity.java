package com.example.agendamob;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnRegister = findViewById(R.id.btn_register);

        db = AppDatabase.getInstance(this);

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        new CheckUserTask().execute(email);
    }

    private class CheckUserTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... strings) {
            String email = strings[0];
            return db.userDao().findByEmail(email);
        }

        @Override
        protected void onPostExecute(User existingUser) {
            if (existingUser != null) {
                Toast.makeText(RegisterActivity.this, "Email já registrado!", Toast.LENGTH_SHORT).show();
            } else {
                new InsertUserTask().execute();
            }
        }
    }

    private class InsertUserTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            String hashedPassword = PasswordUtils.hashPassword(password);

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setHashedPassword(hashedPassword);
            db.userDao().insert(newUser);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(RegisterActivity.this, "Usuário registrado com sucesso!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
