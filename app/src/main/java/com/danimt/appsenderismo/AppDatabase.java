package com.danimt.appsenderismo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Ruta.class, PuntoInteres.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract RutaDao rutaDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "senderismo_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}