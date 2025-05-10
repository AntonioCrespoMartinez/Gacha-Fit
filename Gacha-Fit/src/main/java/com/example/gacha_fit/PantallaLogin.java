package com.example.gacha_fit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gacha_fit.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class PantallaLogin extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView togglePasswordView;
    private boolean isPasswordVisible = false;
    private CheckBox rememberMeCheckBox;
    private TextView registrarse;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantalla_login, container, false);

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        togglePasswordView = view.findViewById(R.id.togglePasswordView);
        registrarse = view.findViewById(R.id.registrarse);
        rememberMeCheckBox = view.findViewById(R.id.recordar);

        Bundle args = getArguments();
        if (args != null) {
            String emailRecibido = args.getString("email", "");
            String passwordRecibido = args.getString("password", "");
            emailEditText.setText(emailRecibido);
            passwordEditText.setText(passwordRecibido);
        }

        auth = FirebaseAuth.getInstance();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Cuentas", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Cargar preferencias guardadas
        String savedEmail = sharedPreferences.getString("email", null);
        String savedPassword = sharedPreferences.getString("password", null);
        boolean remember = sharedPreferences.getBoolean("remember", false);

        if (remember && savedEmail != null && savedPassword != null) {
            emailEditText.setText(savedEmail);
            passwordEditText.setText(savedPassword);
            rememberMeCheckBox.setChecked(true);
        }

        // Mostrar/Ocultar contraseña
        togglePasswordView.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordView.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordView.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.length());
        });

        // Iniciar sesión
        view.findViewById(R.id.buttonLogin).setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(getContext(), "Introduce la contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Guardar datos si se marcó "recordar"
                    if (rememberMeCheckBox.isChecked()) {
                        editor.putString("email", email);
                        editor.putString("password", password);
                        editor.putBoolean("remember", true);
                    } else {
                        editor.clear();
                    }
                    editor.apply();

                    Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), AppActivity.class));
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(getContext(), "Usuario no encontrado, por favor regístrese", Toast.LENGTH_LONG).show();
                        // Cambiar a fragmento registro
                        Fragment registro = new PantallaRegistro();
                        String currentEmail = emailEditText.getText().toString().trim();
                        if (!currentEmail.isEmpty()) {
                            Bundle argumentos = new Bundle();
                            argumentos.putString("email", currentEmail);
                            registro.setArguments(argumentos);
                        }
                        // Llamamos al método de MainActivity para cambiar fragments
                        ((MainActivity) requireActivity()).cambiarFragmento(registro);
                    } else {
                        Toast.makeText(getContext(), "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        });
        // REGISTRO: cambia al fragmento de registro
        registrarse.setOnClickListener(v -> {
            Fragment registro = new PantallaRegistro();
            String currentEmail = emailEditText.getText().toString().trim();
            if (!currentEmail.isEmpty()) {
                Bundle argumentos = new Bundle();
                argumentos.putString("email", currentEmail);
                registro.setArguments(argumentos);
            }
            // Llamamos al método de MainActivity para cambiar fragments
            ((MainActivity) requireActivity()).cambiarFragmento(registro);
        });

        return view;
    }
}
