package com.example.gacha_fit.adaptador;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gacha_fit.R;

public class AdaptadorImagen extends RecyclerView.Adapter<AdaptadorImagen.ImageViewHolder>{
    private int[] images;
    private Context context;

    public AdaptadorImagen(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }
    /**
     * Crea una nueva vista (invocado por el LayoutManager).
     *
     * @param parent   Grupo de vistas al que se añadirá la nueva vista.
     * @param viewType Tipo de la nueva vista.
     * @return Un nuevo ViewHolder que contiene la vista del ítem.
     */
    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.adaptador_imagen, parent, false);
        return new ImageViewHolder(view);
    }
    /**
     * Asocia los datos de una posición específica con el ViewHolder.
     *
     * @param holder   ViewHolder que debe ser actualizado.
     * @param position Posición del ítem en el conjunto de datos.
     */
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.imageView.setImageResource(images[position]);
    }
    /**
     * Devuelve el número total de ítems en el conjunto de datos.
     *
     * @return Cantidad de imágenes.
     */
    @Override
    public int getItemCount() {
        return images.length;
    }
    /**
     * ViewHolder que contiene una sola ImageView.
     */
    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
        }
    }
}
