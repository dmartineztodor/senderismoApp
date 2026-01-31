package com.danimt.appsenderismo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RutaDao {
    @Insert
    void insert(Ruta ruta);

    @Update
    void update(Ruta ruta);

    @Delete
    void delete(Ruta ruta);

    @Query("SELECT * FROM rutas")
    List<Ruta> getAllRutas();

    @Query("SELECT * FROM rutas WHERE dificultad = :dificultad")
    List<Ruta> getByDificultad(float dificultad);


    // Para guardar un punto nuevo desde el diálogo
    @Insert
    void insertPunto(PuntoInteres punto);

    // Para cargar la lista de puntos de una ruta específica
    @Query("SELECT * FROM puntos_interes WHERE ruta_id = :rutaId")
    List<PuntoInteres> getPuntosDeRuta(int rutaId);
}