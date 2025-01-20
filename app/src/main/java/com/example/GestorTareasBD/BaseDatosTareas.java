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
    public long agregarTarea(Tarea tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASIGNATURA, tarea.getAsignatura());
        values.put(COLUMN_TITULO, tarea.getTitulo());
        values.put(COLUMN_DESCRIPCION, tarea.getDescripcion());
        values.put(COLUMN_FECHA_ENTREGA, tarea.getFechaEntrega());
        values.put(COLUMN_HORA_ENTREGA, tarea.getHoraEntrega());
        values.put(COLUMN_ESTACOMPLETADA, tarea.estaCompletada() ? 1 : 0);

        long id = db.insert(TABLE_TAREAS, null, values);
        db.close();
        return id;
    }

    // Método para obtener todas las tareas
    public List<Tarea> obtenerTareas() {
        List<Tarea> tareaList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TAREAS, null, null, null, null, null, null);

        // Comprobar que las columnas existen antes de acceder a ellas
        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int asignaturaIndex = cursor.getColumnIndex(COLUMN_ASIGNATURA);
        int tituloIndex = cursor.getColumnIndex(COLUMN_TITULO);
        int descripcionIndex = cursor.getColumnIndex(COLUMN_DESCRIPCION);
        int fechaEntregaIndex = cursor.getColumnIndex(COLUMN_FECHA_ENTREGA);
        int horaEntregaIndex = cursor.getColumnIndex(COLUMN_HORA_ENTREGA);
        int estaCompletadaIndex = cursor.getColumnIndex(COLUMN_ESTACOMPLETADA);

        // Verifica que todos los índices son válidos (mayores o iguales a 0)
        if (idIndex >= 0 && asignaturaIndex >= 0 && tituloIndex >= 0 && descripcionIndex >= 0 &&
                fechaEntregaIndex >= 0 && horaEntregaIndex >= 0 && estaCompletadaIndex >= 0) {

            // Recorre el cursor y obtiene los datos
            if (cursor.moveToFirst()) {
                do {
                    // Accede a las columnas
                    int id = cursor.getInt(idIndex);
                    String asignatura = cursor.getString(asignaturaIndex);
                    String titulo = cursor.getString(tituloIndex);
                    String descripcion = cursor.getString(descripcionIndex);
                    String fechaEntrega = cursor.getString(fechaEntregaIndex);
                    String horaEntrega = cursor.getString(horaEntregaIndex);
                    boolean estaCompletada = cursor.getInt(estaCompletadaIndex) == 1;

                    // Procesar los datos obtenidos (agregarlos a una lista)
                    Tarea tarea = new Tarea(id, asignatura, titulo, descripcion, fechaEntrega, horaEntrega, estaCompletada);
                    tareaList.add(tarea);
                } while (cursor.moveToNext());
            }
        } else {
            Log.e("BaseDeDatos", "No se encontraron algunas columnas en el cursor");
        }

        cursor.close();
        db.close();
        return tareaList;
    }

    // Método para eliminar una tarea
    public void eliminarTarea(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TAREAS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Método para actualizar el estado de la tarea
    public void actualizarTarea(Tarea tarea) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ASIGNATURA,tarea.setAsignatura(String));
        values.put(COLUMN_ESTACOMPLETADA, tarea.estaCompletada() ? 1 : 0);
        db.update(TABLE_TAREAS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(tarea.getId())});
        db.close();
    }
}