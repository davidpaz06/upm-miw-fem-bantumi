package es.upm.miw.bantumi.ui.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialog;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.datos.RepositorioPartida;
import es.upm.miw.bantumi.datos.RepositorioPartida.PartidaGuardada;
import es.upm.miw.bantumi.ui.actividades.MainActivity;

/**
 * Lista las partidas guardadas para que el usuario elija cuál recuperar
 */
public class SeleccionarPartidaDialog extends DialogFragment {

    @NonNull
    @Override
    public AppCompatDialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity main = (MainActivity) requireActivity();
        final List<PartidaGuardada> partidas = new RepositorioPartida(main).listar();

        DateFormat formatoFecha = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
        String[] etiquetas = new String[partidas.size()];
        for (int i = 0; i < etiquetas.length; i++) {
            PartidaGuardada partida = partidas.get(i);
            etiquetas[i] = getString(
                    R.string.txtItemPartida,
                    formatoFecha.format(new Date(partida.fecha)),
                    partida.semillasJ1,
                    partida.semillasJ2
            );
        }

        return new AlertDialog.Builder(main)
                .setTitle(R.string.txtOpcionRecuperar)
                .setItems(etiquetas, (dialog, which) ->
                        main.seleccionarPartida(partidas.get(which).nombreFichero))
                .setNegativeButton(getString(android.R.string.cancel), null)
                .create();
    }
}
