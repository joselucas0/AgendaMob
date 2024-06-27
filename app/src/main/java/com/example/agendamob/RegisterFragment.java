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

public class RegisterFragment extends Fragment {
    private EditText etEmail, etPassword;
    private Button btnRegister;
    private AppDatabase db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnRegister = view.findViewById(R.id.btn_register);

        // Inicializa o banco de dados
        db = AppDatabase.getInstance(requireContext());

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return view;
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Por favor, preencha todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verifica se o email já está cadastrado em uma thread separada
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
                Toast.makeText(requireContext(), "Email já cadastrado!", Toast.LENGTH_SHORT).show();
            } else {
                // Cria um novo usuário em uma thread separada
                new InsertUserTask().execute();
            }
        }
    }

    private class InsertUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            db.userDao().insert(newUser);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(requireContext(), "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
            // Volta para a tela de login após o cadastro
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
