package com.example.gacha_fit.fragmentos;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.gacha_fit.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EjercicioDex extends Fragment {
    private static final String TAG = "EjercicioDex";

    private ImageView imagenEjercicio;
    private TextView tvNombre;
    private TextView tvTipo;
    private TextView tvDescripcion;
    private RecyclerView recyclerViewRegistro;
    private AdaptadorRegistroActividad adaptadorRegistro;
    private List<String> listaRegistros = new ArrayList<>();
    private Button btnModificarDificultad; // Botón para modificar dificultad
    private String nombreEjercicioActual; // Nombre del ejercicio actual

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public EjercicioDex() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ejercicio_dex, container, false);

        imagenEjercicio = view.findViewById(R.id.imagenEjercicioDex);
        tvNombre = view.findViewById(R.id.nombreEjercicioDex);
        tvTipo = view.findViewById(R.id.tipoEjercicioDex);
        tvDescripcion = view.findViewById(R.id.descripcionEjercicioDex);
        recyclerViewRegistro = view.findViewById(R.id.recyclerViewRegistro);
        btnModificarDificultad = view.findViewById(R.id.btnModificarDificultad);

        //Configurar RecyclerView
        recyclerViewRegistro.setLayoutManager(new LinearLayoutManager(getContext()));
        adaptadorRegistro = new AdaptadorRegistroActividad(listaRegistros);
        recyclerViewRegistro.setAdapter(adaptadorRegistro);

        //Verificar si el usuario es administrador
        checkIsAdmin();

        Bundle args = getArguments();
        if (args != null) {
            String nombreEjercicio = args.getString("nombre");
            nombreEjercicioActual = nombreEjercicio; //Guardar nombre para uso posterior

            //Cargar datos del ejercicio desde Firestore
            cargarDatosEjercicio(nombreEjercicio);

            //Cargar historial del ejercicio
            cargarHistorialEjercicio(nombreEjercicio);
        }
        
        return view;
    }
    
    /**
     * Verifica si el usuario actual es administrador (admin@gmail.com)
     * y muestra u oculta el botón de administrador según corresponda
     */
    private void checkIsAdmin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && "admin@gmail.com".equals(currentUser.getEmail())) {
            btnModificarDificultad.setVisibility(View.VISIBLE);
            btnModificarDificultad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDialogoDificultad();
                }
            });
        } else {
            btnModificarDificultad.setVisibility(View.GONE);
        }
    }

    /**
     * Muestra un diálogo para seleccionar el nivel de dificultad del ejercicio
     */
    private void mostrarDialogoDificultad() {
        if (nombreEjercicioActual == null || nombreEjercicioActual.isEmpty()) {
            Toast.makeText(getContext(), "No hay ejercicio seleccionado", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Modificar dificultad");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_modificar_dificultad, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupDificultad);

        //Obtener la dificultad actual del ejercicio
        db.collection("Ejercicios").document(nombreEjercicioActual)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long dificultad = documentSnapshot.getLong("dificultad");
                        int valorDificultad = dificultad != null ? dificultad.intValue() : 0;

                        //Seleccionar el RadioButton correspondiente
                        switch (valorDificultad) {
                            case 0:
                                ((RadioButton) radioGroup.findViewById(R.id.radioFacil)).setChecked(true);
                                break;
                            case 1:
                                ((RadioButton) radioGroup.findViewById(R.id.radioMedio)).setChecked(true);
                                break;
                            case 2:
                                ((RadioButton) radioGroup.findViewById(R.id.radioDificil)).setChecked(true);
                                break;
                            case 3:
                                ((RadioButton) radioGroup.findViewById(R.id.radioExtremo)).setChecked(true);
                                break;
                        }
                    }
                });

        builder.setView(dialogView);

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                int nuevaDificultad = 0;

                if (selectedId == R.id.radioMedio) {
                    nuevaDificultad = 1;
                } else if (selectedId == R.id.radioDificil) {
                    nuevaDificultad = 2;
                } else if (selectedId == R.id.radioExtremo) {
                    nuevaDificultad = 3;
                }

                final int dificultadFinal = nuevaDificultad;

                //Actualizar la dificultad en Firestore (Ejercicios)
                db.collection("Ejercicios").document(nombreEjercicioActual)
                        .update("dificultad", nuevaDificultad)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Dificultad actualizada correctamente", Toast.LENGTH_SHORT).show();

                            //Actualizar la dificultad en FitDex para todos los usuarios
                            //Primero obtenemos todos los documentos de FitDex
                            db.collection("FitDex")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        //Para cada documento (usuario) en FitDex
                                        for (DocumentSnapshot fitDexDoc : queryDocumentSnapshots.getDocuments()) {
                                            //Si el ejercicio existe en el documento
                                            if (fitDexDoc.contains(nombreEjercicioActual)) {
                                                //Actualizamos el valor con la nueva dificultad
                                                //Para cualquier valor, incluyendo los no realizados (valor 0)
                                                Map<String, Object> update = new HashMap<>();
                                                update.put(nombreEjercicioActual, dificultadFinal);
                                                
                                                // Actualizar el documento
                                                fitDexDoc.getReference().update(update)
                                                        .addOnFailureListener(e -> {
                                                            Log.e(TAG, "Error al actualizar FitDex para " + fitDexDoc.getId(), e);
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error al obtener documentos FitDex", e);
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Error al actualizar la dificultad", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Cancelar", null);

        builder.create().show();
    }
    
    /**
     * Carga los datos del ejercicio desde la colección "Ejercicios" en Firestore y los muestra en la UI.
     *
     * @param nombreEjercicio Nombre del ejercicio a buscar.
     */
    private void cargarDatosEjercicio(String nombreEjercicio) {
        if (nombreEjercicio == null || nombreEjercicio.isEmpty()) return;

        db.collection("Ejercicios").document(nombreEjercicio)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                //Mostrar datos del ejercicio
                                tvNombre.setText(document.getString("nombre"));
                                tvTipo.setText("Tipo: " + document.getString("tipo"));
                                tvDescripcion.setText(document.getString("descripcion"));

                                //Cargar imagen desde drawable
                                String nombreImagen = document.getString("imagen_url");
                                if (nombreImagen != null) {
                                    Context context = requireContext();
                                    int resId = context.getResources().getIdentifier(
                                            nombreImagen,
                                            "drawable",
                                            context.getPackageName()
                                    );
                                    if (resId != 0) {
                                        imagenEjercicio.setImageResource(resId);
                                    }
                                }
                            } else {
                                Log.d(TAG, "No se encontró el documento del ejercicio");
                            }
                        } else {
                            Log.d(TAG, "Error al obtener el documento: ", task.getException());
                        }
                    }
                });
    }
    /**
     * Carga el historial de registros del ejercicio específico realizado por el usuario autenticado.
     * Se consultan los registros desde la colección "RegistroActividad" filtrando por nombre de usuario.
     *
     * @param nombreEjercicio Nombre del ejercicio a filtrar.
     */
    private void cargarHistorialEjercicio(final String nombreEjercicio) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) return;
        
        String correo = currentUser.getEmail();
        if (correo == null) return;

        //Obtener nombre de usuario desde "Usuarios"
        db.collection("Usuarios")
                .whereEqualTo("Correo", correo)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot usuarioDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String usuario = usuarioDoc.getString("Usuario");
                        if (usuario == null) return;
                        
                        //Cargar registros desde "RegistroActividad"
                        db.collection("RegistroActividad").document(usuario)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    listaRegistros.clear();
                                    
                                    if (documentSnapshot.exists()) {
                                        Map<String, Object> data = documentSnapshot.getData();
                                        if (data != null) {
                                            //Filtrar claves que comiencen con "ejercicio-dificultad"
                                            for (Map.Entry<String, Object> entry : data.entrySet()) {
                                                String key = entry.getKey();
                                                if (key.startsWith(nombreEjercicio + "-")) {
                                                    String[] partes = key.split("-");
                                                    String dificultad = partes.length > 1 ? partes[1] : "";
                                                    String registro = entry.getValue().toString();
                                                    listaRegistros.add("Dificultad: " + dificultad + ": " + registro);
                                                }
                                            }
                                            
                                            adaptadorRegistro.notifyDataSetChanged();
                                        }
                                    }
                                    
                                    if (listaRegistros.isEmpty()) {
                                        listaRegistros.add("No hay registros para este ejercicio");
                                        adaptadorRegistro.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Log.e(TAG, "Error al buscar registros: ", e);
                                    listaRegistros.add("Error al cargar los registros");
                                    adaptadorRegistro.notifyDataSetChanged();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar usuario: ", e);
                });
    }

    /**
     * Adaptador para mostrar una lista de registros de actividad en un RecyclerView.
     * Utiliza el layout simple_list_item_1.
     */
    private class AdaptadorRegistroActividad extends RecyclerView.Adapter<AdaptadorRegistroActividad.ViewHolder> {
        private List<String> registroItems;

        public AdaptadorRegistroActividad(List<String> registroItems) {
            this.registroItems = registroItems;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.textView.setText(registroItems.get(position));
        }

        @Override
        public int getItemCount() {
            return registroItems.size();
        }
        /**
         * ViewHolder que contiene un solo TextView por cada item de registro.
         */
        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}