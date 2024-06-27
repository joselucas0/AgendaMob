package com.example.agendamob;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_login);

        // Inicializa as views
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);

        // Inicializa o banco de dados
        db = AppDatabase.getInstance(this);

        // Configura o listener do botão de login
        btnLogin.setOnClickListener(v -> login());

        // Configura o listener do botão de registro
        btnRegister.setOnClickListener(v -> {
            // Navega para a tela de registro
            navigateToRegisterActivity();
        });
    }

    // Método para realizar o login
    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Valida se os campos estão preenchidos
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realiza o login em uma thread separada usando AsyncTask
        new LoginTask().execute(email, password);
    }

    // AsyncTask para realizar o login em segundo plano
    private class LoginTask extends AsyncTask<String, Void, User> {
        @Override
        protected User doInBackground(String... strings) {
            String email = strings[0];
            String password = strings[1];
            return db.userDao().login(email, password);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                // Login bem sucedido, navega para a próxima tela ou realiza a ação desejada
                Toast.makeText(LoginActivity.this, "Login bem sucedido!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("LOGGED_IN_USER", user);
                startActivity(intent);
                finish();
            } else {
                // Falha no login, exibe uma mensagem de erro
                Toast.makeText(LoginActivity.this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para navegar para a RegisterActivity
    private void navigateToRegisterActivity() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}
