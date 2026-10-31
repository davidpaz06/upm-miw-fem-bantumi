package es.upm.miw.bantumi.ui.fragmentos;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.actividades.MainActivity;

public class FinalAlertDialog extends DialogFragment {

    private static final String ARG_TITULO = "titulo";

    public static FinalAlertDialog newInstance(String titulo) {
        FinalAlertDialog dialog = new FinalAlertDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITULO, titulo);
        dialog.setArguments(args);
        return dialog;
    }

    @NonNull
    @Override
	public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
		final MainActivity main = (MainActivity) requireActivity();
        final String tituloAlertDialog = requireArguments().getString(ARG_TITULO);

        AlertDialog.Builder builder = new AlertDialog.Builder(main);
        builder
                .setTitle(tituloAlertDialog)
                .setMessage(R.string.txtDialogoFinalPregunta)
                .setPositiveButton(
                        getString(android.R.string.ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.reiniciarPartida();
                            }
                        }
                )
                .setNegativeButton(
                        getString(android.R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                main.finish();
                            }
                        }
                );

		return builder.create();
	}
}
