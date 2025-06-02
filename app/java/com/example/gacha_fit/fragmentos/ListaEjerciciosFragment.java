package com.example.gacha_fit.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gacha_fit.R;
import com.example.gacha_fit.activity.EjercicioActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListaEjerciciosFragment extends Fragment {

    private ListView listViewEjercicios;
    private List<String> nombresEjercicios;
    private Map<String, Map<String, Object>> ejerciciosMap;

    public ListaEjerciciosFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lista_ejercicios, container, false);
        listViewEjercicios = view.findViewById(R.id.listViewEjercicios);
        
        nombresEjercicios = new ArrayList<>();
        ejerciciosMap = new HashMap<>();
        
        //Configurar adaptador para la lista
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                nombresEjercicios
        );
        listViewEjercicios.setAdapter(adapter);
        
        //Obtener todos los ejercicios de Firestore
        obtenerTodosLosEjercicios();
        
        //Configurar listener para los items de la lista
        listViewEjercicios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String nombreEjercicio = nombresEjercicios.get(position);
                Map<String, Object> ejercicioSeleccionado = ejerciciosMap.get(nombreEjercicio);
                
                if (ejercicioSeleccionado != null) {
                    // Crear bundle con la información del ejercicio
                    Bundle bundle = new Bundle();
                    bundle.putString("nombre", nombreEjercicio);
                    bundle.putString("descripcion", ejercicioSeleccionado.get("descripcion") != null ? 
                            ejercicioSeleccionado.get("descripcion").toString() : "");
                    bundle.putString("dificultad_facil", ejercicioSeleccionado.get("dificultad_facil") != null ? 
                            ejercicioSeleccionado.get("dificultad_facil").toString() : "");
                    bundle.putString("dificultad_normal", ejercicioSeleccionado.get("dificultad_normal") != null ? 
                            ejercicioSeleccionado.get("dificultad_normal").toString() : "");
                    bundle.putString("dificultad_dificil", ejercicioSeleccionado.get("dificultad_dificil") != null ? 
                            ejercicioSeleccionado.get("dificultad_dificil").toString() : "");
                    
                    Object pf = ejercicioSeleccionado.get("puntuacion_facil");
                    Object pn = ejercicioSeleccionado.get("puntuacion_normal");
                    Object pd = ejercicioSeleccionado.get("puntuacion_dificil");

                    int puntuacionFacil = (pf instanceof Number) ? ((Number) pf).intValue() : 0;
                    int puntuacionNormal = (pn instanceof Number) ? ((Number) pn).intValue() : 0;
                    int puntuacionDificil = (pd instanceof Number) ? ((Number) pd).intValue() : 0;

                    bundle.putInt("puntuacion_facil", puntuacionFacil);
                    bundle.putInt("puntuacion_normal", puntuacionNormal);
                    bundle.putInt("puntuacion_dificil", puntuacionDificil);
                    
                    bundle.putString("medida", ejercicioSeleccionado.get("medida") != null ? 
                            ejercicioSeleccionado.get("medida").toString() : "");
                    bundle.putString("imagen_url", ejercicioSeleccionado.get("imagen_url") != null ? 
                            ejercicioSeleccionado.get("imagen_url").toString() : "");
                    
                    //Iniciar EjercicioActivity con los datos del ejercicio
                    Intent intent = new Intent(getActivity(), EjercicioActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });
        
        return view;
    }
    
    /**
     * Obtiene todos los ejercicios de la base de datos Firestore
     * y actualiza la lista para mostrarlos.
     */
    private void obtenerTodosLosEjercicios() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Ejercicios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    nombresEjercicios.clear();
                    ejerciciosMap.clear();
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> data = document.getData();
                        String nombre = data.get("nombre") != null ? data.get("nombre").toString() : "";
                        
                        if (!nombre.isEmpty()) {
                            nombresEjercicios.add(nombre);
                            ejerciciosMap.put(nombre, data);
                        }
                    }
                    
                    //Actualizar el adaptador de la lista
                    ((ArrayAdapter) listViewEjercicios.getAdapter()).notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al obtener ejercicios: " + e.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });
    }
}
