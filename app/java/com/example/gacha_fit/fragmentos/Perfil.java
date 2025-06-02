package com.example.gacha_fit.fragmentos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.gacha_fit.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class Perfil extends Fragment {
    private static final String TAG = "Perfil";
    private static final int PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int PERMISSIONS_REQUEST_GALLERY = 101;

    private CircleImageView imagenPerfil;
    private ImageButton btnCambiarImagen;
    private TextView tvCorreo;
    private EditText etNombreUsuario;
    private Button btnGuardarCambios;
    private ProgressBar progressBar;
    private TextView tvMensaje;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String nombreUsuarioActual;
    private File photoFile;
    private String fotoPath;

    private ActivityResultLauncher<Intent> takePictureLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    public Perfil() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //Configurar launcher para hacer foto
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        procesarImagenCapturada();
                    }
                });

        //Configurar launcher para seleccionar imagen de galería
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        requireActivity().getContentResolver(), selectedImageUri);
                                imagenPerfil.setImageBitmap(bitmap);
                                guardarImagenEnArchivo(bitmap);
                            } catch (IOException e) {
                                Log.e(TAG, "Error al procesar imagen de galería: " + e.getMessage());
                                mostrarMensaje("Error al procesar la imagen seleccionada", true);
                            }
                        }
                    }
                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        //Inicializar vistas
        imagenPerfil = view.findViewById(R.id.imagenPerfil);
        btnCambiarImagen = view.findViewById(R.id.btnCambiarImagen);
        tvCorreo = view.findViewById(R.id.tvCorreo);
        etNombreUsuario = view.findViewById(R.id.etNombreUsuario);
        btnGuardarCambios = view.findViewById(R.id.btnGuardarCambios);
        progressBar = view.findViewById(R.id.progressBar);
        tvMensaje = view.findViewById(R.id.tvMensaje);

        //Configurar listeners
        btnCambiarImagen.setOnClickListener(v -> mostrarOpcionesImagen());
        btnGuardarCambios.setOnClickListener(v -> guardarCambios());

        //Cargar datos del usuario
        cargarDatosUsuario();

        //Cargar imagen guardada si existe
        cargarImagenGuardada();

        return view;
    }
    /**
     * Carga los datos del usuario desde Firestore y actualiza la interfaz.
     */
    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String correo = user.getEmail();
            tvCorreo.setText(correo);

            //Buscar el usuario por correo en Firestore
            db.collection("Usuarios")
                    .whereEqualTo("Correo", correo)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            nombreUsuarioActual = document.getString("Usuario");
                            if (nombreUsuarioActual != null) {
                                etNombreUsuario.setText(nombreUsuarioActual);
                                //Cargar la imagen una vez que tengamos el nombre de usuario
                                cargarImagenGuardada();
                            }
                        } else {
                            Log.d(TAG, "Error al obtener datos del usuario: ", task.getException());
                            mostrarMensaje("Error al cargar datos del usuario");
                        }
                    });
        }
    }

    /**
     * Muestra un diálogo con opciones para cambiar la imagen de perfil.
     */
    private void mostrarOpcionesImagen() {
        String[] opciones = {"Hacer foto", "Seleccionar de la galería"};
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cambiar imagen de perfil");
        builder.setItems(opciones, (dialog, which) -> {
            if (which == 0) {
                verificarPermisoCamara();
            } else {
                verificarPermisoGaleria();
            }
        });
        builder.show();
    }

    /**
     * Verifica y solicita permiso para usar la cámara si es necesario.
     */
    private void verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            hacerFoto();
        }
    }
    /**
     * Verifica y solicita permiso para acceder a la galería, según la versión de Android.
     */
    private void verificarPermisoGaleria() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            //Para Android 13+ necesitamos READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PERMISSIONS_REQUEST_GALLERY);
            } else {
                seleccionarImagenGaleria();
            }
        } else {
            //Para versiones anteriores usamos READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_GALLERY);
            } else {
                seleccionarImagenGaleria();
            }
        }
    }
    /**
     * Maneja el resultado de la solicitud de permisos.
     *
     * @param requestCode Código de la solicitud.
     * @param permissions Permisos solicitados.
     * @param grantResults Resultados de los permisos.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                hacerFoto();
            } else {
                Toast.makeText(requireContext(), "Se requiere permiso de cámara", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PERMISSIONS_REQUEST_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                seleccionarImagenGaleria();
            } else {
                Toast.makeText(requireContext(), "Se requiere permiso de almacenamiento", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /**
     * Inicia la cámara para tomar una foto.
     */
    private void hacerFoto () {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
            try {
                photoFile = crearArchivoImagen();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(requireContext(),
                            "com.example.gacha_fit.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureLauncher.launch(takePictureIntent);
                }
            } catch (IOException ex) {
                Log.e(TAG, "Error al crear archivo para foto", ex);
                mostrarMensaje("Error al crear archivo para foto");
            }
        }
    }
    /**
     * Abre la galería para seleccionar una imagen.
     */
    private void seleccionarImagenGaleria() {
        try {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
        } catch (Exception e) {
            Log.e(TAG, "Error al abrir selector de imágenes: " + e.getMessage());
            //Intentar con el método alternativo si el primero falla
            try {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickImageLauncher.launch(intent);
            } catch (Exception ex) {
                Log.e(TAG, "Error en método alternativo: " + ex.getMessage());
                mostrarMensaje("Error al abrir selector de imágenes", true);
            }
        }
    }
    /**
     * Crea un archivo temporal para guardar la foto capturada.
     *
     * @return Archivo creado.
     * @throws IOException Si hay error al crear el archivo.
     */
    private File crearArchivoImagen() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        fotoPath = image.getAbsolutePath();
        return image;
    }
    /**
     * Procesa la imagen capturada desde la cámara y la muestra en el perfil.
     */
    private void procesarImagenCapturada() {
        if (photoFile != null && photoFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            if (bitmap != null) {
                imagenPerfil.setImageBitmap(bitmap);
                guardarImagenEnArchivo(bitmap);
            }
        }
    }
    /**
     * Guarda la imagen de perfil en almacenamiento interno.
     *
     * @param bitmap Imagen del perfil.
     */
    private void guardarImagenEnArchivo(Bitmap bitmap) {
        try {
            if (nombreUsuarioActual == null) {
                Log.e(TAG, "Nombre de usuario no disponible para guardar imagen");
                return;
            }

            File imageDir = new File(requireActivity().getFilesDir(), "profile_images");
            if (!imageDir.exists()) {
                boolean dirCreated = imageDir.mkdirs();
                if (!dirCreated) {
                    Log.e(TAG, "No se pudo crear el directorio para imágenes");
                    return;
                }
            }

            File imageFile = new File(imageDir, nombreUsuarioActual + "_profile.jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
            Log.d(TAG, "Imagen guardada correctamente en: " + imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al guardar imagen: " + e.getMessage());
            mostrarMensaje("Error al guardar la imagen de perfil");
        }
    }
    /**
     * Carga una imagen guardada localmente del usuario y la muestra.
     */
    private void cargarImagenGuardada() {
        try {
            if (nombreUsuarioActual == null) {
                Log.e(TAG, "Nombre de usuario no disponible para cargar imagen");
                return;
            }

            File imageDir = new File(requireActivity().getFilesDir(), "profile_images");
            File imageFile = new File(imageDir, nombreUsuarioActual + "_profile.jpg");

            Log.d(TAG, "Intentando cargar imagen desde: " + imageFile.getAbsolutePath());

            if (imageFile.exists()) {
                FileInputStream fis = new FileInputStream(imageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                if (bitmap != null) {
                    Log.d(TAG, "Imagen cargada correctamente");
                    imagenPerfil.setImageBitmap(bitmap);
                } else {
                    Log.e(TAG, "No se pudo decodificar la imagen");
                }
                fis.close();
            } else {
                Log.d(TAG, "No existe archivo de imagen guardada");
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al cargar imagen guardada: " + e.getMessage());
        }
    }
    /**
     * Inicia el proceso para guardar cambios en el nombre de usuario.
     */
    private void guardarCambios() {
        String nuevoNombre = etNombreUsuario.getText().toString().trim();

        if (nuevoNombre.isEmpty()) {
            mostrarMensaje("El nombre de usuario no puede estar vacío");
            return;
        }

        if (nuevoNombre.equals(nombreUsuarioActual)) {
            mostrarMensaje("No hay cambios que guardar");
            return;
        }

        mostrarCargando(true);

        //Verificar si el nuevo nombre ya está en uso
        verificarNombreUsuario(nuevoNombre, disponible -> {
            if (disponible) {
                actualizarNombreUsuario(nuevoNombre);
            } else {
                mostrarCargando(false);
                mostrarMensaje("El nombre de usuario ya está en uso");
            }
        });
    }
    /**
     * Verifica si un nombre de usuario está disponible en la base de datos.
     *
     * @param nuevoNombre Nombre a verificar.
     * @param listener Callback que devuelve si está disponible.
     */
    private void verificarNombreUsuario(String nuevoNombre, OnNombreVerificadoListener listener) {
        db.collection("Usuarios")
                .whereEqualTo("Usuario", nuevoNombre)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean disponible = task.getResult().isEmpty();
                        listener.onVerificado(disponible);
                    } else {
                        mostrarCargando(false);
                        mostrarMensaje("Error al verificar disponibilidad del nombre de usuario");
                        listener.onVerificado(false);
                    }
                });
    }
    /**
     * Actualiza el nombre de usuario en Firestore y en colecciones relacionadas.
     *
     * @param nuevoNombre Nuevo nombre a establecer.
     */
    private void actualizarNombreUsuario(String nuevoNombre) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String correo = user.getEmail();

            //Primero, obtener el documento del usuario
            db.collection("Usuarios")
                    .whereEqualTo("Correo", correo)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            String documentId = document.getId();

                            //Actualizar el nombre de usuario en la colección Usuarios
                            db.collection("Usuarios").document(documentId)
                                    .update("Usuario", nuevoNombre)
                                    .addOnSuccessListener(aVoid -> {
                                        //Ahora actualizar en otras colecciones
                                        actualizarNombreEnColeccion("FitDex", nombreUsuarioActual, nuevoNombre);
                                        actualizarNombreEnColeccion("RegistroActividad", nombreUsuarioActual, nuevoNombre);

                                        //Actualizar variable local y UI
                                        String viejoNombre = nombreUsuarioActual;
                                        nombreUsuarioActual = nuevoNombre;

                                        //Renombrar archivo de imagen si existe
                                        renombrarArchivoImagen(viejoNombre, nuevoNombre);

                                        mostrarCargando(false);
                                        mostrarMensaje("Nombre de usuario actualizado correctamente", false);
                                        Toast.makeText(requireContext(), "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        mostrarCargando(false);
                                        mostrarMensaje("Error al actualizar el nombre de usuario");
                                        Log.e(TAG, "Error al actualizar en Usuarios: ", e);
                                    });
                        } else {
                            mostrarCargando(false);
                            mostrarMensaje("Error al encontrar el documento del usuario");
                        }
                    });
        }
    }
    /**
     * Actualiza el nombre de usuario en una colección específica.
     *
     * @param coleccion Nombre de la colección.
     * @param nombreViejo Nombre actual del usuario.
     * @param nombreNuevo Nuevo nombre del usuario.
     */
    private void actualizarNombreEnColeccion(String coleccion, String nombreViejo, String nombreNuevo) {
        //Verificar si el documento existe
        db.collection(coleccion).document(nombreViejo)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        //Copiar datos al nuevo documento
                        db.collection(coleccion).document(nombreViejo)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (documentSnapshot.exists()) {
                                        db.collection(coleccion).document(nombreNuevo)
                                                .set(documentSnapshot.getData())
                                                .addOnSuccessListener(aVoid -> {
                                                    //Eliminar documento antiguo después de copiar
                                                    db.collection(coleccion).document(nombreViejo)
                                                            .delete()
                                                            .addOnFailureListener(e ->
                                                                    Log.e(TAG, "Error al eliminar documento antiguo en " + coleccion, e));
                                                })
                                                .addOnFailureListener(e ->
                                                        Log.e(TAG, "Error al crear nuevo documento en " + coleccion, e));
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Log.e(TAG, "Error al obtener documento para copiar en " + coleccion, e));
                    }
                });
    }
    /**
     * Cambia el nombre del archivo de imagen del perfil cuando el usuario cambia su nombre.
     *
     * @param nombreViejo Nombre anterior del usuario.
     * @param nombreNuevo Nuevo nombre del usuario.
     */
    private void renombrarArchivoImagen(String nombreViejo, String nombreNuevo) {
        try {
            File imageDir = new File(requireActivity().getFilesDir(), "profile_images");
            File oldFile = new File(imageDir, nombreViejo + "_profile.jpg");
            File newFile = new File(imageDir, nombreNuevo + "_profile.jpg");

            if (oldFile.exists()) {
                boolean success = oldFile.renameTo(newFile);
                if (!success) {
                    Log.e(TAG, "No se pudo renombrar el archivo de imagen");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error al renombrar archivo de imagen: " + e.getMessage());
        }
    }
    /**
     * Muestra u oculta el ProgressBar y habilita/deshabilita el botón de guardar.
     *
     * @param mostrar true para mostrar la barra de progreso.
     */
    private void mostrarCargando(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        btnGuardarCambios.setEnabled(!mostrar);
    }
    /**
     * Muestra un mensaje de error o éxito en pantalla.
     *
     * @param mensaje Texto del mensaje.
     */
    private void mostrarMensaje(String mensaje) {
        mostrarMensaje(mensaje, true);
    }
    /**
     * Muestra un mensaje personalizado indicando si es error o éxito.
     *
     * @param mensaje Texto del mensaje.
     * @param error true si es mensaje de error.
     */
    private void mostrarMensaje(String mensaje, boolean error) {
        tvMensaje.setText(mensaje);
        tvMensaje.setTextColor(error ?
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark) :
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
        tvMensaje.setVisibility(View.VISIBLE);
    }
    /**
     * Interfaz de devolución para verificar la disponibilidad del nombre de usuario.
     */
    interface OnNombreVerificadoListener {
        void onVerificado(boolean disponible);
    }
}