package com.example.gacha_fit.modelo;

/**
 * Clase modelo para representar un usuario en la clasificación.
 */
public class Usuario {
    private String nombre;
    private int puntuacion;
    private int posicion;
    private String correo;
    /**
     * Constructor por defecto para Firestore
     */
    public Usuario() {
        //Constructor vacío necesario para Firestore
    }

    /**
     * Constructor con los datos básicos de usuario
     *
     * @param nombre     Nombre del usuario
     * @param puntuacion Puntuación del usuario
     * @param posicion   Posición en el ranking
     */
    public Usuario(String nombre, int puntuacion, int posicion) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.posicion = posicion;
    }

    /**
     * Constructor completo con todos los datos
     *
     * @param nombre     Nombre del usuario
     * @param puntuacion Puntuación del usuario
     * @param posicion   Posición en el ranking
     * @param correo     Correo electrónico del usuario
     */
    public Usuario(String nombre, int puntuacion, int posicion, String correo) {
        this.nombre = nombre;
        this.puntuacion = puntuacion;
        this.posicion = posicion;
        this.correo = correo;}
    //Getters y setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
