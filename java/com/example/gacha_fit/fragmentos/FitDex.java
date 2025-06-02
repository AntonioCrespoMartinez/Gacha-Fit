package com.example.gacha_fit.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gacha_fit.adaptador.AdaptadorFitDex;
import com.example.gacha_fit.base_datos.DatosEjercicios;
import com.example.gacha_fit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FitDex extends Fragment {
    private RecyclerView recyclerView;
    private AdaptadorFitDex adaptador;
    private List<Map<String, Object>> listaEjercicios = new ArrayList<>();
    private List<Map<String, Object>> listaEjerciciosFiltrada = new ArrayList<>();
    private Map<String, Long> progreso = new HashMap<>();
    private EditText editTextBuscar;
    private ImageButton buttonBuscar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fit_dex, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewFitDex);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        //Inicializar los componentes de búsqueda
        editTextBuscar = view.findViewById(R.id.editTextBuscar);
        buttonBuscar = view.findViewById(R.id.buttonBuscar);
        
        //Configurar el botón de búsqueda
        buttonBuscar.setOnClickListener(v -> buscarEjercicios());
        
        cargarEjerciciosYProgreso();
        return view;
    }
    
    /**
     * Filtra los ejercicios según el texto ingresado en el campo de búsqueda
     */
    private void buscarEjercicios() {
        String textoBusqueda = editTextBuscar.getText().toString().toLowerCase().trim();
        listaEjerciciosFiltrada.clear();
        
        if (textoBusqueda.isEmpty()) {
            //Si no hay texto de búsqueda, mostrar todos los ejercicios
            listaEjerciciosFiltrada.addAll(listaEjercicios);
        } else {
            //Filtrar ejercicios que coincidan con el nombre
            for (Map<String, Object> ejercicio : listaEjercicios) {
                String nombreEjercicio = ((String) ejercicio.get("nombre")).toLowerCase();
                if (nombreEjercicio.contains(textoBusqueda)) {
                    listaEjerciciosFiltrada.add(ejercicio);
                }
            }
        }
        
        //Actualizar el adaptador con la lista filtrada
        actualizarAdaptador();
    }
    
    /**
     * Actualiza el adaptador con la lista filtrada actual
     */
    private void actualizarAdaptador() {
        adaptador = new AdaptadorFitDex(getContext(), listaEjerciciosFiltrada, progreso, (ejercicio, estrellas) -> {
            //Navegación al detalle del ejercicio seleccionado
            EjercicioDex fragmentoDex = new EjercicioDex();
            Bundle args = new Bundle();
            args.putString("nombre", (String) ejercicio.get("nombre"));
            args.putString("descripcion", (String) ejercicio.get("descripcion"));
            args.putString("tipo", (String) ejercicio.get("tipo"));
            args.putString("nombreImagen", (String) ejercicio.get("imagen_url"));
            fragmentoDex.setArguments(args);

            //Navegar al fragmento usando el ID correcto del contenedor
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragmentoDex)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adaptador);
    }
    
    /**
     * Carga la lista de ejercicios desde la base de datos local y el progreso del usuario desde Firestore.
     * Al obtener el progreso, llama a mostrarFitDex para actualizar la interfaz.
     */
    private void cargarEjerciciosYProgreso() {
        listaEjercicios.clear();
        for (Map<String, Object> ejercicio : DatosEjercicios.obtenerEjercicios()) {
            if (ejercicio != null) listaEjercicios.add(ejercicio);
        }
        String correo = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (correo == null) return;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios")
                .whereEqualTo("Correo", correo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot usuarioDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String usuario = usuarioDoc.getString("Usuario");
                        if (usuario == null) return;
                        db.collection("FitDex").document(usuario).get().addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Map<String, Object> datos = documentSnapshot.getData();
                                if (datos != null) {
                                    for (String nombreEjercicio : datos.keySet()) {
                                        Object valor = datos.get(nombreEjercicio);
                                        if (valor instanceof Long) progreso.put(nombreEjercicio, (Long) valor);
                                        else if (valor instanceof Integer) progreso.put(nombreEjercicio, ((Integer) valor).longValue());
                                    }
                                }
                            }
                            mostrarFitDex();
                        });
                    }
                });
    }
    /**
     * Configura el adaptador del RecyclerView para mostrar la lista de ejercicios
     * junto con el progreso del usuario, y establece el listener para la selección
     * de un ejercicio, que navega al fragmento de detalle EjercicioDex.
     */
    private void mostrarFitDex() {
        //Inicializar la lista filtrada con todos los ejercicios
        listaEjerciciosFiltrada.clear();
        listaEjerciciosFiltrada.addAll(listaEjercicios);
        
        adaptador = new AdaptadorFitDex(getContext(), listaEjerciciosFiltrada, progreso, (ejercicio, estrellas) -> {
            //Navegación al detalle del ejercicio seleccionado
            EjercicioDex fragmentoDex = new EjercicioDex();
            Bundle args = new Bundle();
            args.putString("nombre", (String) ejercicio.get("nombre"));
            args.putString("descripcion", (String) ejercicio.get("descripcion"));
            args.putString("tipo", (String) ejercicio.get("tipo"));
            args.putString("nombreImagen", (String) ejercicio.get("imagen_url"));
            fragmentoDex.setArguments(args);

            //Navegar al fragmento usando el ID correcto del contenedor
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerView, fragmentoDex)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adaptador);
    }
}