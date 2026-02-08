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

    // consultas de filtrado

    // Fácil: Menor de 2.5
    @Query("SELECT * FROM rutas WHERE dificultad < 2.5")
    List<Ruta> getRutasFaciles();

    // Media: Entre 2.5 y 4
    @Query("SELECT * FROM rutas WHERE dificultad >= 2.5 AND dificultad <= 4")
    List<Ruta> getRutasMedias();

    // Difícil: Mayor de 4
    @Query("SELECT * FROM rutas WHERE dificultad > 4")
    List<Ruta> getRutasDificiles();

    // valor de dificultad exacto
    @Query("SELECT * FROM rutas WHERE dificultad = :dificultad")
    List<Ruta> getByDificultad(float dificultad);


    //manejo de los puntos de interes
    @Insert
    void insertPunto(PuntoInteres punto);

    //recupera puntos de interes asociados al id de la ruta
    @Query("SELECT * FROM puntos_interes WHERE ruta_id = :rutaId")
    List<PuntoInteres> getPuntosDeRuta(int rutaId);
}