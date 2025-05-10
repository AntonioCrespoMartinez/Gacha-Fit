package com.example.gacha_fit;

import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FragmentoGacha extends Fragment {

    private ViewPager2 viewPagerImages;
    private Button buttonSelect;

    public FragmentoGacha() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_gacha, container, false);
        viewPagerImages = view.findViewById(R.id.viewPager_images);
        buttonSelect = view.findViewById(R.id.button_select);

        int[] images = {R.drawable.gacha_brazo, R.drawable.gacha_pierna, R.drawable.gacha_pecho};
        AdaptadorImagen adapter = new AdaptadorImagen(getContext(), images);
        viewPagerImages.setAdapter(adapter);

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentItem = viewPagerImages.getCurrentItem();
                String selectedTable;
//esto es un easter egg inofensivo, una perita?
                switch (currentItem) {
                    case 0:
                        selectedTable = "brazo";
                        break;
                    case 1:
                        selectedTable = "pierna";
                        break;
                    case 2:
                        selectedTable = "pecho";
                        break;
                    default:
                        selectedTable = "brazo";
                }

                // Aquí se llamaría a la base de datos para obtener un elemento aleatorio de la tabla seleccionada
                // Ejemplo: obtenerElementoAleatorio(selectedTable);
                Toast.makeText(getContext(), "Seleccionando de la tabla: " + selectedTable, Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}