package com.danimt.appsenderismo;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Defino las tablas (Ruta y PuntoInteres) y subo la versión a 2 por el cambio de la foto
@Database(entities = {Ruta.class, PuntoInteres.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    // Método abstracto para acceder al DAO
    public abstract RutaDao rutaDao();

    // Volatile asegura que todos los hilos vean la última versión de la instancia
    private static volatile AppDatabase INSTANCE;

    // Patrón Singleton para tener una única conexión a la BD en toda la app
    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            // Synchronized evita que dos hilos creen la BD a la vez
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "senderismo_database")
                            // Si cambio la estructura de la tabla, borra y crea de nuevo para no crashear
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}