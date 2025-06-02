package com.example.gacha_fit.fragmentos;

import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.gacha_fit.base_datos.FireBase;
import com.example.gacha_fit.R;

public class PantallaRegistro extends Fragment {

    private EditText emailRegistro;
    private EditText nombreUsuario;
    private EditText passwordRegistro;
    private EditText passwordRepetir;
    private ImageView togglePasswordView;
    private ImageView togglePasswordRepetir;
    private boolean isPasswordVisible = false;
    private boolean isPasswordRepetirVisible = false;
    private Button registrarse;
    private String restriccionVacia = "Campo obligatorio";
    private final String restriccionEmail = "Introduzca un email válido";
    private final String regex = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantalla_registro, container, false);

        emailRegistro = view.findViewById(R.id.emailRegistro);
        nombreUsuario = view.findViewById(R.id.nombreUsuario);
        passwordRegistro = view.findViewById(R.id.passwordRegistro);
        passwordRepetir = view.findViewById(R.id.passwordRepetir);
        togglePasswordView = view.findViewById(R.id.togglePasswordView);
        togglePasswordRepetir = view.findViewById(R.id.togglePasswordRepetir);
        registrarse = view.findViewById(R.id.botonRegistro);
        //Si se recibe un email desde otro fragmento, se rellena el campo
        Bundle args = getArguments();
        if (args != null) {
            String emailRecibido = args.getString("email", "");
            emailRegistro.setText(emailRecibido);
        }


        //Mostrar/Ocultar contraseña
        togglePasswordView.setOnClickListener(v -> {
            if (isPasswordVisible) {
                passwordRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordView.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordRegistro.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordView.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordVisible = !isPasswordVisible;
            passwordRegistro.setSelection(passwordRegistro.length());
        });
        //Mostrar/Ocultar contraseña repetida
        togglePasswordRepetir.setOnClickListener(v -> {
            if (isPasswordRepetirVisible) {
                passwordRepetir.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePasswordRepetir.setImageResource(R.drawable.ic_visibility_off);
            } else {
                passwordRepetir.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePasswordRepetir.setImageResource(R.drawable.ic_visibility);
            }
            isPasswordRepetirVisible = !isPasswordRepetirVisible;
            passwordRepetir.setSelection(passwordRepetir.length());
        });
        //Botón para registrar usuario con validaciones
        registrarse.setOnClickListener(v -> {
            //Primero validar email
            if (!emailRegistro.getText().toString().isEmpty() && !nombreUsuario.getText().toString().isEmpty() && !passwordRegistro.getText().toString().isEmpty() && !passwordRepetir.getText().toString().isEmpty()) {
                registrarUsuario();
            } else if (emailRegistro.getText().toString().isEmpty()) {
                emailRegistro.setError(restriccionVacia);
            }else if (!emailRegistro.getText().toString().matches(regex)) {
                emailRegistro.setError(restriccionEmail);
            //Validar demás campos obligatorios
            } else if (passwordRegistro.getText().toString().isEmpty()) {
                passwordRegistro.setError(restriccionVacia);
            } else if (passwordRepetir.getText().toString().isEmpty()) {
                passwordRepetir.setError(restriccionVacia);
            } else if (nombreUsuario.getText().toString().isEmpty()) {
                nombreUsuario.setError(restriccionVacia);
            }
        });


        return view;
    }
    /**
     * Método que valida las contraseñas y llama al método de FireBase para registrar el usuario.
     */
    private void registrarUsuario() {
        String email = emailRegistro.getText().toString().trim();
        String usuario = nombreUsuario.getText().toString().trim();
        String password = passwordRegistro.getText().toString().trim();
        String passwordRepetir = this.passwordRepetir.getText().toString().trim();

        if (!password.equals(passwordRepetir)) {
            Toast.makeText(getActivity(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }
        //Llamada a método externo que gestiona el registro en Firebase
        FireBase fireBase = new FireBase();
        fireBase.comprobarNombreUsuarioYRegistrar(email, password, usuario, this);
    }
}