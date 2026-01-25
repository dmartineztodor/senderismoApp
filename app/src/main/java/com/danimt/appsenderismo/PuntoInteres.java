package com.danimt.appsenderismo;

@Entity(tableName = "puntos_interes",
        foreignKeys = @ForeignKey(entity = Ruta.class,
                parentColumns = "id",
                childColumns = "ruta_id",
                onDelete = ForeignKey.CASCADE))
public class PuntoInteres {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String nombre;
    public double latitud;
    public double longitud;
    public int ruta_id; // Clave foránea

    // Constructor
}
