package com.example.GestorTareasBDRoom;

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

    private List<Tarea> listaTareas;  // Cambié esto a no final para poder modificar la lista después
    private final OnTareaClickListener listener;

    public TareaAdapter(List<Tarea> listaTareas, OnTareaClickListener listener) {
        this.listaTareas = listaTareas;
        this.listener = listener;
    }
    public void setTareas(List<Tarea> listaTareas) {
        this.listaTareas = listaTareas;
        notifyDataSetChanged();  // Notifica al adaptador que los datos han cambiado
    }

    @NonNull
    @Override
    public TareaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout para cada item de la lista
        View vistaDeItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarea, parent, false);
        return new TareaViewHolder(vistaDeItem);
    }

    @Override
    public void onBindViewHolder(@NonNull TareaViewHolder holder, int position) {
        Tarea tareaActual = listaTareas.get(position);

        // Asignamos los valores a cada uno de los TextViews
        holder.asignaturaTextView.setText(tareaActual.getAsignatura());
        holder.tituloTextView.setText(tareaActual.getTitulo());
        holder.descripcionTextView.setText(tareaActual.getDescripcion());
        holder.fechaEntregaTextView.setText("Fecha de entrega: " + tareaActual.getFechaEntrega());
        holder.horaEntregaTextView.setText("Hora de entrega: " + tareaActual.getHoraEntrega());
        holder.estadoTextView.setText(tareaActual.estaCompletada() ? "Completada" : "Pendiente");

        // Configuramos el evento de clic
        holder.itemView.setOnClickListener(v -> listener.onTareaClick(tareaActual));
    }

    @Override
    public int getItemCount() {
        return listaTareas.size(); // Retorna la cantidad de elementos en la lista
    }

    // Método para actualizar la lista de tareas en el adaptador
    public void actualizarLista(List<Tarea> nuevaLista) {
        listaTareas = nuevaLista;
        notifyDataSetChanged(); // Notifica que la lista ha cambiado
    }

    // Método para eliminar una tarea de la lista
    public void eliminarTarea(int position) {
        listaTareas.remove(position);  // Elimina la tarea de la lista
        notifyItemRemoved(position);   // Notifica que un item ha sido eliminado
    }

    // Método para actualizar una tarea en la lista
    public void actualizarTarea(int position, Tarea tarea) {
        listaTareas.set(position, tarea); // Actualiza la tarea en la lista
        notifyItemChanged(position);  // Notifica que un item ha sido actualizado
    }

    public static class TareaViewHolder extends RecyclerView.ViewHolder {
        TextView asignaturaTextView;
        TextView tituloTextView;
        TextView descripcionTextView;
        TextView fechaEntregaTextView;
        TextView horaEntregaTextView;
        TextView estadoTextView;

        public TareaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Enlazamos las vistas con los elementos de la UI
            asignaturaTextView = itemView.findViewById(R.id.asignaturaTextView);
            tituloTextView = itemView.findViewById(R.id.tituloTextView);
            descripcionTextView = itemView.findViewById(R.id.descripcionTextView);
            fechaEntregaTextView = itemView.findViewById(R.id.fechaEntregaTextView);
            horaEntregaTextView = itemView.findViewById(R.id.horaEntregaTextView);
            estadoTextView = itemView.findViewById(R.id.estadoTextView);
        }
    }

    // Interfaz que permitirá ejecutar acciones cuando se haga clic en una tarea
    public interface OnTareaClickListener {
        void onTareaClick(Tarea tarea);
    }
}