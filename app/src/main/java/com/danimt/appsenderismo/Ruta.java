package com.danimt.appsenderismo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "rutas")
public class Ruta implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "localizacion")
    public String localizacion;

    @ColumnInfo(name = "tipo")
    public String tipo;

    @ColumnInfo(name = "dificultad")
    public float dificultad;

    @ColumnInfo(name = "distancia")
    public double distancia;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    @ColumnInfo(name = "favorita")
    public boolean favorita;

    @ColumnInfo(name = "latitud")
    public double latitud;

    @ColumnInfo(name = "longitud")
    public double longitud;

    // --- NUEVO CAMPO PARA LA FOTO ---
    @ColumnInfo(name = "imagenUri")
    public String imagenUri;

    public Ruta() {
    }

    // Constructor ACTUALIZADO (Añadido imagenUri al final)
    @Ignore
    public Ruta(String nombre, String localizacion, String tipo, float dificultad, double distancia, String descripcion, boolean favorita, double latitud, double longitud, String imagenUri) {
        this.nombre = nombre;
        this.localizacion = localizacion;
        this.tipo = tipo;
        this.dificultad = dificultad;
        this.distancia = distancia;
        this.descripcion = descripcion;
        this.favorita = favorita;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagenUri = imagenUri; // Guardamos la ruta de la foto
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; } // Añadido setter por si acaso
    public double getDistancia() { return distancia; }
    public float getDificultad() { return dificultad; }
    public String getDescripcion() { return descripcion; }
    public boolean isFavorita() { return favorita; }
    public void setFavorita(boolean favorita) { this.favorita = favorita; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getTipo() { return tipo; }

    // Getter y Setter para la imagen
    public String getImagenUri() { return imagenUri; }
    public void setImagenUri(String imagenUri) { this.imagenUri = imagenUri; }

    public String getTiempoEstimado() {
        double velocidad = (dificultad >= 4) ? 3.0 : 4.0;
        double tiempo = distancia / velocidad;
        int horas = (int) tiempo;
        int minutos = (int) ((tiempo - horas) * 60);
        return horas + "h " + minutos + "m";
    }
}