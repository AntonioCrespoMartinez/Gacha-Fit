package com.example.gacha_fit.fragmentos;

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

import com.example.gacha_fit.R;
import com.example.gacha_fit.activity.AppActivity;
import com.example.gacha_fit.activity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class PantallaLogin extends Fragment {

    private EditText emailEditText;
    private EditText passwordEditText;
    private ImageView togglePasswordView;
    private boolean isPasswordVisible = false;
    private CheckBox rememberMe;
    private TextView registrarse;
    private FirebaseAuth auth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //Comprobación de "mantener sesión" al iniciar
        SharedPreferences prefs = requireActivity().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        boolean mantenerSesion = prefs.getBoolean("mantenerSesion", false);
        if (mantenerSesion) {
            Intent intent = new Intent(getActivity(), AppActivity.class);
            startActivity(intent);
            requireActivity().finish();
            return null; //No crear vista porque ya redirigimos
        }

        //Inflar la vista normalmente
        View view = inflater.inflate(R.layout.fragment_pantalla_login, container, false);

        emailEditText = view.findViewById(R.id.email);
        passwordEditText = view.findViewById(R.id.password);
        togglePasswordView = view.findViewById(R.id.togglePasswordView);
        registrarse = view.findViewById(R.id.registrarse);
        rememberMe = view.findViewById(R.id.recordar);

        //Inicializar el CheckBox según el valor guardado
        rememberMe.setChecked(mantenerSesion);
        //Recuperar email y password si vienen como argumentos (por ejemplo desde registro)
        Bundle args = getArguments();
        if (args != null) {
            String emailRecibido = args.getString("email", "");
            String passwordRecibido = args.getString("password", "");
            emailEditText.setText(emailRecibido);
            passwordEditText.setText(passwordRecibido);
        }

        auth = FirebaseAuth.getInstance();

        //Mostrar/Ocultar contraseña al pulsar icono
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

        //Botón para iniciar sesión con Firebase Authentication
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
                    //Guardar el estado de "mantener sesión"
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("mantenerSesion", rememberMe.isChecked());
                    editor.apply();

                    Toast.makeText(getContext(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getActivity(), AppActivity.class));
                    requireActivity().finish();
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toast.makeText(getContext(), "Usuario no encontrado, por favor regístrese", Toast.LENGTH_LONG).show();
                        //Cambiar a fragmento registro
                        Fragment registro = new PantallaRegistro();
                        String currentEmail = emailEditText.getText().toString().trim();
                        if (!currentEmail.isEmpty()) {
                            Bundle argumentos = new Bundle();
                            argumentos.putString("email", currentEmail);
                            registro.setArguments(argumentos);
                        }
                        //Llamar método de MainActivity para cambiar fragmentos
                        ((MainActivity) requireActivity()).cambiarFragmento(registro);
                    } else {
                        Toast.makeText(getContext(), "Error al iniciar sesión: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        });

        //Navegar al fragmento de registro al pulsar "Registrarse"
        registrarse.setOnClickListener(v -> {
            Fragment registro = new PantallaRegistro();
            String currentEmail = emailEditText.getText().toString().trim();
            if (!currentEmail.isEmpty()) {
                Bundle argumentos = new Bundle();
                argumentos.putString("email", currentEmail);
                registro.setArguments(argumentos);
            }
            ((MainActivity) requireActivity()).cambiarFragmento(registro);
        });

        return view;
    }
}
