package com.danimt.appsenderismo;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RutaDao {

    // --- MÉTODOS BÁSICOS ---
    @Insert
    void insert(Ruta ruta);

    @Update
    void update(Ruta ruta);

    @Delete
    void delete(Ruta ruta);

    @Query("SELECT * FROM rutas")
    List<Ruta> getAllRutas();

    // --- NUEVAS CONSULTAS DE FILTRADO (RANGOS) ---

    // Fácil: Menor de 2.5
    @Query("SELECT * FROM rutas WHERE dificultad < 2.5")
    List<Ruta> getRutasFaciles();

    // Media: Entre 2.5 y 4
    @Query("SELECT * FROM rutas WHERE dificultad >= 2.5 AND dificultad <= 4")
    List<Ruta> getRutasMedias();

    // Difícil: Mayor de 4
    @Query("SELECT * FROM rutas WHERE dificultad > 4")
    List<Ruta> getRutasDificiles();

    // (Mantenemos esta por si la usabas en otro lado, aunque con las de arriba ya cubres todo)
    @Query("SELECT * FROM rutas WHERE dificultad = :dificultad")
    List<Ruta> getByDificultad(float dificultad);


    // --- PUNTOS DE INTERÉS ---
    @Insert
    void insertPunto(PuntoInteres punto);

    @Query("SELECT * FROM puntos_interes WHERE ruta_id = :rutaId")
    List<PuntoInteres> getPuntosDeRuta(int rutaId);
}