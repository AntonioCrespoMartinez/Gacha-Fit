package com.example.gacha_fit.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gacha_fit.R;
import com.example.gacha_fit.adaptador.ClasificacionAdapter;
import com.example.gacha_fit.modelo.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Clasificacion extends Fragment {
    private static final String TAG = "Clasificacion";
    
    private RecyclerView recyclerView;
    private ClasificacionAdapter adaptador;
    private List<Usuario> listaUsuarios;
    private ProgressBar progressBar;
    private TextView tvMensajeVacio;
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public Clasificacion() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        listaUsuarios = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clasificacion, container, false);
        
        recyclerView = view.findViewById(R.id.recyclerViewClasificacion);
        progressBar = view.findViewById(R.id.progressBarClasificacion);
        tvMensajeVacio = view.findViewById(R.id.tvMensajeVacio);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptador = new ClasificacionAdapter(listaUsuarios);
        recyclerView.setAdapter(adaptador);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cargarClasificacion();
    }
    
    /**
     * Carga la clasificación de usuarios desde Firestore
     */
    private void cargarClasificacion() {
        mostrarCargando(true);
        
        //Consultar todos los usuarios con puntuación > 0, ordenados de mayor a menor
        db.collection("Usuarios")
                .whereGreaterThan("Puntuacion", 0)
                .orderBy("Puntuacion", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaUsuarios.clear();
                        int posicion = 1;
                        
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nombre = document.getString("Usuario");
                            int puntuacion = 0;
                            
                            // Intentar obtener la puntuación como número
                            if (document.get("Puntuacion") != null) {
                                if (document.get("Puntuacion") instanceof Long) {
                                    puntuacion = ((Long) document.get("Puntuacion")).intValue();
                                } else if (document.get("Puntuacion") instanceof Integer) {
                                    puntuacion = (Integer) document.get("Puntuacion");
                                } else if (document.get("Puntuacion") instanceof Double) {
                                    puntuacion = ((Double) document.get("Puntuacion")).intValue();
                                }
                            }
                            
                            //Obtener correo del usuario
                            String correo = document.getString("Correo");
                            
                            //Crear usuario con todos los datos
                            Usuario usuario = new Usuario(nombre, puntuacion, posicion, correo);
                            listaUsuarios.add(usuario);
                            
                            posicion++;
                        }
                        
                        adaptador.actualizarDatos(listaUsuarios);
                        mostrarResultados();
                    } else {
                        Log.d(TAG, "Error obteniendo usuarios: ", task.getException());
                        mostrarError("Error al cargar la clasificación");
                    }
                    
                    mostrarCargando(false);
                });
    }
    
    /**
     * Muestra u oculta elementos según si hay resultados
     */
    private void mostrarResultados() {
        if (listaUsuarios.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            tvMensajeVacio.setVisibility(View.VISIBLE);
            tvMensajeVacio.setText(R.string.no_hay_usuarios_en_la_clasificacion);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            tvMensajeVacio.setVisibility(View.GONE);
        }
    }
    
    /**
     * Muestra un mensaje de error
     */
    private void mostrarError(String mensaje) {
        recyclerView.setVisibility(View.GONE);
        tvMensajeVacio.setVisibility(View.VISIBLE);
        tvMensajeVacio.setText(mensaje);
    }
    
    /**
     * Muestra u oculta el indicador de carga
     */
    private void mostrarCargando(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            recyclerView.setVisibility(View.GONE);
            tvMensajeVacio.setVisibility(View.GONE);
        }
    }
}