package com.example.GestorTareasBD;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.tarea_7_gestortareas.R;

import java.util.Calendar;

// Clase que representa un cuadro de diálogo para agregar o editar tareas
public class NuevaTareaDialogFragment extends DialogFragment {

    // Campos del formulario
    private EditText campoTitulo;        // Para el título de la tarea
    private EditText campoDescripcion;  // Para la descripción de la tarea
    private EditText campoFechaEntrega; // Para la fecha de entrega
    private EditText campoHoraEntrega;  // Para la hora de entrega
    private Spinner spinnerAsignatura;  // Para seleccionar la asignatura
    private OnTareaGuardadaListener listener; // Para notificar cuando se guarda una tarea
    private Tarea tareaAEditar;         // La tarea que se está editando (si aplica)


    // Método que se ejecuta cuando se crea el cuadro de diálogo
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Asegurarse de que la actividad que llama al diálogo no sea nula
        if (getActivity() == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        // Crear el cuadro de diálogo
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View vista = layoutInflater.inflate( R.layout.agregar_tarea, null);

        // Asociar las vistas del formulario con sus campos en el diseño XML
        campoTitulo = vista.findViewById(R.id.titulo);
        campoDescripcion = vista.findViewById(R.id.descripcion);
        campoFechaEntrega = vista.findViewById(R.id.fecha);
        campoHoraEntrega = vista.findViewById(R.id.hora);
        spinnerAsignatura = vista.findViewById(R.id.Spinner);

        // Mostrar un selector de fecha al hacer clic en el campo de fecha
        campoFechaEntrega.setOnClickListener(v -> mostrarDatePickerDialog());

        // Mostrar un selector de hora al hacer clic en el campo de hora
        campoHoraEntrega.setOnClickListener(v -> mostrarTimePickerDialog());

        // Si se pasa una tarea para editar, llenar los campos con sus datos
        if (getArguments() != null) {
            tareaAEditar = getArguments().getParcelable("homework");
            if (tareaAEditar != null) {
                campoTitulo.setText(tareaAEditar.getTitulo());
                campoDescripcion.setText(tareaAEditar.getDescripcion());
                campoFechaEntrega.setText(tareaAEditar.getFechaEntrega());
                campoHoraEntrega.setText(tareaAEditar.getHoraEntrega());
                spinnerAsignatura.setSelection(getIndice(spinnerAsignatura, tareaAEditar.getAsignatura()));
            }
        }

        // Configurar el botón "Guardar"
        Button botonGuardar = vista.findViewById(R.id.guardar);
        botonGuardar.setOnClickListener(v -> {
            if (validarEntradas()) { // Verificar que los campos no estén vacíos
                Tarea tarea = new Tarea(
                        spinnerAsignatura.getSelectedItem().toString(),
                        campoTitulo.getText().toString(),
                        campoDescripcion.getText().toString(),
                        campoFechaEntrega.getText().toString(),
                        campoHoraEntrega.getText().toString(),
                        false // La tarea comienza como "pendiente"
                );

                // Notificar que la tarea se guardó
                if (listener != null) {
                    listener.onTareaGuardada(tarea);
                }
                dismiss(); // Cerrar el cuadro de diálogo
            }
        });

        // Configurar el botón "Cancelar"
        Button botonCancelar = vista.findViewById(R.id.cancelar);
        botonCancelar.setOnClickListener(v -> dismiss());

        builder.setView(vista); // Establecer la vista personalizada
        return builder.create(); // Crear y devolver el cuadro de diálogo
    }


    // Obtener el índice de una asignatura en el spinner
    private int getIndice(Spinner spinnerAsignatura, String asignatura) {
        for (int i = 0; i < spinnerAsignatura.getCount(); i++) {
            if (spinnerAsignatura.getItemAtPosition(i).toString().equalsIgnoreCase(asignatura)) {
                return i;
            }
        }
        return 0;
    }


    // Interfaz para manejar el evento de guardar una tarea
    public interface OnTareaGuardadaListener {
        void onTareaGuardada(Tarea tarea);
    }


    // Establecer el listener para el evento
    public void setOnTareaGuardadaListener(OnTareaGuardadaListener listener) {
        this.listener = listener;
    }


    // Mostrar el selector de fecha
    private void mostrarDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (getContext() == null) return;
        new DatePickerDialog(
                getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    campoFechaEntrega.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }


    // Mostrar el selector de hora
    private void mostrarTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (getContext() == null) return;
        new TimePickerDialog(
                getContext(),
                (TimePicker view, int hourOfDay, int minute) -> {
                    campoHoraEntrega.setText(String.format("%02d:%02d", hourOfDay, minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }


    // Validar que los campos no estén vacíos
    private boolean validarEntradas() {
        if (TextUtils.isEmpty(campoTitulo.getText())) {
            campoTitulo.setError("El título es obligatorio");
            return false;
        }
        if (TextUtils.isEmpty(campoDescripcion.getText())) {
            campoDescripcion.setError("La descripción es obligatoria");
            return false;
        }
        if (TextUtils.isEmpty(campoFechaEntrega.getText())) {
            campoFechaEntrega.setError("La fecha de entrega es obligatoria");
            return false;
        }
        if (TextUtils.isEmpty(campoHoraEntrega.getText())) {
            campoHoraEntrega.setError("La hora de entrega es obligatoria");
            return false;
        }
        return true;
    }
}