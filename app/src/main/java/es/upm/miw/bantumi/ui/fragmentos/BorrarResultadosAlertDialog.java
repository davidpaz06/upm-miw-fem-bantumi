package es.upm.miw.bantumi.ui.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.actividades.MejoresResultadosActivity;

/**
 * Diálogo de confirmación para eliminar todos los resultados guardados
 */
public class BorrarResultadosAlertDialog extends DialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MejoresResultadosActivity activity = (MejoresResultadosActivity) requireActivity();

        return new AlertDialog.Builder(activity)
                .setTitle(R.string.txtBorrarResultados)
                .setMessage(R.string.txtBorrarResultadosPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> activity.borrarResultados()
                )
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
    }
}
