package com.danimt.appsenderismo;
import java.io.Serializable;
public class Ruta implements Serializable {
    private String nombre;
    private String localizacion;
    private String tipo; // "Circular" o "Lineal"
    private float dificultad; // De 1 a 5
    private double distancia; // En km
    private String descripcion;
    private String notas;
    private boolean favorita;
    // Nuevos campos pedidos en el PDF
    private double latitud;
    private double longitud;

    public Ruta(String nombre, String localizacion, String tipo, float dificultad, double distancia, String descripcion, boolean favorita) {
        this.nombre = nombre;
        this.localizacion = localizacion;
        this.tipo = tipo;
        this.dificultad = dificultad;
        this.distancia = distancia;
        this.descripcion = descripcion;
        this.favorita = favorita;
    }


    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public float getDificultad() { return dificultad; }
    public double getDistancia() { return distancia; }
    public String getDescripcion() { return descripcion; }
    public boolean isFavorita() { return favorita; }
    public void setFavorita(boolean favorita) { this.favorita = favorita; }


    // Distancia / 3km/h (difícil) o 4km/h
    public String getTiempoEstimado() {
        double velocidad = (dificultad >= 4) ? 3.0 : 4.0;
        double tiempo = distancia / velocidad;
        int horas = (int) tiempo;
        int minutos = (int) ((tiempo - horas) * 60);
        return horas + "h " + minutos + "m";
    }
}
