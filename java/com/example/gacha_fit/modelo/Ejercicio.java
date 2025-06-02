package com.example.gacha_fit.modelo;

public class Ejercicio {
    private String nombre;
    private String descripcion;
    private String tipo;
    private String dificultadFacil;
    private String dificultadNormal;
    private String dificultadDificil;
    private int puntuacionFacil;
    private int puntuacionNormal;
    private int puntuacionDificil;
    private String medida;
    private String imagenUrl;
    private int dificultad;

    public Ejercicio() {
        //Constructor vacío requerido para Firestore
    }

    public Ejercicio(String nombre, String descripcion, String tipo, String dificultadFacil,
                    String dificultadNormal, String dificultadDificil, int puntuacionFacil,
                    int puntuacionNormal, int puntuacionDificil, String medida, String imagenUrl, int dificultad) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.dificultadFacil = dificultadFacil;
        this.dificultadNormal = dificultadNormal;
        this.dificultadDificil = dificultadDificil;
        this.puntuacionFacil = puntuacionFacil;
        this.puntuacionNormal = puntuacionNormal;
        this.puntuacionDificil = puntuacionDificil;
        this.medida = medida;
        this.imagenUrl = imagenUrl;
        this.dificultad = dificultad;
    }

    //Getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDificultadFacil() {
        return dificultadFacil;
    }

    public void setDificultadFacil(String dificultadFacil) {
        this.dificultadFacil = dificultadFacil;
    }

    public String getDificultadNormal() {
        return dificultadNormal;
    }

    public void setDificultadNormal(String dificultadNormal) {
        this.dificultadNormal = dificultadNormal;
    }

    public String getDificultadDificil() {
        return dificultadDificil;
    }

    public void setDificultadDificil(String dificultadDificil) {
        this.dificultadDificil = dificultadDificil;
    }

    public int getPuntuacionFacil() {
        return puntuacionFacil;
    }

    public void setPuntuacionFacil(int puntuacionFacil) {
        this.puntuacionFacil = puntuacionFacil;
    }

    public int getPuntuacionNormal() {
        return puntuacionNormal;
    }

    public void setPuntuacionNormal(int puntuacionNormal) {
        this.puntuacionNormal = puntuacionNormal;
    }

    public int getPuntuacionDificil() {
        return puntuacionDificil;
    }

    public void setPuntuacionDificil(int puntuacionDificil) {
        this.puntuacionDificil = puntuacionDificil;
    }

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public int getDificultad() {
        return dificultad;
    }

    public void setDificultad(int dificultad) {
        this.dificultad = dificultad;
    }
}
