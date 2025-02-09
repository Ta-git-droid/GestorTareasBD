package com.example.GestorTareasBDRoom;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

// Clase que representa un cuadro de diálogo para agregar o editar tareas
public class NuevaTareaDialogFragment extends DialogFragment {

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
        if (getActivity() == null || getContext() == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        View vista = layoutInflater.inflate(R.layout.agregar_tarea, null);

        campoTitulo = vista.findViewById(R.id.titulo);
        campoDescripcion = vista.findViewById(R.id.descripcion);
        campoFechaEntrega = vista.findViewById(R.id.fecha);
        campoHoraEntrega = vista.findViewById(R.id.hora);
        spinnerAsignatura = vista.findViewById(R.id.Spinner);

        campoFechaEntrega.setOnClickListener(v -> mostrarDatePickerDialog());
        campoHoraEntrega.setOnClickListener(v -> mostrarTimePickerDialog());

        // Si estamos editando una tarea, rellenamos los campos
        if (getArguments() != null && getArguments().containsKey("homework")) {
            tareaAEditar = getArguments().getParcelable("homework");
            if (tareaAEditar != null) {
                campoTitulo.setText(tareaAEditar.getTitulo());
                campoDescripcion.setText(tareaAEditar.getDescripcion());
                campoFechaEntrega.setText(tareaAEditar.getFechaEntrega());
                campoHoraEntrega.setText(tareaAEditar.getHoraEntrega());
                spinnerAsignatura.setSelection(getIndice(spinnerAsignatura, tareaAEditar.getAsignatura()));
            }
        }

        Button botonGuardar = vista.findViewById(R.id.guardar);
        botonGuardar.setOnClickListener(v -> {
            if (validarEntradas()) {
                Tarea tarea;

                if (tareaAEditar != null) {
                    // Si estamos editando, mantenemos el ID
                    tarea = new Tarea(
                            tareaAEditar.getAsignatura(),
                            campoTitulo.getText().toString(),
                            campoDescripcion.getText().toString(),
                            campoFechaEntrega.getText().toString(),
                            campoHoraEntrega.getText().toString(),
                            tareaAEditar.estaCompletada()
                    );
                    tarea.setId(tareaAEditar.getId()); // Aseguramos que el ID de la tarea no cambie
                } else {
                    // Si estamos creando una nueva tarea, no pasamos el ID
                    tarea = new Tarea(
                            spinnerAsignatura.getSelectedItem().toString(),
                            campoTitulo.getText().toString(),
                            campoDescripcion.getText().toString(),
                            campoFechaEntrega.getText().toString(),
                            campoHoraEntrega.getText().toString(),
                            false // La tarea comienza como "pendiente"
                    );
                }

                // Llamamos al listener para que guarde la tarea
                if (listener != null) {
                    listener.onTareaGuardada(tarea);
                }
                dismiss(); // Cerrar el cuadro de diálogo
            }
        });

        Button botonCancelar = vista.findViewById(R.id.cancelar);
        botonCancelar.setOnClickListener(v -> dismiss());

        builder.setView(vista);
        return builder.create();
    }

    private int getIndice(Spinner spinnerAsignatura, String asignatura) {
        for (int i = 0; i < spinnerAsignatura.getCount(); i++) {
            if (spinnerAsignatura.getItemAtPosition(i).toString().equalsIgnoreCase(asignatura)) {
                return i;
            }
        }
        return 0;
    }

    public interface OnTareaGuardadaListener {
        void onTareaGuardada(Tarea tarea);
    }

    public void setOnTareaGuardadaListener(OnTareaGuardadaListener listener) {
        this.listener = listener;
    }

    private void mostrarDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (getContext() == null) return;
        new DatePickerDialog(
                getContext(),
                (DatePicker view, int year, int month, int dayOfMonth) -> {
                    // Formato de la fecha: dd/MM/yyyy
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    calendar.set(year, month, dayOfMonth);
                    campoFechaEntrega.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void mostrarTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        if (getContext() == null) return;
        new TimePickerDialog(
                getContext(),
                (TimePicker view, int hourOfDay, int minute) -> {
                    // Formato de la hora: HH:mm
                    campoHoraEntrega.setText(String.format( Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        ).show();
    }

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