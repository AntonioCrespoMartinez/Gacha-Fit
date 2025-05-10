package com.example.gacha_fit;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import com.google.firebase.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBase {

    private FirebaseFirestore db;

    public FireBase() {
        // Inicializa la referencia a la base de datos
        db = FirebaseFirestore.getInstance();
    }

    public void agregarUsuario(String correo, String password) {
        Map<String, Object> usuarioData = new HashMap<>();
        usuarioData.put("Correo", correo);
        usuarioData.put("Contraseña", password);

        db.collection("Usuarios")
                .document(correo)
                .set(usuarioData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firebase","Usuario " + correo + " registrado correctamente");
                        })
                .addOnFailureListener(e ->{
                    Log.w("Firebase","Error al registrar usuario",e);
                });
    }
}
