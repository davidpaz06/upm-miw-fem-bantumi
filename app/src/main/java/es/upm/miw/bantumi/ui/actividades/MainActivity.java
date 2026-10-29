package es.upm.miw.bantumi.ui.actividades;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
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

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Locale;

import es.upm.miw.bantumi.datos.RepositorioPartida;
import es.upm.miw.bantumi.datos.Resultado;
import es.upm.miw.bantumi.datos.ResultadoRepository;
import es.upm.miw.bantumi.ui.fragmentos.FinalAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.RecuperarAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.ReiniciarAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.SeleccionarPartidaDialog;
import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.dominio.logica.JuegoBantumi;
import es.upm.miw.bantumi.ui.viewmodel.BantumiViewModel;

public class MainActivity extends AppCompatActivity {

    protected final String LOG_TAG = "MiW";
    private static final long RETARDO_COMPUTADOR_MS = 800;
    public JuegoBantumi juegoBantumi;
    private BantumiViewModel bantumiVM;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable jugadaComputador = this::juegaComputador;

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
        bantumiVM = new ViewModelProvider(this).get(BantumiViewModel.class);
        juegoBantumi = new JuegoBantumi(bantumiVM, obtenerTurnoInicial(), obtenerNumInicialSemillas());
        crearObservadores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.tvPlayer1)).setText(obtenerNombreJugador());
        aplicarColores();
        if (!juegoBantumi.juegoTerminado()) {
            bantumiVM.iniciarTiempo();
        }
        actualizarCronometro();
        programarJugadaComputador();
    }

    @Override
    protected void onPause() {
        // Evita mostrar diálogos (fin de partida) con la actividad en segundo plano
        handler.removeCallbacks(jugadaComputador);
        bantumiVM.pararTiempo();
        actualizarCronometro();
        super.onPause();
    }

    /**
     * Sincroniza el cronómetro mostrado con el tiempo de la partida
     */
    private void actualizarCronometro() {
        Chronometer cronometro = findViewById(R.id.cronometro);
        cronometro.setBase(SystemClock.elapsedRealtime() - bantumiVM.getTiempoMs());
        if (bantumiVM.isCronometroEnMarcha()) {
            cronometro.start();
        } else {
            cronometro.stop();
        }
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
     * @return color de resaltado del turno configurado en los ajustes
     */
    private int obtenerColorTurno() {
        return obtenerColor(R.string.prefKeyColorTurno, "#33B5E5");
    }

    /**
     * Aplica los colores de los ajustes (se llama al volver de la pantalla de ajustes)
     */
    private void aplicarColores() {
        int colorAlmacen = obtenerColor(R.string.prefKeyColorAlmacen, "#018786");
        ((TextView) findViewById(R.id.casilla_06)).setTextColor(colorAlmacen);
        ((TextView) findViewById(R.id.casilla_13)).setTextColor(colorAlmacen);
        marcarTurno(juegoBantumi.turnoActual());
        marcarCasillas();
    }

    private int obtenerColor(int idClave, String porDefecto) {
        String valor = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(idClave), porDefecto);
        try {
            return Color.parseColor(valor);
        } catch (IllegalArgumentException e) {
            return Color.parseColor(porDefecto);
        }
    }

    /**
     * @return número de semillas por casilla configurado en los ajustes
     */
    private int obtenerNumInicialSemillas() {
        int porDefecto = getResources().getInteger(R.integer.intNumInicialSemillas);
        String valor = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.prefKeyNumSemillas), null);
        try {
            return (valor == null) ? porDefecto : Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }

    /**
     * @return turno con el que empieza una partida, según los ajustes
     */
    private JuegoBantumi.Turno obtenerTurnoInicial() {
        String valor = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.prefKeyJugadorInicial), "J1");
        if ("ALEATORIO".equals(valor)) {
            return (Math.random() < 0.5) ? JuegoBantumi.Turno.turnoJ1 : JuegoBantumi.Turno.turnoJ2;
        }
        return "J2".equals(valor) ? JuegoBantumi.Turno.turnoJ2 : JuegoBantumi.Turno.turnoJ1;
    }

    /**
     * Si es el turno del computador, programa su jugada tras una breve pausa
     */
    private void programarJugadaComputador() {
        handler.removeCallbacks(jugadaComputador);
        if (juegoBantumi.turnoActual() == JuegoBantumi.Turno.turnoJ2) {
            handler.postDelayed(jugadaComputador, RETARDO_COMPUTADOR_MS);
        }
    }

    /**
     * Realiza la jugada del computador (y las que encadene si repite turno)
     */
    private void juegaComputador() {
        if (juegoBantumi.turnoActual() != JuegoBantumi.Turno.turnoJ2) {
            return;
        }
        Log.i(LOG_TAG, "* Juega Computador");
        String estadoAnterior = juegoBantumi.serializa();
        juegoBantumi.juegaComputador();
        trasJugada(estadoAnterior);
    }

    /**
     * Actualiza el estado de la partida tras una jugada: la marca como modificada,
     * comprueba si ha terminado y, si no, da paso al computador cuando le toque.
     *
     * @param estadoAnterior estado serializado antes de la jugada
     */
    private void trasJugada(String estadoAnterior) {
        if (!estadoAnterior.equals(juegoBantumi.serializa())) {
            bantumiVM.setPartidaModificada(true);
        }
        if (juegoBantumi.juegoTerminado()) {
            finJuego();
        } else {
            programarJugadaComputador();
        }
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
        bantumiVM.getUltimaCasillaJ1().observe(this, pos -> marcarCasillas());   // Casillas elegidas
        bantumiVM.getUltimaCasillaJ2().observe(this, pos -> marcarCasillas());
    }

    /**
     * Resalta con un borde la última casilla elegida por cada jugador
     */
    private void marcarCasillas() {
        int marcaJ1 = bantumiVM.getUltimaCasillaJ1().getValue();
        int marcaJ2 = bantumiVM.getUltimaCasillaJ2().getValue();
        int grosor = (int) (3 * getResources().getDisplayMetrics().density);
        ColorStateList color = ColorStateList.valueOf(obtenerColorTurno());
        for (int pos = 0; pos < JuegoBantumi.NUM_POSICIONES; pos++) {
            if (pos == 6 || pos == 13) {    // Almacenes
                continue;
            }
            View casilla = findViewById(idCasilla(pos));
            if (casilla instanceof MaterialButton) {
                MaterialButton boton = (MaterialButton) casilla;
                boton.setStrokeColor(color);
                boton.setStrokeWidth((pos == marcaJ1 || pos == marcaJ2) ? grosor : 0);
            }
        }
    }

    /**
     * @param pos posición del tablero
     * @return identificador de la vista de la posición (<code>casilla_XX</code>), o 0 si no existe
     */
    private int idCasilla(int pos) {
        String num2digitos = String.format(Locale.getDefault(), "%02d", pos);
        return getResources().getIdentifier("casilla_" + num2digitos, "id", getPackageName());
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
                tvJugador1.setBackgroundColor(obtenerColorTurno());
                tvJugador2.setTextColor(getColor(R.color.black));
                tvJugador2.setBackgroundColor(getColor(R.color.white));
                break;
            case turnoJ2:
                tvJugador1.setTextColor(getColor(R.color.black));
                tvJugador1.setBackgroundColor(getColor(R.color.white));
                tvJugador2.setTextColor(getColor(R.color.white));
                tvJugador2.setBackgroundColor(obtenerColorTurno());
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
        // Los identificadores de los huecos tienen el formato casilla_XX
        int idBoton = idCasilla(pos);
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
            mostrarPartidasGuardadas();
        } else if (itemId == R.id.opcMejoresResultados) {
            startActivity(new Intent(this, MejoresResultadosActivity.class));
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
     * Inicia una nueva partida con el número de semillas y el jugador inicial
     * de los ajustes, y la marca como no modificada
     */
    public void reiniciarPartida() {
        juegoBantumi.setNumInicialSemillas(obtenerNumInicialSemillas());
        juegoBantumi.inicializar(obtenerTurnoInicial());
        bantumiVM.setPartidaModificada(false);
        bantumiVM.setPuntuacionGuardada(false);
        bantumiVM.reiniciarTiempo(0);
        bantumiVM.iniciarTiempo();
        actualizarCronometro();
        programarJugadaComputador();
    }

    /**
     * Guarda el estado actual del tablero en el fichero de partida
     */
    private void guardarPartida() {
        int mensaje;
        try {
            new RepositorioPartida(this).guardar(juegoBantumi.serializa() + "\n" + bantumiVM.getTiempoMs());
            bantumiVM.setPartidaModificada(false);
            mensaje = R.string.txtPartidaGuardada;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error al guardar la partida", e);
            mensaje = R.string.txtErrorGuardarPartida;
        }
        Snackbar.make(findViewById(android.R.id.content), getString(mensaje), Snackbar.LENGTH_LONG).show();
    }

    /**
     * Muestra la lista de partidas guardadas para elegir cuál recuperar
     */
    private void mostrarPartidasGuardadas() {
        if (!new RepositorioPartida(this).existe()) {
            mostrarMensaje(R.string.txtNoHayPartidaGuardada);
        } else {
            new SeleccionarPartidaDialog().show(getSupportFragmentManager(), "SELECCIONAR_DIALOG");
        }
    }

    /**
     * Recupera la partida elegida. Si la partida actual se ha modificado,
     * pide confirmación antes de sustituirla.
     *
     * @param nombreFichero fichero de la partida elegida
     */
    public void seleccionarPartida(String nombreFichero) {
        if (bantumiVM.isPartidaModificada()) {
            RecuperarAlertDialog.newInstance(nombreFichero).show(getSupportFragmentManager(), "RECUPERAR_DIALOG");
        } else {
            recuperarPartida(nombreFichero);
        }
    }

    /**
     * Sustituye la partida actual por la guardada en el fichero
     *
     * @param nombreFichero fichero de la partida a recuperar
     */
    public void recuperarPartida(String nombreFichero) {
        int mensaje;
        try {
            String guardada = new RepositorioPartida(this).cargar(nombreFichero);
            if (guardada == null) {
                mensaje = R.string.txtNoHayPartidaGuardada;
            } else {
                // Primera línea: estado del tablero; segunda (opcional): tiempo de partida
                String[] lineas = guardada.split("\n");
                juegoBantumi.deserializa(lineas[0]);
                long tiempoMs = 0;
                if (lineas.length > 1) {
                    try {
                        tiempoMs = Long.parseLong(lineas[1].trim());
                    } catch (NumberFormatException e) {
                        Log.w(LOG_TAG, "Tiempo de partida inválido: " + lineas[1]);
                    }
                }
                bantumiVM.reiniciarTiempo(tiempoMs);
                if (!juegoBantumi.juegoTerminado()) {
                    bantumiVM.iniciarTiempo();
                }
                actualizarCronometro();
                bantumiVM.setPartidaModificada(false);
                // Una partida ya terminada se registró cuando acabó
                bantumiVM.setPuntuacionGuardada(juegoBantumi.juegoTerminado());
                programarJugadaComputador();
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
        switch (juegoBantumi.turnoActual()) {
            case turnoJ1:
                Log.i(LOG_TAG, "* Juega Jugador");
                String estadoAnterior = juegoBantumi.serializa();
                juegoBantumi.jugar(num);
                trasJugada(estadoAnterior);
                break;
            case turnoJ2:
                // Turno del computador: adelanta su jugada, que ya estaba programada
                juegaComputador();
                break;
            default:    // JUEGO TERMINADO
                finJuego();
        }
    }

    /**
     * Guarda en la base de datos el resultado de la partida terminada (una sola vez por partida)
     */
    private void guardarPuntuacion() {
        if (bantumiVM.isPuntuacionGuardada()) {
            return;
        }
        new ResultadoRepository(this).insertar(new Resultado(
                obtenerNombreJugador(),
                System.currentTimeMillis(),
                juegoBantumi.getSemillas(6),
                juegoBantumi.getSemillas(13),
                bantumiVM.getTiempoMs()
        ));
        bantumiVM.setPuntuacionGuardada(true);
    }

    /**
     * El juego ha terminado. Volver a jugar?
     */
    private void finJuego() {
        bantumiVM.pararTiempo();
        actualizarCronometro();
        int semillasJ1 = juegoBantumi.getSemillas(6);
        int semillasJ2 = juegoBantumi.getSemillas(13);
        String texto = getString(
                (semillasJ1 > semillasJ2)
                        ? R.string.txtGanaJugador1
                        : R.string.txtGanaJugador2
        );
        if (semillasJ1 == semillasJ2) {
            texto = getString(R.string.txtEmpate);
        }

        guardarPuntuacion();

        // terminar
        FinalAlertDialog.newInstance(texto).show(getSupportFragmentManager(), "ALERT_DIALOG");
    }
}