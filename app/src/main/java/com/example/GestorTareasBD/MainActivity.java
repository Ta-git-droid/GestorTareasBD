package com.example.GestorTareasBD;

import android.os.Bundle;
import android.util.Log;
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

        // Inicializar vistas y la base de datos
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton botonFlotante = findViewById(R.id.fab);
        baseDatosTareas = new BaseDatosTareas(this);

        listaTareas = new ArrayList<>();
        adaptador = new TareaAdapter(listaTareas, this::mostrarOpcionesTarea);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        botonFlotante.setOnClickListener(v -> mostrarDialogoTarea(null));

        cargarTareasDesdeBaseDeDatos();
        ordenarTareas();
    }

    private void cargarTareasDesdeBaseDeDatos() {
        listaTareas.clear();
        listaTareas.addAll(baseDatosTareas.obtenerTareas());
        ordenarTareas();
    }

    private void agregarTareaBD(Tarea tarea) {
        if (tarea.getId() == 0) {
            tarea.setId(generateNewId());  // Generar un nuevo ID si no tiene
        }

        if (baseDatosTareas.agregarTarea(tarea)) {
            cargarTareasDesdeBaseDeDatos();
        } else {
            Log.e("MainActivity", "Error al agregar la tarea: " + tarea.getTitulo());
        }
    }

    private void eliminarTareaBD(Tarea tarea) {
        if (tarea.getId() > 0) {
            baseDatosTareas.eliminarTarea(tarea.getId());
            cargarTareasDesdeBaseDeDatos();
        } else {
            Log.e("MainActivity", "ID de tarea no válido para eliminación: " + tarea.getId());
        }
    }

    private void actualizarTareaBD(Tarea tarea) {
        if (baseDatosTareas.actualizarTarea(tarea) > 0) {
            cargarTareasDesdeBaseDeDatos(); // Recargar tareas desde la base de datos
            adaptador.notifyDataSetChanged(); // Forzar la actualización del RecyclerView
        } else {
            Log.e("MainActivity", "Error al actualizar la tarea: " + tarea.getTitulo());
        }
    }

    private void ordenarTareas() {
        listaTareas.sort((t1, t2) -> {
            int asignaturaComparison = t1.getAsignatura().compareTo(t2.getAsignatura());
            if (asignaturaComparison != 0) return asignaturaComparison;

            try {
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
                Date fecha1 = formatoFecha.parse(t1.getFechaEntrega());
                Date fecha2 = formatoFecha.parse(t2.getFechaEntrega());
                return fecha1.compareTo(fecha2);
            } catch (ParseException e) {
                Log.e("MainActivity", "Error al parsear fechas", e);
                return 0;
            }
        });
        adaptador.notifyDataSetChanged();
    }

    private void mostrarDialogoTarea(Tarea tareaAEditar) {
        NuevaTareaDialogFragment dialogo = new NuevaTareaDialogFragment();
        if (tareaAEditar != null) {
            Bundle argumentos = new Bundle();
            argumentos.putParcelable("homework", tareaAEditar);
            dialogo.setArguments(argumentos);
        }

        dialogo.setOnTareaGuardadaListener(tarea -> {
            if (tareaAEditar == null) {
                agregarTareaBD(tarea);
            } else {
                actualizarTareaBD(tarea);
            }
        });

        dialogo.show(getSupportFragmentManager(), "DialogoTarea");
    }

    private void mostrarOpcionesTarea(Tarea tarea) {
        BottomSheetDialog dialogoOpciones = new BottomSheetDialog(this);
        View vistaOpciones = getLayoutInflater().inflate(R.layout.tareas_opciones, null);

        vistaOpciones.findViewById(R.id.editOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            mostrarDialogoTarea(tarea);
        });

        vistaOpciones.findViewById(R.id.deleteOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            confirmarEliminacion(tarea);
        });

        vistaOpciones.findViewById(R.id.completeOption).setOnClickListener(v -> {
            dialogoOpciones.dismiss();
            tarea.setEstaCompletada(true);
            actualizarTareaBD(tarea);
            Toast.makeText(this, "Tarea marcada como completada", Toast.LENGTH_SHORT).show();
        });

        dialogoOpciones.setContentView(vistaOpciones);
        dialogoOpciones.show();
    }

    private void confirmarEliminacion(Tarea tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarTareaBD(tarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Generador de un nuevo ID si es necesario
    private int generateNewId() {
        return listaTareas.size() + 1;  // Método simple para generar un nuevo ID
    }
}