package com.danimt.appsenderismo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RutaDao {
    @Insert
    void insert(Ruta ruta);

    @Delete
    void delete(Ruta ruta);

    @Query("SELECT * FROM rutas")
    List<Ruta> getAllRutas();

    @Query("SELECT * FROM rutas WHERE dificultad = :dificultad")
    List<Ruta> getRutasPorDificultad(float dificultad);

    // Método para obtener puntos de interés de una ruta específica
    @Query("SELECT * FROM puntos_interes WHERE ruta_id = :rutaId")
    List<PuntoInteres> getPuntosDeRuta(int rutaId);
}
