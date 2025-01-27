package com.example.GestorTareasBD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BaseDatosTareas extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "GestorTareasDB";
    private static final int DATABASE_VERSION = 1;

    // Nombre de la tabla y columnas
    private static final String TABLE_TAREAS = "tareas";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ASIGNATURA = "asignatura";
    private static final String COLUMN_TITULO = "titulo";
    private static final String COLUMN_DESCRIPCION = "descripcion";
    private static final String COLUMN_FECHA_ENTREGA = "fechaEntrega";
    private static final String COLUMN_HORA_ENTREGA = "horaEntrega";
    private static final String COLUMN_ESTACOMPLETADA = "estaCompletada";

    public BaseDatosTareas(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla de tareas
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TAREAS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ASIGNATURA + " TEXT,"
                + COLUMN_TITULO + " TEXT,"
                + COLUMN_DESCRIPCION + " TEXT,"
                + COLUMN_FECHA_ENTREGA + " TEXT,"
                + COLUMN_HORA_ENTREGA + " TEXT,"
                + COLUMN_ESTACOMPLETADA + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }

    // Método para agregar una tarea
    public boolean agregarTarea(Tarea tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASIGNATURA, tarea.getAsignatura());
        values.put(COLUMN_TITULO, tarea.getTitulo());
        values.put(COLUMN_DESCRIPCION, tarea.getDescripcion());
        values.put(COLUMN_FECHA_ENTREGA, tarea.getFechaEntrega());
        values.put(COLUMN_HORA_ENTREGA, tarea.getHoraEntrega());
        values.put(COLUMN_ESTACOMPLETADA, tarea.estaCompletada() ? 1 : 0);

        boolean tareaAgregada = false;
        try {
            // Insertar la tarea y verificar si la inserción fue exitosa
            long id = db.insert(TABLE_TAREAS, null, values);
            if (id != -1) {
                tarea.setId((int) id);  // Asignamos el ID generado
                tareaAgregada = true;  // La tarea fue agregada con éxito
            } else {
                Log.e("BaseDeDatos", "Error al insertar tarea. ID no válido.");
            }
        } catch (Exception e) {
            Log.e("BaseDeDatos", "Error al insertar tarea: " + e.getMessage());
        } finally {
            db.close();  // Asegurar que la base de datos se cierre al finalizar
        }

        return tareaAgregada;
    }

    // Método para obtener todas las tareas
    public List<Tarea> obtenerTareas() {
        List<Tarea> tareaList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_TAREAS, null, null, null, null, null, null);

            // Comprobar que las columnas existen antes de acceder a ellas
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(COLUMN_ID);
                int asignaturaIndex = cursor.getColumnIndex(COLUMN_ASIGNATURA);
                int tituloIndex = cursor.getColumnIndex(COLUMN_TITULO);
                int descripcionIndex = cursor.getColumnIndex(COLUMN_DESCRIPCION);
                int fechaEntregaIndex = cursor.getColumnIndex(COLUMN_FECHA_ENTREGA);
                int horaEntregaIndex = cursor.getColumnIndex(COLUMN_HORA_ENTREGA);
                int estaCompletadaIndex = cursor.getColumnIndex(COLUMN_ESTACOMPLETADA);

                do {
                    int id = cursor.getInt(idIndex);
                    String asignatura = cursor.getString(asignaturaIndex);
                    String titulo = cursor.getString(tituloIndex);
                    String descripcion = cursor.getString(descripcionIndex);
                    String fechaEntrega = cursor.getString(fechaEntregaIndex);
                    String horaEntrega = cursor.getString(horaEntregaIndex);
                    boolean estaCompletada = cursor.getInt(estaCompletadaIndex) == 1;

                    Tarea tarea = new Tarea(id, asignatura, titulo, descripcion, fechaEntrega, horaEntrega, estaCompletada);
                    tareaList.add(tarea);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BaseDeDatos", "Error al obtener tareas: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();  // Asegurar que el cursor se cierre
            db.close();  // Cerrar la base de datos
        }

        return tareaList;
    }

    // Método para eliminar una tarea
    public void eliminarTarea(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_TAREAS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        } catch (Exception e) {
            Log.e("BaseDeDatos", "Error al eliminar tarea: " + e.getMessage());
        } finally {
            db.close();  // Cerrar la base de datos después de la operación
        }
    }

    // Método para actualizar el estado de la tarea
    public int actualizarTarea(Tarea tarea) {
        if (tarea.getId() <= 0) {
            Log.e("BaseDeDatos", "ID de tarea no válido: " + tarea.getId());
            return 0; // No realizamos ninguna actualización si el ID no es válido
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASIGNATURA, tarea.getAsignatura());
        values.put(COLUMN_TITULO, tarea.getTitulo());
        values.put(COLUMN_DESCRIPCION, tarea.getDescripcion());
        values.put(COLUMN_FECHA_ENTREGA, tarea.getFechaEntrega());
        values.put(COLUMN_HORA_ENTREGA, tarea.getHoraEntrega());
        values.put(COLUMN_ESTACOMPLETADA, tarea.estaCompletada() ? 1 : 0);

        int filasActualizadas = 0;

        try {
            // Verificar si la tarea existe antes de intentar actualizarla
            String selectQuery = "SELECT " + COLUMN_ID + " FROM " + TABLE_TAREAS + " WHERE " + COLUMN_ID + " = ?";
            Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(tarea.getId())});

            if (cursor != null && cursor.moveToFirst()) {
                // Si existe, actualizamos la tarea
                filasActualizadas = db.update(TABLE_TAREAS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(tarea.getId())});
            } else {
                // Si no existe, logueamos un mensaje
                Log.e("BaseDeDatos", "Tarea con ID " + tarea.getId() + " no encontrada.");
            }
            if (cursor != null) cursor.close(); // Cerrar el cursor si se abrió
        } catch (Exception e) {
            Log.e("BaseDeDatos", "Error al actualizar tarea: " + e.getMessage());
        } finally {
            db.close();  // Cerrar la base de datos
        }

        return filasActualizadas;  // Retornar el número de filas afectadas
    }
}