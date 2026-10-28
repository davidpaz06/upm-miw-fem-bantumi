package es.upm.miw.bantumi.ui.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.actividades.MainActivity;

/**
 * Diálogo de confirmación para recuperar la partida guardada cuando
 * la partida actual se ha modificado
 */
public class RecuperarAlertDialog extends DialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();

        return new AlertDialog.Builder(main)
                .setTitle(R.string.txtOpcionRecuperar)
                .setMessage(R.string.txtRecuperarPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> main.recuperarPartida()
                )
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
    }
}
