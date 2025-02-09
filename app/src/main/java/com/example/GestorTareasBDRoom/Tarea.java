package com.example.GestorTareasBDRoom;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room requiere que los modelos de datos estén anotados con @Entity, y las columnas dentro de la tabla con @ColumnInfo.
// También, el ID de la tabla debe estar anotado con @PrimaryKey.
@Entity(tableName = "tareas")
public class Tarea implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String asignatura;
    private String titulo;
    private String descripcion;
    private String fechaEntrega;
    private String horaEntrega;
    private boolean estaCompletada;

    // Constructor, getters y setters

    public Tarea(String asignatura, String titulo, String descripcion,
                 String fechaEntrega, String horaEntrega, boolean estaCompletada) {
        this.asignatura = asignatura;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.estaCompletada = estaCompletada;
    }

    protected Tarea(Parcel in) {
        id = in.readInt ();
        asignatura = in.readString ();
        titulo = in.readString ();
        descripcion = in.readString ();
        fechaEntrega = in.readString ();
        horaEntrega = in.readString ();
        estaCompletada = in.readByte () != 0;
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea> () {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea ( in );
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(String asignatura) {
        this.asignatura = asignatura;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(String fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public String getHoraEntrega() {
        return horaEntrega;
    }

    public void setHoraEntrega(String horaEntrega) {
        this.horaEntrega = horaEntrega;
    }

    public boolean estaCompletada() {
        return estaCompletada;
    }

    public void setEstaCompletada(boolean estaCompletada) {
        this.estaCompletada = estaCompletada;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel , int i) {
        parcel.writeInt ( id );
        parcel.writeString ( asignatura );
        parcel.writeString ( titulo );
        parcel.writeString ( descripcion );
        parcel.writeString ( fechaEntrega );
        parcel.writeString ( horaEntrega );
        parcel.writeByte ( (byte) (estaCompletada ? 1 : 0) );
    }
}