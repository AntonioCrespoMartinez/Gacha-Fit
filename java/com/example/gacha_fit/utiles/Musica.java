// MusicManager.java
package com.example.gacha_fit.utiles;

import android.content.Context;
import android.media.MediaPlayer;
import android.content.SharedPreferences;

import com.example.gacha_fit.R;

public class Musica {
    private static MediaPlayer mediaPlayer;
    private static float musicVolume = 1.0f; //Volumen de música (0.0f a 1.0f)
    private static float soundVolume = 1.0f; //Volumen de efectos de sonido (0.0f a 1.0f)
    private static final String PREFS_NAME = "GachaFitPrefs";
    private static final String KEY_MUSIC_VOLUME = "music_volume";
    private static final String KEY_SOUND_VOLUME = "sound_volume";

    public static void startMusic(Context context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.lobby_music);
            mediaPlayer.setLooping(true); //Música en bucle
            mediaPlayer.setVolume(musicVolume, musicVolume); //Aplicar volumen actual
            mediaPlayer.start();
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public static void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    //Métodos para control de volumen de música
    public static void setMusicVolume(Context context, float volume) {
        //Asegurar que el volumen esté entre 0.0 y 1.0
        musicVolume = Math.max(0.0f, Math.min(volume, 1.0f));
        
        //Aplicar el volumen al reproductor si existe
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(musicVolume, musicVolume);
        }
        
        //Guardar la preferencia
        saveVolumeSettings(context);
    }
    
    public static float getMusicVolume() {
        return musicVolume;
    }
    
    //Métodos para control de volumen de efectos de sonido
    public static void setSoundVolume(Context context, float volume) {
        //Asegurar que el volumen esté entre 0.0 y 1.0
        soundVolume = Math.max(0.0f, Math.min(volume, 1.0f));
        
        //Guardar la preferencia
        saveVolumeSettings(context);
    }
    
    public static float getSoundVolume() {
        return soundVolume;
    }
    
    //Guardar configuraciones de volumen
    private static void saveVolumeSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putFloat(KEY_MUSIC_VOLUME, musicVolume);
        editor.putFloat(KEY_SOUND_VOLUME, soundVolume);
        editor.apply();
    }
    
    //Cargar configuraciones de volumen
    public static void loadVolumeSettings(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        musicVolume = prefs.getFloat(KEY_MUSIC_VOLUME, 1.0f);
        soundVolume = prefs.getFloat(KEY_SOUND_VOLUME, 1.0f);
        
        //Aplicar volumen al reproductor si existe
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(musicVolume, musicVolume);
        }
    }
    
    //Aplicar volumen de efectos de sonido a cualquier MediaPlayer
    public static void applySoundVolumeToMediaPlayer(MediaPlayer player) {
        if (player != null) {
            player.setVolume(soundVolume, soundVolume);
        }
    }
}
