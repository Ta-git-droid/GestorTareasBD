package com.example.GestorTareasBD;

import android.os.Parcel;
import android.os.Parcelable;

// Clase Tarea: representa una tarea que puede ser asignada y completada.
// Implementa Parcelable para permitir que se envíen objetos Tarea entre componentes de Android (como Activities y Fragments).
public class Tarea implements Parcelable {

    // Atributos de la clase Tarea:
    private int id;                // ID único de la tarea (utilizado para la base de datos).
    private String asignatura;      // Nombre de la asignatura (por ejemplo, PMDM, AD, etc.).
    private String titulo;          // Título o nombre de la tarea.
    private String descripcion;     // Descripción detallada de la tarea.
    private String fechaEntrega;    // Fecha en la que la tarea debe entregarse (formato: dd/MM/yyyy).
    private String horaEntrega;     // Hora en la que la tarea debe entregarse (formato: HH:mm).
    private boolean estaCompletada; // Indica si la tarea está completada (true) o pendiente (false).

    // Constructor con ID
    public Tarea(int id, String asignatura, String titulo, String descripcion, String fechaEntrega, String horaEntrega, boolean estaCompletada) {
        this.id = id;
        this.asignatura = asignatura;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.estaCompletada = estaCompletada;
    }

    // Constructor sin ID (para insertar tareas)
    public Tarea(String asignatura, String titulo, String descripcion, String fechaEntrega, String horaEntrega, boolean estaCompletada) {
        this.asignatura = asignatura;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.horaEntrega = horaEntrega;
        this.estaCompletada = estaCompletada;
    }

    // Métodos "getter" y "setter" para cada atributo.
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

    // Parcelable: permite "empaquetar" objetos Tarea para enviarlos entre Activities o Fragments.

    protected Tarea(Parcel in) {
        id = in.readInt();
        asignatura = in.readString();
        titulo = in.readString();
        descripcion = in.readString();
        fechaEntrega = in.readString();
        horaEntrega = in.readString();
        estaCompletada = in.readByte() != 0;
    }

    public static final Creator<Tarea> CREATOR = new Creator<Tarea>() {
        @Override
        public Tarea createFromParcel(Parcel in) {
            return new Tarea(in);
        }

        @Override
        public Tarea[] newArray(int size) {
            return new Tarea[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(asignatura);
        dest.writeString(titulo);
        dest.writeString(descripcion);
        dest.writeString(fechaEntrega);
        dest.writeString(horaEntrega);
        dest.writeByte((byte) (estaCompletada ? 1 : 0));
    }
}