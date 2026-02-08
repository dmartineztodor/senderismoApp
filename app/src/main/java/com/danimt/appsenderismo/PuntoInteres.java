package com.danimt.appsenderismo;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

//Va a definir la tabla de puntos de interes y la relacion con la tabla ruta
@Entity(tableName = "puntos_interes",
        foreignKeys = @ForeignKey(entity = Ruta.class,
                parentColumns = "id",
                childColumns = "ruta_id",
                onDelete = ForeignKey.CASCADE)) //Establce que al borrarse la ruta,se borren sus puntos
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
    public String foto; // Guarda la URL o ruta del archivo


//foreign key,vincula este punto con una ruta
    @ColumnInfo(name = "ruta_id")
    public int ruta_id;

    public PuntoInteres() {
    }
}