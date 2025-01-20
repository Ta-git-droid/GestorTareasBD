package com.example.GestorTareasBD;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea_7_gestortareas.R;

import java.util.List;

// Esta clase es un adaptador para un RecyclerView.
// El adaptador se encarga de gestionar cómo se muestran los datos (tareas) en una lista.
public class TareaAdapter extends RecyclerView.Adapter<TareaAdapter.TareaViewHolder> {

    // Lista de tareas que se mostrará en el RecyclerView.
    // Es como una lista de datos que queremos mostrar en pantalla.
    private final List<Tarea> listaTareas;

    // Listener que permite manejar qué sucede cuando se hace clic en una tarea.
    private final OnTareaClickListener listener;


    // Constructor de la clase TareaAdapter.
    // Se utiliza para inicializar el adaptador con la lista de tareas y el listener.
    public TareaAdapter(List<Tarea> listaTareas, OnTareaClickListener listener) {
        this.listaTareas = listaTareas; // Guardamos la lista de tareas.
        this.listener = listener; // Guardamos el listener para manejar los clics.
    }


    // Este método se llama cuando se necesita un nuevo "item" o elemento para la lista.
    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Aquí inflamos el diseño XML de cada tarea (el "diseño" de cómo se ve un item en la lista).
        View vistaDeItem = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_tarea, parent, false);

        // Creamos un nuevo ViewHolder que manejará la vista inflada.
        return new TareaViewHolder(vistaDeItem);
    }


    // Este método se llama para "llenar" un item con datos de una tarea específica.
    @SuppressLint("SetTextI18n") // Ignoramos una advertencia sobre concatenar texto.
    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        // Obtenemos la tarea correspondiente a esta posición en la lista.
        Tarea tareaActual = listaTareas.get(position);

        // Llenamos las vistas con los datos de la tarea.
        holder.asignaturaTextView.setText(tareaActual.getAsignatura()); // Nombre de la asignatura.
        holder.tituloTextView.setText(tareaActual.getTitulo()); // Título de la tarea.
        holder.descripcionTextView.setText(tareaActual.getDescripcion()); // Descripción de la tarea.
        holder.fechaEntregaTextView.setText("Fecha de entrega: " + tareaActual.getFechaEntrega()); // Fecha de entrega.
        holder.horaEntregaTextView.setText("Hora de entrega: " + tareaActual.getHoraEntrega()); // Hora de entrega.
        holder.estadoTextView.setText(tareaActual.estaCompletada() ? "Completada" : "Pendiente"); // Estado de la tarea.

        // Configuramos un evento de clic para la tarea.
        holder.itemView.setOnClickListener(v -> listener.onTareaClick(tareaActual));
        // Cuando se hace clic, llamamos al método del listener con la tarea actual.
    }


    // Este método devuelve el número total de tareas en la lista.
    @Override
    public int getItemCount() {
        return listaTareas.size(); // Devolvemos el tamaño de la lista.
    }


    // Clase interna que se encarga de manejar las vistas de cada tarea.
    // Un ViewHolder contiene las referencias a los elementos visuales que usamos en cada item.
    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        // Declaramos las vistas que mostrarán la información de una tarea.
        TextView asignaturaTextView;
        TextView tituloTextView;
        TextView descripcionTextView;
        TextView fechaEntregaTextView;
        TextView horaEntregaTextView;
        TextView estadoTextView;

        // Constructor de TareaViewHolder.
        // Recibe la vista de un item y asocia las variables con los IDs de las vistas en el diseño XML.
        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Vinculamos las variables con las vistas específicas del diseño.
            asignaturaTextView = itemView.findViewById(R.id.asignaturaTextView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            fechaEntregaTextView = itemView.findViewById(R.id.fechaEntregaTextView);
            horaEntregaTextView = itemView.findViewById(R.id.horaEntregaTextView);
            estadoTextView = itemView.findViewById(R.id.estadoTextView);
        }
    }


    // Interfaz que define el comportamiento al hacer clic en una tarea.
    // Permite que otras clases decidan qué hacer cuando se hace clic en una tarea.
    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea); // Este método se llama cuando se hace clic en una tarea.
    }
}