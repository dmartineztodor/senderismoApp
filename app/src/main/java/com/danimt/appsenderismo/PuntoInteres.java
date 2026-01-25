package com.danimt.appsenderismo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "puntos_interes",
        foreignKeys = @ForeignKey(entity = Ruta.class,
                parentColumns = "id",
                childColumns = "ruta_id",
                onDelete = ForeignKey.CASCADE))
public class PuntoInteres {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nombre")
    public String nombre;

    @ColumnInfo(name = "latitud")
    public double latitud;

    @ColumnInfo(name = "longitud")
    public double longitud;

    @ColumnInfo(name = "foto")
    public String foto; // Guardaremos la URL o ruta del archivo

    @ColumnInfo(name = "ruta_id")
    public int ruta_id;

    public PuntoInteres() {
    }
}