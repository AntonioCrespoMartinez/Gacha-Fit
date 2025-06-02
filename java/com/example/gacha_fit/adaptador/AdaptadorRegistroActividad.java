package com.example.gacha_fit.adaptador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorRegistroActividad extends RecyclerView.Adapter<AdaptadorRegistroActividad.ViewHolder> {
    //Lista de registros de actividad (cada registro es una cadena de texto).
    private List<String> registroItems;

    public AdaptadorRegistroActividad(List<String> registroItems) {
        this.registroItems = registroItems;
    }
    /**
     * Crea una nueva vista para un ítem de la lista.
     *
     * @param parent   Vista padre en la que se inflará el nuevo ítem.
     * @param viewType Tipo de la vista (no utilizado en este caso).
     * @return Un nuevo {@link ViewHolder} que contiene la vista del ítem.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view);
    }
    /**
     * Asocia un registro de actividad con una vista.
     *
     * @param holder   {@link ViewHolder} que debe actualizarse.
     * @param position Posición del ítem en la lista.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(registroItems.get(position));
    }
    /**
     * Devuelve el número total de elementos en la lista.
     *
     * @return Cantidad de registros de actividad.
     */
    @Override
    public int getItemCount() {
        return registroItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
