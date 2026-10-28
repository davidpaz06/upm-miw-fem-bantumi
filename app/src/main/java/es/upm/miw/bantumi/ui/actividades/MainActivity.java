package es.upm.miw.bantumi.ui.actividades;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Locale;

import es.upm.miw.bantumi.datos.RepositorioPartida;
import es.upm.miw.bantumi.ui.fragmentos.FinalAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.RecuperarAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.ReiniciarAlertDialog;
import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.dominio.logica.JuegoBantumi;
import es.upm.miw.bantumi.ui.viewmodel.BantumiViewModel;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    public JuegoBantumi juegoBantumi;
    private BantumiViewModel bantumiVM;
    int numInicialSemillas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Instancia el ViewModel y el juego, y asigna observadores a los huecos
        numInicialSemillas = getResources().getInteger(R.integer.intNumInicialSemillas);
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, JuegoBantumi.Turno.turnoJ1, numInicialSemillas);
        crearObservadores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.tvPlayer1)).setText(obtenerNombreJugador());
    }

    /**
     * @return nombre del jugador configurado en los ajustes
     */
    public String obtenerNombreJugador() {
        String nombre = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.prefKeyNombreJugador), null);
        return (nombre == null || nombre.trim().isEmpty())
                ? getString(R.string.txtPlayer1)
                : nombre.trim();
    }

    /**
     * Crea y subscribe los observadores asignados a las posiciones del tablero.
     * Si se modifica el contenido del tablero -> se actualiza la vista.
     */
    private void crearObservadores() {
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            int finalI = i;
            bantumiVM.getNumSemillas(i).observe(    // Huecos y almacenes
                    this,
                    new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            mostrarValor(finalI, juegoBantumi.getSemillas(finalI));
                        }
                    });
        }
        bantumiVM.getTurno().observe(   // Turno
                this,
                new Observer<JuegoBantumi.Turno>() {
                    @Override
                    public void onChanged(JuegoBantumi.Turno turno) {
                        marcarTurno(juegoBantumi.turnoActual());
                    }
                }
        );
    }

    /**
     * Indica el turno actual cambiando el color del texto
     *
     * @param turnoActual turno actual
     */
    private void marcarTurno(@NonNull JuegoBantumi.Turno turnoActual) {
        TextView tvJugador1 = findViewById(R.id.tvPlayer1);
        TextView tvJugador2 = findViewById(R.id.tvPlayer2);
        switch (turnoActual) {
            case turnoJ1:
                tvJugador1.setTextColor(getColor(R.color.white));
                tvJugador1.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(getColor(android.R.color.holo_blue_light));
                break;
            default:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador2.setTextColor(getColor(R.color.black));
        }
    }

    /**
     * Muestra el valor <i>valor</i> en la posición <i>pos</i>
     *
     * @param pos posición a actualizar
     * @param valor valor a mostrar
     */
    private void mostrarValor(int pos, int valor) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
        if (0 != idBoton) {
            TextView viewHueco = findViewById(idBoton);
            viewHueco.setText(String.valueOf(valor));
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.opciones_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.opcAcercaDe) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.aboutTitle)
                    .setMessage(R.string.aboutMessage)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else if (itemId == R.id.opcReiniciarPartida) {
            new ReiniciarAlertDialog().show(getSupportFragmentManager(), "REINICIAR_DIALOG");
        } else if (itemId == R.id.opcGuardarPartida) {
            guardarPartida();
        } else if (itemId == R.id.opcRecuperarPartida) {
            solicitarRecuperarPartida();
        } else if (itemId == R.id.opcAjustes) {
            startActivity(new Intent(this, BantumiPrefs.class));
        // @TODO!!! resto opciones
        } else {
            Snackbar.make(
                    findViewById(android.R.id.content),
                    getString(R.string.txtSinImplementar),
                    Snackbar.LENGTH_LONG
            ).show();
        }
        return true;
    }

    /**
     * Inicia una nueva partida (turno de J1) y la marca como no modificada
     */
    public void reiniciarPartida() {
        juegoBantumi.inicializar(JuegoBantumi.Turno.turnoJ1);
        bantumiVM.setPartidaModificada(false);
    }

    /**
     * Guarda el estado actual del tablero en el fichero de partida
     */
    private void guardarPartida() {
        int mensaje;
        try {
            new RepositorioPartida(this).guardar(juegoBantumi.serializa());
            bantumiVM.setPartidaModificada(false);
            mensaje = R.string.txtPartidaGuardada;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error al guardar la partida", e);
            mensaje = R.string.txtErrorGuardarPartida;
        }
        Snackbar.make(findViewById(android.R.id.content), getString(mensaje), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Recupera la partida guardada, si existe. Si la partida actual se ha
     * modificado, pide confirmación antes de sustituirla.
     */
    private void solicitarRecuperarPartida() {
        if (!new RepositorioPartida(this).existe()) {
            mostrarMensaje(R.string.txtNoHayPartidaGuardada);
        } else if (bantumiVM.isPartidaModificada()) {
            new RecuperarAlertDialog().show(getSupportFragmentManager(), "RECUPERAR_DIALOG");
        } else {
            recuperarPartida();
        }
    }

    /**
     * Sustituye la partida actual por la guardada en el fichero
     */
    public void recuperarPartida() {
        int mensaje;
        try {
            String guardada = new RepositorioPartida(this).cargar();
            if (guardada == null) {
                mensaje = R.string.txtNoHayPartidaGuardada;
            } else {
                juegoBantumi.deserializa(guardada);
                bantumiVM.setPartidaModificada(false);
                mensaje = R.string.txtPartidaRecuperada;
            }
        } catch (IOException | IllegalArgumentException e) {
            Log.e(LOG_TAG, "Error al recuperar la partida", e);
            mensaje = R.string.txtErrorRecuperarPartida;
        }
        mostrarMensaje(mensaje);
    }

    private void mostrarMensaje(int idMensaje) {
        Snackbar.make(findViewById(android.R.id.content), getString(idMensaje), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Acción que se ejecuta al pulsar sobre cualquier hueco
     *
     * @param v Vista pulsada (hueco)
     */
    public void huecoPulsado(@NonNull View v) {
        String resourceName = getResources().getResourceEntryName(v.getId()); // pXY
        int num = Integer.parseInt(resourceName.substring(resourceName.length() - 2));
        Log.i(LOG_TAG, "huecoPulsado(" + resourceName + ") num=" + num);
        String estadoAnterior = juegoBantumi.serializa();
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                Log.i(LOG_TAG, "* Juega Jugador");
                juegoBantumi.jugar(num);
                break;
            case turnoJ2:
                Log.i(LOG_TAG, "* Juega Computador");
                juegoBantumi.juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
                return;
        }
        if (!estadoAnterior.equals(juegoBantumi.serializa())) {
            bantumiVM.setPartidaModificada(true);
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        }
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        String texto = getString(
                (juegoBantumi.getSemillas(6) > 6 * numInicialSemillas)
                        ? R.string.txtGanaJugador1
                        : R.string.txtGanaJugador2
        );
        if (juegoBantumi.getSemillas(6) == 6 * numInicialSemillas) {
            texto = getString(R.string.txtEmpate);
        }

        // @TODO guardar puntuación

        // terminar
        FinalAlertDialog.newInstance(texto).show(getSupportFragmentManager(), "ALERT_DIALOG");
    }
}