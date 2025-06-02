package com.example.gacha_fit.adaptador;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gacha_fit.R;

import java.util.List;
import java.util.Map;

public class AdaptadorFitDex extends RecyclerView.Adapter<AdaptadorFitDex.FitDexViewHolder> {
    private final Context context;
    private final List<Map<String, Object>> ejercicios;
    private final Map<String, Long> progreso;
    private final OnEjercicioClickListener listener;

    /**
     * Interfaz para manejar clics sobre los ejercicios mostrados.
     */
    public interface OnEjercicioClickListener {
        void onEjercicioClick(Map<String, Object> ejercicio, int estrellas);
    }

    public AdaptadorFitDex(Context context, List<Map<String, Object>> ejercicios, Map<String, Long> progreso, OnEjercicioClickListener listener) {
        this.context = context;
        this.ejercicios = ejercicios;
        this.progreso = progreso;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FitDexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fitdex, parent, false);
        return new FitDexViewHolder(view);
    }

    /**
     * Vincula los datos del ejercicio a la vista.
     */
    @Override
    public void onBindViewHolder(@NonNull FitDexViewHolder holder, int position) {
        Map<String, Object> ejercicio = ejercicios.get(position);
        String nombre = (String) ejercicio.get("nombre");
        String imagen = (String) ejercicio.get("imagen_url");
        int resId = context.getResources().getIdentifier(imagen, "drawable", context.getPackageName());
        long valor = progreso.get(nombre) != null ? progreso.get(nombre) : 0;

        //Si el ejercicio no se ha desbloqueado, mostrar en escala de grises
        if (valor == 0) {
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            holder.imageButton.setColorFilter(new ColorMatrixColorFilter(matrix));
        } else {
            holder.imageButton.setColorFilter(null);//Colores normales
        }
        //Establece la imagen del ejercicio
        holder.imageButton.setImageResource(resId);

        //Limpia y agrega las estrellas correspondientes al progreso
        holder.estrellasLayout.removeAllViews();
        int estrellas = (int) valor;
        for (int i = 0; i < estrellas; i++) {
            ImageView star = new ImageView(context);
            star.setImageResource(android.R.drawable.btn_star_big_on);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);
            star.setLayoutParams(params);
            holder.estrellasLayout.addView(star);
        }

        //Listener para el botón de imagen
        holder.imageButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEjercicioClick(ejercicio, (int)valor);
            }
        });

        //Listener para el ítem completo (seguridad extra)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEjercicioClick(ejercicio, (int)valor);
            }
        });
    }

    /**
     * Devuelve el número total de ejercicios.
     */
    @Override
    public int getItemCount() {
        return ejercicios.size();
    }

    /**
     * ViewHolder que representa cada elemento del RecyclerView.
     * Contiene un botón con la imagen del ejercicio y una fila de estrellas.
     */
    static class FitDexViewHolder extends RecyclerView.ViewHolder {
        ImageButton imageButton;
        LinearLayout estrellasLayout;
        FitDexViewHolder(@NonNull View itemView) {
            super(itemView);
            imageButton = itemView.findViewById(R.id.imageButtonEjercicio);
            estrellasLayout = itemView.findViewById(R.id.estrellasLayout);
        }
    }
}