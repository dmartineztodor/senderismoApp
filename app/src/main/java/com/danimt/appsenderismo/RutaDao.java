package com.danimt.appsenderismo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RutaDao {

    // CAMBIO 1: Cambiamos 'void' por 'long'.
    // Esto devuelve el ID de la ruta recién creada.
    @Insert
    long insert(Ruta ruta);

    @Delete
    void delete(Ruta ruta);

    @androidx.room.Update
    void update(Ruta ruta);

    @Query("SELECT * FROM rutas")
    List<Ruta> getAllRutas();

    @Query("SELECT * FROM rutas WHERE dificultad = :dificultad")
    List<Ruta> getRutasPorDificultad(float dificultad);

    // CAMBIO 2: Añadimos método para insertar Puntos
    @Insert
    void insertPunto(PuntoInteres punto);

    @Query("SELECT * FROM puntos_interes WHERE ruta_id = :rutaId")
    List<PuntoInteres> getPuntosDeRuta(int rutaId);
}