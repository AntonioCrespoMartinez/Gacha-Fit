package com.example.gacha_fit.adaptador;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gacha_fit.R;
import com.example.gacha_fit.modelo.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ClasificacionAdapter extends RecyclerView.Adapter<ClasificacionAdapter.ViewHolder> {

    private static final String TAG = "ClasificacionAdapter";
    private List<Usuario> listaUsuarios;
    private boolean isAdmin = false;
    private FirebaseFirestore db;
    private Context context;

    public ClasificacionAdapter(List<Usuario> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
        this.db = FirebaseFirestore.getInstance();
        checkIsAdmin();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_clasificacion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        
        holder.textViewPosicion.setText(String.valueOf(usuario.getPosicion()));
        holder.textViewUsuario.setText(usuario.getNombre());
        holder.textViewPuntuacion.setText(String.valueOf(usuario.getPuntuacion()));
        
        //Mostrar botón de administrador solo si el usuario actual es admin
        if (isAdmin && !"admin@gmail.com".equals(usuario.getCorreo())) {
            holder.btnAdminUsuario.setVisibility(View.VISIBLE);
            holder.btnAdminUsuario.setOnClickListener(v -> mostrarMenuOpciones(v, usuario));
        } else {
            holder.btnAdminUsuario.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void actualizarDatos(List<Usuario> nuevosUsuarios) {
        this.listaUsuarios = nuevosUsuarios;
        notifyDataSetChanged();
    }
    
    /**
     * Verifica si el usuario actual es administrador (admin@gmail.com)
     */
    private void checkIsAdmin() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null && "admin@gmail.com".equals(currentUser.getEmail())) {
            isAdmin = true;
        } else {
            isAdmin = false;
        }
    }
    
    /**
     * Muestra un menú emergente con opciones para administrar usuarios
     */
    private void mostrarMenuOpciones(View view, Usuario usuario) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenu().add("Cambiar nombre");
        popupMenu.getMenu().add("Eliminar usuario");
        popupMenu.getMenu().add("Modificar puntuación");
        
        popupMenu.setOnMenuItemClickListener(item -> {
            String titulo = item.getTitle().toString();
            
            if ("Cambiar nombre".equals(titulo)) {
                mostrarDialogoCambiarNombre(usuario);
                return true;
            } else if ("Eliminar usuario".equals(titulo)) {
                mostrarDialogoEliminarUsuario(usuario);
                return true;
            } else if ("Modificar puntuación".equals(titulo)) {
                mostrarDialogoModificarPuntuacion(usuario);
                return true;
            }
            
            return false;
        });
        
        popupMenu.show();
    }
    
    /**
     * Muestra un diálogo para cambiar el nombre del usuario
     */
    private void mostrarDialogoCambiarNombre(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Cambiar nombre de usuario");
        
        final EditText input = new EditText(context);
        input.setText(usuario.getNombre());
        builder.setView(input);
        
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString().trim();
            
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(context, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }
            
            //Verificar si el nombre ya existe
            db.collection("Usuarios")
                .whereEqualTo("Usuario", nuevoNombre)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot.isEmpty()) {
                            //El nombre no existe, proceder con el cambio
                            actualizarNombreUsuario(usuario, nuevoNombre);
                        } else {
                            //El nombre ya existe
                            Toast.makeText(context, "Este nombre de usuario ya está en uso", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error al verificar disponibilidad del nombre", Toast.LENGTH_SHORT).show();
                    }
                });
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    /**
     * Actualiza el nombre del usuario en Firestore
     */
    private void actualizarNombreUsuario(Usuario usuario, String nuevoNombre) {
        //Obtener referencia del documento por correo electrónico
        db.collection("Usuarios")
            .whereEqualTo("Correo", usuario.getCorreo())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    
                    //Actualizar el nombre de usuario
                    documentSnapshot.getReference()
                        .update("Usuario", nuevoNombre)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Nombre actualizado correctamente", Toast.LENGTH_SHORT).show();
                            
                            //Actualizar el objeto Usuario en la lista local
                            usuario.setNombre(nuevoNombre);
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error al actualizar nombre: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Error al buscar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }
    
    /**
     * Muestra un diálogo para confirmar la eliminación del usuario
     */
    private void mostrarDialogoEliminarUsuario(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Eliminar usuario");
        builder.setMessage("¿Estás seguro de que deseas eliminar a " + usuario.getNombre() + "? Esta acción no se puede deshacer.");
        
        builder.setPositiveButton("Eliminar", (dialog, which) -> {
            eliminarUsuario(usuario);
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }

    /**
     * Elimina al usuario de la base de datos, FirebaseAuth y todas las colecciones relacionadas
     */
    private void eliminarUsuario(Usuario usuario) {
        //Primero buscar todas las referencias al usuario en Firestore
        db.collection("Usuarios")
                .whereEqualTo("Correo", usuario.getCorreo())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String nombreUsuario = usuario.getNombre();

                        //1. Eliminar el documento del usuario de Usuarios
                        documentSnapshot.getReference().delete()
                                .addOnSuccessListener(aVoid -> {
                                    //2. Eliminar registros de actividad
                                    db.collection("RegistroActividad").document(nombreUsuario)
                                            .delete()
                                            .addOnCompleteListener(task -> {
                                                //3. Eliminar registros de FitDex
                                                db.collection("FitDex").document(nombreUsuario)
                                                        .delete()
                                                        .addOnCompleteListener(taskFitDex -> {
                                                            //Notificar al usuario que se ha eliminado correctamente
                                                            Toast.makeText(context, 
                                                                "Usuario eliminado correctamente", 
                                                                Toast.LENGTH_SHORT).show();
                                                            
                                                            //Notificar al adaptador que el usuario ha sido eliminado
                                                            listaUsuarios.remove(usuario);
                                                            notifyDataSetChanged();
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Toast.makeText(context, "Error al eliminar FitDex: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        });
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(context, "Error al eliminar registros: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Error al eliminar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al buscar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    
    /**
     * Muestra un diálogo para modificar la puntuación del usuario
     */
    private void mostrarDialogoModificarPuntuacion(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Modificar puntuación");
        
        final EditText input = new EditText(context);
        input.setText(String.valueOf(usuario.getPuntuacion()));
        input.setHint("Ingrese la nueva puntuación");
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);
        
        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevaPuntuacionStr = input.getText().toString().trim();
            
            if (nuevaPuntuacionStr.isEmpty()) {
                Toast.makeText(context, "Ingrese una puntuación válida", Toast.LENGTH_SHORT).show();
                return;
            }
            
            try {
                int nuevaPuntuacion = Integer.parseInt(nuevaPuntuacionStr);
                actualizarPuntuacionUsuario(usuario, nuevaPuntuacion);
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Ingrese un valor numérico válido", Toast.LENGTH_SHORT).show();
            }
        });
        
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    /**
     * Actualiza la puntuación del usuario en Firestore
     */
    private void actualizarPuntuacionUsuario(Usuario usuario, int nuevaPuntuacion) {
        //Obtener referencia del documento por correo electrónico
        db.collection("Usuarios")
            .whereEqualTo("Correo", usuario.getCorreo())
            .get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                if (!queryDocumentSnapshots.isEmpty()) {
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                    
                    //Actualizar la puntuación
                    documentSnapshot.getReference()
                        .update("Puntuacion", nuevaPuntuacion)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Puntuación actualizada correctamente", Toast.LENGTH_SHORT).show();
                            
                            //Actualizar el objeto Usuario en la lista local
                            usuario.setPuntuacion(nuevaPuntuacion);
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Error al actualizar puntuación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(context, "Error al buscar usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPosicion;
        TextView textViewUsuario;
        TextView textViewPuntuacion;
        ImageButton btnAdminUsuario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPosicion = itemView.findViewById(R.id.textViewPosicion);
            textViewUsuario = itemView.findViewById(R.id.textViewUsuario);
            textViewPuntuacion = itemView.findViewById(R.id.textViewPuntuacion);
            btnAdminUsuario = itemView.findViewById(R.id.btnAdminUsuario);
        }
    }
}
