package com.example.GestorTareasBDRoom;

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
import androidx.lifecycle.Observer;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    // Creando una instancia del Executor para ejecutar tareas en segundo plano
    private Executor executor = Executors.newSingleThreadExecutor();

    private RecyclerView recyclerView;  // Vista para mostrar una lista de tareas
    private TareaAdapter adaptador;     // Adaptador para conectar la lista de tareas con la vista
    private BaseDatosTareas baseDatosTareas; // Instancia de la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Configurar diseño sin bordes (opcional)
        setContentView(R.layout.activity_main);

        // Configurar la disposición de la actividad principal para adaptarse a los bordes del sistema
        // Esto solo es necesario si se quiere manejar el recorte de bordes del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_main), (vista, insets) -> {
            Insets bordesSistema = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            vista.setPadding(bordesSistema.left, bordesSistema.top, bordesSistema.right, bordesSistema.bottom);
            return insets;
        });

        // Inicializar vistas y la base de datos
        recyclerView = findViewById(R.id.recyclerView);
        FloatingActionButton botonFlotante = findViewById(R.id.fab);
        baseDatosTareas = BaseDatosTareas.obtenerInstancia(this); // Usar el método singleton para obtener la instancia

        adaptador = new TareaAdapter(new ArrayList<>(), this::mostrarOpcionesTarea);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adaptador);

        botonFlotante.setOnClickListener(v -> mostrarDialogoTarea(null));

        // Observar las tareas desde la base de datos
        baseDatosTareas.tareaDao().obtenerTareas().observe(this, new Observer<List<Tarea>>() {
            @Override
            public void onChanged(List<Tarea> tareas) {
                if (tareas != null) {
                    ordenarTareas(tareas);
                    adaptador.setTareas(tareas); // Actualizar la lista en el adaptador
                }
            }
        });
    }

    // Método para ordenar las tareas por asignatura y fecha
    private void ordenarTareas(List<Tarea> tareas) {
        tareas.sort((t1, t2) -> {
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
    }

    // Método para agregar una tarea en la base de datos
    private void agregarTareaBD(Tarea tarea) {
        // Usamos el executor para ejecutar la operación en segundo plano
        executor.execute(() -> {
            long id = baseDatosTareas.tareaDao().insertarTarea(tarea);
            runOnUiThread(() -> {
                if (id != -1) {
                    Log.d("MainActivity", "Tarea agregada con éxito: " + tarea.getTitulo());
                    Toast.makeText(MainActivity.this, "Tarea agregada con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "Error al agregar la tarea: " + tarea.getTitulo());
                    Toast.makeText(MainActivity.this, "Error al agregar tarea", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Método para eliminar una tarea de la base de datos
    private void eliminarTareaBD(Tarea tarea) {
        executor.execute(() -> {
            baseDatosTareas.tareaDao().eliminarTarea(tarea);
            runOnUiThread(() -> {
                Log.d("MainActivity", "Tarea eliminada: " + tarea.getTitulo());
                Toast.makeText(MainActivity.this, "Tarea eliminada", Toast.LENGTH_SHORT).show();
            });
        });
    }

    // Método para actualizar una tarea en la base de datos
    private void actualizarTareaBD(Tarea tarea) {
        executor.execute(() -> {
            int rowsAffected = baseDatosTareas.tareaDao().actualizarTarea(tarea);
            runOnUiThread(() -> {
                if (rowsAffected > 0) {
                    Log.d("MainActivity", "Tarea actualizada con éxito: " + tarea.getTitulo());
                    Toast.makeText(MainActivity.this, "Tarea actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("MainActivity", "Error al actualizar la tarea: " + tarea.getTitulo());
                    Toast.makeText(MainActivity.this, "Error al actualizar tarea", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // Mostrar el diálogo para agregar o editar una tarea
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

    // Mostrar las opciones para editar, eliminar o marcar como completada una tarea
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

    // Confirmar la eliminación de una tarea
    private void confirmarEliminacion(Tarea tarea) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar esta tarea?")
                .setPositiveButton("Eliminar", (dialog, which) -> eliminarTareaBD(tarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}