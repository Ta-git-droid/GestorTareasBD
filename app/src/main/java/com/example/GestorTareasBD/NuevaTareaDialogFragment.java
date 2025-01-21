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
    private EditText campoTitulo;
    private EditText campoDescripcion;
    private EditText campoFechaEntrega;
    private EditText campoHoraEntrega;
    private Spinner spinnerAsignatura;
    private OnTareaGuardadaListener listener;
    private Tarea tareaAEditar;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getActivity() == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View vista = layoutInflater.inflate(R.layout.agregar_tarea, null);

        // Asociar campos con sus elementos en el XML
        campoTitulo = vista.findViewById(R.id.titulo);
        campoDescripcion = vista.findViewById(R.id.descripcion);
        campoFechaEntrega = vista.findViewById(R.id.fecha);
        campoHoraEntrega = vista.findViewById(R.id.hora);
        spinnerAsignatura = vista.findViewById(R.id.Spinner);

        // Manejadores de fecha y hora
        campoFechaEntrega.setOnClickListener(v -> mostrarDatePickerDialog());
        campoHoraEntrega.setOnClickListener(v -> mostrarTimePickerDialog());

        // Si es tarea a editar, cargar los datos
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

        // Configuración del botón "Guardar"
        Button botonGuardar = vista.findViewById(R.id.guardar);
        botonGuardar.setOnClickListener(v -> {
            // Deshabilitar el botón para evitar doble clic
            botonGuardar.setEnabled(false);

            if (validarEntradas()) {
                Tarea tarea = new Tarea(
                        spinnerAsignatura.getSelectedItem().toString(),
                        campoTitulo.getText().toString(),
                        campoDescripcion.getText().toString(),
                        campoFechaEntrega.getText().toString(),
                        campoHoraEntrega.getText().toString(),
                        false
                );

                // Si es tarea a editar, actualizar la base de datos, si no, agregarla
                if (tareaAEditar != null) {
                    tarea.setId(tareaAEditar.getId());
                    actualizarTareaEnBaseDeDatos(tarea);
                } else {
                    agregarTareaEnBaseDeDatos(tarea);
                }

                if (listener != null) {
                    listener.onTareaGuardada(tarea);
                }

                dismiss();
            } else {
                botonGuardar.setEnabled(true);  // Habilitar el botón de nuevo si hubo error
            }
        });

        // Configuración del botón "Cancelar"
        Button botonCancelar = vista.findViewById(R.id.cancelar);
        botonCancelar.setOnClickListener(v -> dismiss());

        builder.setView(vista);
        return builder.create();
    }

    // Agregar tarea en la base de datos
    private void agregarTareaEnBaseDeDatos(Tarea tarea) {
        BaseDatosTareas dbHelper = new BaseDatosTareas(getContext());
        boolean tareaAgregada = dbHelper.agregarTarea(tarea); // Ahora devuelve un booleano

        if (tareaAgregada) {
            // Si la tarea fue agregada exitosamente, se puede establecer un ID ficticio o manejar de otra manera
            tarea.setId(0); // Por ejemplo, establecer el ID a 0 o lo que sea apropiado
        }
    }


    // Actualizar tarea en la base de datos
    private void actualizarTareaEnBaseDeDatos(Tarea tarea) {
        BaseDatosTareas dbHelper = new BaseDatosTareas(getContext());
        dbHelper.actualizarTarea(tarea);
    }

    // Obtener el índice de la asignatura seleccionada en el spinner
    private int getIndice(Spinner spinnerAsignatura, String asignatura) {
        for (int i = 0; i < spinnerAsignatura.getCount(); i++) {
            if (spinnerAsignatura.getItemAtPosition(i).toString().equalsIgnoreCase(asignatura)) {
                return i;
            }
        }
        return 0;
    }

    // Interface para la notificación de tarea guardada
    public interface OnTareaGuardadaListener {
        void onTareaGuardada(Tarea tarea);
    }

    // Establecer el listener para tarea guardada
    public void setOnTareaGuardadaListener(OnTareaGuardadaListener listener) {
        this.listener = listener;
    }

    // Mostrar el DatePickerDialog para seleccionar la fecha de entrega
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

    // Mostrar el TimePickerDialog para seleccionar la hora de entrega
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

    // Validar las entradas del formulario
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