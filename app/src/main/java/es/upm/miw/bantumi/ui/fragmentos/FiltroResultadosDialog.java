package es.upm.miw.bantumi.ui.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.actividades.MejoresResultadosActivity;

/**
 * Diálogo para filtrar los mejores resultados por jugador y por número de semillas
 */
public class FiltroResultadosDialog extends DialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MejoresResultadosActivity activity = (MejoresResultadosActivity) requireActivity();

        View vista = LayoutInflater.from(activity).inflate(R.layout.dialog_filtro_resultados, null);
        final EditText etNombre = vista.findViewById(R.id.etFiltroNombre);
        final EditText etSemillas = vista.findViewById(R.id.etFiltroSemillas);
        if (savedInstanceState == null) {   // Si se recrea, el sistema restaura lo tecleado
            etNombre.setText(activity.getFiltroNombre());
            if (activity.getFiltroMinSemillas() > 0) {
                etSemillas.setText(String.valueOf(activity.getFiltroMinSemillas()));
            }
        }

        return new AlertDialog.Builder(activity)
                .setTitle(R.string.txtFiltrarResultados)
                .setView(vista)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> activity.aplicarFiltro(
                                etNombre.getText().toString(),
                                etSemillas.getText().toString()
                        )
                )
                .setNeutralButton(
                        R.string.txtQuitarFiltro,
                        (dialog, which) -> activity.aplicarFiltro("", "")
                )
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
    }
}
