package com.danimt.appsenderismo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "rutas") // 1. Define el nombre de la tabla
public class Ruta implements Serializable {

    // 2. Clave primaria autogenerada
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "nombre")
    private String nombre;

    @ColumnInfo(name = "localizacion")
    private String localizacion;

    @ColumnInfo(name = "tipo")
    private String tipo; // Circular o lineal

    @ColumnInfo(name = "dificultad")
    private float dificultad; // De 1 a 5

    @ColumnInfo(name = "distancia")
    private double distancia;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "notas")
    private String notas;

    @ColumnInfo(name = "favorita")
    private boolean favorita;

    @ColumnInfo(name = "latitud")
    private double latitud;

    @ColumnInfo(name = "longitud")
    private double longitud;

    // 3. Constructor vacío (OBLIGATORIO PARA ROOM)
    public Ruta() {
    }

    // 4. Constructor lógico (Marcado con @Ignore para que Room no se confunda)
    @Ignore
    public Ruta(String nombre, String localizacion, String tipo, float dificultad, double distancia, String descripcion, boolean favorita) {
        this.nombre = nombre;
        this.localizacion = localizacion;
        this.tipo = tipo;
        this.dificultad = dificultad;
        this.distancia = distancia;
        this.descripcion = descripcion;
        this.favorita = favorita;
    }

    // --- GETTERS Y SETTERS (Room los necesita todos) ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getLocalizacion() { return localizacion; }
    public void setLocalizacion(String localizacion) { this.localizacion = localizacion; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public float getDificultad() { return dificultad; }
    public void setDificultad(float dificultad) { this.dificultad = dificultad; }

    public double getDistancia() { return distancia; }
    public void setDistancia(double distancia) { this.distancia = distancia; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }

    public boolean isFavorita() { return favorita; }
    public void setFavorita(boolean favorita) { this.favorita = favorita; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    // --- LÓGICA DE NEGOCIO (No afecta a la BD) ---

    // Distancia / 3km/h (difícil) o 4km/h
    public String getTiempoEstimado() {
        double velocidad = (dificultad >= 4) ? 3.0 : 4.0;
        double tiempo = distancia / velocidad;
        int horas = (int) tiempo;
        int minutos = (int) ((tiempo - horas) * 60);
        return horas + "h " + minutos + "m";
    }
}