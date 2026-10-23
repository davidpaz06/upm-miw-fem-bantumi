package es.upm.miw.bantumi.ui.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.dominio.logica.JuegoBantumi;
import es.upm.miw.bantumi.ui.actividades.MainActivity;

/**
 * Diálogo de confirmación para reiniciar la partida actual
 */
public class ReiniciarAlertDialog extends DialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();

        return new AlertDialog.Builder(main)
                .setTitle(R.string.txtReiniciarPartida)
                .setMessage(R.string.txtReiniciarPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> main.juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1)
                )
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
    }
}
