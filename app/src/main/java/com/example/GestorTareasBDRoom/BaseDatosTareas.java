package com.example.GestorTareasBDRoom;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// Room utiliza una clase abstracta de base de datos anotada con @Database.
// Esta clase es responsable de mantener las entidades y el acceso a los DAOs.
@Database(entities = {Tarea.class}, version = 1)
public abstract class BaseDatosTareas extends RoomDatabase {

    private static BaseDatosTareas instance;

    public abstract TareaDao tareaDao();

    public static synchronized BaseDatosTareas obtenerInstancia(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            BaseDatosTareas.class, "gestor_tareas.db")
                    .fallbackToDestructiveMigration() // Para manejar cambios en la base de datos
                    .build();
        }
        return instance;
    }
}