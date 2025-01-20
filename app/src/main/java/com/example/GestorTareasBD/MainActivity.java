package com.example.GestorTareasBD;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tarea_7_gestortareas.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;  // Vista para mostrar una lista de tareas
    private TareaAdapter adaptador;     // Adaptador para conectar la lista de tareas con la vista
    private List<Tarea> listaTareas;    // Lista que contiene las tareas a mostrar
    private BaseDatosTareas baseDatosTareas; // Instancia de la base de datos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Configurar diseño sin bordes (opcional)
        setContentView( R.layout.activity_main);

        // Configurar la disposición de la actividad principal para adaptarse a los bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (vista, insets) -> {
            Insets bordesSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(bordesSistema.left, bordesSistema.top, bordesSistema.right, bordesSistema.bottom);
            return insets;
        });

        // Inicializar el RecyclerView y el botón flotante
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton botonFlotante = findViewById(R.id.fab);

        // Inicializar la base de datos
        baseDatosTareas = new BaseDatosTareas(this);

        // Crear una lista vacía y un adaptador para las tareas
        listaTareas = new ArrayList<>();
        adaptador = new TareaAdapter(listaTareas, this::mostrarOpcionesTarea);

        // Configurar el RecyclerView con un diseño vertical
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        // Configurar el botón flotante para agregar nuevas tareas
        botonFlotante.setOnClickListener(v -> mostrarDialogoTarea(null));

        // Cargar tareas desde la base de datos y ordenarlas
        cargarTareasDesdeBaseDeDatos();
        ordenarTareas();
    }

    /**
     * Cargar las tareas desde la base de datos.
     */
    private void cargarTareasDesdeBaseDeDatos() {
        listaTareas.clear();
        listaTareas.addAll(baseDatosTareas.obtenerTareas()); // Obtener tareas de la base de datos
        ordenarTareas ();
    }

    // Agregar una tarea a la base de datos
    private void agregarTareaBD(Tarea tarea) {
        baseDatosTareas.agregarTarea(tarea);
        cargarTareasDesdeBaseDeDatos ();  // Recargar las tareas desde la base de datos
    }

    // Eliminar una tarea de la base de datos
    private void eliminarTareaBD(Tarea tarea) {
        baseDatosTareas.eliminarTarea(tarea.getId());
        cargarTareasDesdeBaseDeDatos ();  // Recargar las tareas después de eliminar
    }

    // Actualizar el estado de la tarea en la base de datos
    private void actualizarTareaBD(Tarea tarea) {
        baseDatosTareas.actualizarTarea(tarea);
        cargarTareasDesdeBaseDeDatos ();  // Recargar las tareas después de actualizar
    }

    /**
     * Ordenar la lista de tareas por asignatura y luego por fecha y hora de entrega.
     */
    private void ordenarTareas() {
        listaTareas.sort((t1, t2) -> {
            int compararAsignatura = t1.getAsignatura().compareTo(t2.getAsignatura());
            if (compararAsignatura != 0) return compararAsignatura;

            try {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                Date fecha1 = formatoFecha.parse(t1.getFechaEntrega());
                Date fecha2 = formatoFecha.parse(t2.getFechaEntrega());
                return fecha1.compareTo(fecha2);
            } catch (ParseException e) {
                e.printStackTrace();
                return 0;
            }
        });
        adaptador.notifyDataSetChanged(); // Actualizar la vista
    }

    /**
     * Mostrar un cuadro de diálogo para agregar o editar una tarea.
     */
    private void mostrarDialogoTarea(Tarea tareaAEditar) {
        NuevaTareaDialogFragment dialogo = new NuevaTareaDialogFragment();

        if (tareaAEditar != null) {
            Bundle argumentos = new Bundle();
            argumentos.putParcelable("homework", tareaAEditar);
            dialogo.setArguments(argumentos);
        }

        dialogo.setOnTareaGuardadaListener(tarea -> {
            if (tareaAEditar == null) {
                agregarTareaBD(tarea); // Agregar nueva tarea a la base de datos
            } else {
                actualizarTareaBD(tarea); // Actualizar tarea en la base de datos
            }
            ordenarTareas(); // Reordenar tareas y actualizar vista
        });

        dialogo.show(getSupportFragmentManager(), "DialogoTarea");
    }

    /**
     * Mostrar opciones (editar, eliminar o completar) para una tarea seleccionada.
     */
    private void mostrarOpcionesTarea(Tarea tarea) {
        BottomSheetDialog dialogoOpciones = new BottomSheetDialog(this);
        View vistaOpciones = getLayoutInflater().inflate(R.layout.tareas_opciones, null);

        vistaOpciones.findViewById(R.id.editOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            mostrarDialogoTarea(tarea);  // Editar tarea
        });

        vistaOpciones.findViewById(R.id.deleteOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            confirmarEliminacion(tarea);  // Eliminar tarea
        });

        vistaOpciones.findViewById(R.id.completeOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            tarea.setEstaCompletada(true);  // Marcar tarea como completada
            actualizarTareaBD(tarea);  // Actualizar tarea en la base de datos
            adaptador.notifyDataSetChanged();  // Actualizar la vista
            Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show();
        });

        dialogoOpciones.setContentView(vistaOpciones);
        dialogoOpciones.show();
    }

    /**
     * Confirmar la eliminación de una tarea con un cuadro de diálogo.
     */
    private void confirmarEliminacion(Tarea tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialogo, cual) -> {
                    eliminarTareaBD(tarea); // Eliminar tarea de la base de datos
                    Toast.makeText(this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}