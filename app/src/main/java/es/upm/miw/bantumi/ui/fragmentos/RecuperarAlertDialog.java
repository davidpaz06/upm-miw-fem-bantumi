package es.upm.miw.bantumi.ui.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.actividades.MainActivity;

/**
 * Diálogo de confirmación para recuperar una partida guardada cuando
 * la partida actual se ha modificado
 */
public class RecuperarAlertDialog extends DialogFragment {

    private static final String ARG_FICHERO = "fichero";

    /**
     * @param nombreFichero fichero de la partida que se recuperará si el usuario confirma
     */
    public static RecuperarAlertDialog newInstance(String nombreFichero) {
        RecuperarAlertDialog dialog = new RecuperarAlertDialog();
        Bundle args = new Bundle();
        args.putString(ARG_FICHERO, nombreFichero);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();
        final String nombreFichero = requireArguments().getString(ARG_FICHERO);

        return new AlertDialog.Builder(main)
                .setTitle(R.string.txtOpcionRecuperar)
                .setMessage(R.string.txtRecuperarPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        (dialog, which) -> main.recuperarPartida(nombreFichero)
                )
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
    }
}
