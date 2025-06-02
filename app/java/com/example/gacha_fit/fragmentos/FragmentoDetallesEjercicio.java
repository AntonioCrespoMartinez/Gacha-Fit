package com.example.gacha_fit.fragmentos;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gacha_fit.R;

public class FragmentoDetallesEjercicio extends Fragment {

    private TextView tvNombre;
    private TextView tvDescripcion;
    private Button btnFacil;
    private Button btnNormal;
    private Button btnDificil;
    private ImageView imagenEjercicio;

    public FragmentoDetallesEjercicio() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflar el layout del fragmento
        View view = inflater.inflate(R.layout.fragment_ejercicio_detalle, container, false);

        //Referencias a elementos de la UI
        tvNombre = view.findViewById(R.id.nombreEjercicio);
        tvDescripcion = view.findViewById(R.id.descripcionEjercicio);
        btnFacil = view.findViewById(R.id.btnFacil);
        btnNormal = view.findViewById(R.id.btnNormal);
        btnDificil = view.findViewById(R.id.btnDificil);
        imagenEjercicio = view.findViewById(R.id.imagenEjercicio);

        //Cargar argumentos del bundle
        Bundle args = getArguments();
        if (args != null) {
            String nombre = args.getString("nombre", "");
            String descripcion = args.getString("descripcion", "");
            String dificultadFacil = args.getString("dificultad_facil", "");
            String dificultadNormal = args.getString("dificultad_normal", "");
            String dificultadDificil = args.getString("dificultad_dificil", "");
            int puntuacionFacil = args.getInt("puntuacion_facil", 0);
            int puntuacionNormal = args.getInt("puntuacion_normal", 0);
            int puntuacionDificil = args.getInt("puntuacion_dificil", 0);
            String medida = args.getString("medida", "");
            String imagen_url = args.getString("imagen_url", "");

            tvNombre.setText(nombre);
            tvDescripcion.setText(descripcion);
            btnFacil.setText("Fácil: " + dificultadFacil + " " + medida + " " + puntuacionFacil + " puntos");
            btnNormal.setText("Normal: " + dificultadNormal + " " + medida + " " + puntuacionNormal + " puntos");
            btnDificil.setText("Difícil: " + dificultadDificil + " " + medida + " " + puntuacionDificil + " puntos");
            //Cargar imagen desde recursos usando nombre
            if (imagen_url != null && !imagen_url.isEmpty()) {
                int resId = getResources().getIdentifier(imagen_url, "drawable", requireContext().getPackageName());
                if (resId != 0) {
                    imagenEjercicio.setImageResource(resId);
                }
            }
            //Configurar listeners de los botones
            btnFacil.setOnClickListener(v -> realizarEjercicio(nombre,"Facil",medida,Integer.parseInt(dificultadFacil),puntuacionFacil));
            btnNormal.setOnClickListener(v -> realizarEjercicio(nombre,"Normal",medida,Integer.parseInt(dificultadNormal),puntuacionNormal));
            btnDificil.setOnClickListener(v -> realizarEjercicio(nombre,"Dificil",medida,Integer.parseInt(dificultadDificil),puntuacionDificil));
        }

        return view;
    }
    /**
     * Inicia el fragmento Exito para mostrar el resultado del ejercicio realizado,
     * pasando todos los datos necesarios mediante un Bundle.
     *
     * @param ejercicio   Nombre del ejercicio.
     * @param dificultad  Nivel de dificultad.
     * @param medida      Unidad de medida (ej. repeticiones).
     * @param unidad      Cantidad realizada.
     * @param puntuacion  Puntos que se otorgan.
     */
    public void realizarEjercicio(String ejercicio,String dificultad,String medida,int unidad, int puntuacion) {
        Bundle bundle = new Bundle();
        bundle.putString("ejercicio",ejercicio);
        bundle.putString("dificultad",dificultad);
        bundle.putString("medida", medida);
        bundle.putInt("unidad", unidad);
        bundle.putInt("puntuacion", puntuacion);

        Activity activity = getActivity();
        if (activity != null && activity instanceof com.example.gacha_fit.activity.EjercicioActivity) {
            Exito exito = new Exito();
            exito.setArguments(bundle);
            ((com.example.gacha_fit.activity.EjercicioActivity) activity).cambiarFragmento(exito);
        }
    }
}