package com.example.agendamob;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        btnRegister = view.findViewById(R.id.btn_register);

        // Inicializa o banco de dados
        db = AppDatabase.getInstance(requireContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navega para a tela de cadastro
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Realiza o login em uma thread separada
        new LoginTask().execute(email, password);
    }

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
                // Login bem sucedido, navegue para a próxima tela ou realize a ação desejada
                Toast.makeText(requireContext(), "Login bem sucedido!", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginSuccessFragment())
                        .addToBackStack(null)
                        .commit();
            } else {
                // Falha no login, exiba uma mensagem de erro
                Toast.makeText(requireContext(), "Credenciais inválidas", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
