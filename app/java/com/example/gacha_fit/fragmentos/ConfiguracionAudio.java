package com.example.gacha_fit.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gacha_fit.utiles.Musica;
import com.example.gacha_fit.R;

public class ConfiguracionAudio extends Fragment {

    private SeekBar seekBarMusic;
    private SeekBar seekBarSound;
    private TextView tvMusicVolumeValue;
    private TextView tvSoundVolumeValue;
    private Button btnAplicar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracion_audio, container, false);
        
        //Inicializar vistas
        seekBarMusic = view.findViewById(R.id.seekBarMusic);
        seekBarSound = view.findViewById(R.id.seekBarSound);
        tvMusicVolumeValue = view.findViewById(R.id.tvMusicVolumeValue);
        tvSoundVolumeValue = view.findViewById(R.id.tvSoundVolumeValue);
        btnAplicar = view.findViewById(R.id.btnAplicar);
        
        //Cargar configuraciones guardadas
        Musica.loadVolumeSettings(requireContext());
        
        //Establecer valores iniciales en los SeekBar
        seekBarMusic.setProgress((int)(Musica.getMusicVolume() * 100));
        seekBarSound.setProgress((int)(Musica.getSoundVolume() * 100));
        
        //Actualizar textos de porcentaje
        actualizarTextoPorcentajeMusica(Musica.getMusicVolume());
        actualizarTextoPorcentajeSonido(Musica.getSoundVolume());
        
        //Configurar listeners de los SeekBar
        seekBarMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100.0f;
                actualizarTextoPorcentajeMusica(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        
        seekBarSound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volume = progress / 100.0f;
                actualizarTextoPorcentajeSonido(volume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        
        //Configurar listener del botón Aplicar
        btnAplicar.setOnClickListener(v -> {
            float musicVolume = seekBarMusic.getProgress() / 100.0f;
            float soundVolume = seekBarSound.getProgress() / 100.0f;
            
            //Aplicar y guardar configuraciones
            Musica.setMusicVolume(requireContext(), musicVolume);
            Musica.setSoundVolume(requireContext(), soundVolume);
            
            Toast.makeText(requireContext(), "Configuraciones de audio aplicadas", Toast.LENGTH_SHORT).show();
        });
        
        return view;
    }
    
    private void actualizarTextoPorcentajeMusica(float volume) {
        int porcentaje = (int)(volume * 100);
        tvMusicVolumeValue.setText(porcentaje + "%");
    }
    
    private void actualizarTextoPorcentajeSonido(float volume) {
        int porcentaje = (int)(volume * 100);
        tvSoundVolumeValue.setText(porcentaje + "%");
    }
}
