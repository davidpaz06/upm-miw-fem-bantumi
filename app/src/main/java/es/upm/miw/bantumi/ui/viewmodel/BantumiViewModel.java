package es.upm.miw.bantumi.ui.viewmodel;

import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import es.upm.miw.bantumi.dominio.logica.JuegoBantumi;

public class BantumiViewModel extends ViewModel {

    private ArrayList<MutableLiveData<Integer>> tablero;

    private MutableLiveData<JuegoBantumi.Turno> turno;

    // Última casilla elegida por cada jugador (-1 si aún no ha jugado)
    private final MutableLiveData<Integer> ultimaCasillaJ1 = new MutableLiveData<>(-1);
    private final MutableLiveData<Integer> ultimaCasillaJ2 = new MutableLiveData<>(-1);

    // Tiempo de partida: acumulado en tramos ya cerrados + tramo en marcha (0 si está parado)
    private long tiempoAcumuladoMs = 0;
    private long inicioTramoMs = 0;

    // Indica si la partida actual se ha modificado desde que se inició, guardó o recuperó
    private boolean partidaModificada = false;

    // Indica si el resultado de la partida terminada ya se ha guardado (evita duplicados)
    private boolean puntuacionGuardada = false;

    public BantumiViewModel() {
        turno = new MutableLiveData<>(JuegoBantumi.Turno.turnoJ1);
        tablero = new ArrayList<>(JuegoBantumi.NUM_POSICIONES);
        for (int i = 0; i < JuegoBantumi.NUM_POSICIONES; i++) {
            tablero.add(i, new MutableLiveData<>(0));
        }
    }

    /**
     * @return Devuelve el turno actual
     */
    public LiveData<JuegoBantumi.Turno> getTurno() {
        return turno;
    }

    /**
     * Establece el valor para turno
     * @param turno valor
     */
    public void setTurno(JuegoBantumi.Turno turno) {
        this.turno.setValue(turno);
    }

    /**
     * @return tiempo de la partida en milisegundos
     */
    public long getTiempoMs() {
        return tiempoAcumuladoMs
                + ((inicioTramoMs != 0) ? SystemClock.elapsedRealtime() - inicioTramoMs : 0);
    }

    public boolean isCronometroEnMarcha() {
        return inicioTramoMs != 0;
    }

    public void iniciarTiempo() {
        if (inicioTramoMs == 0) {
            inicioTramoMs = SystemClock.elapsedRealtime();
        }
    }

    public void pararTiempo() {
        tiempoAcumuladoMs = getTiempoMs();
        inicioTramoMs = 0;
    }

    /**
     * Para el cronómetro y fija el tiempo de la partida
     *
     * @param tiempoMs tiempo en milisegundos
     */
    public void reiniciarTiempo(long tiempoMs) {
        tiempoAcumuladoMs = tiempoMs;
        inicioTramoMs = 0;
    }

    public LiveData<Integer> getUltimaCasillaJ1() {
        return ultimaCasillaJ1;
    }

    public LiveData<Integer> getUltimaCasillaJ2() {
        return ultimaCasillaJ2;
    }

    /**
     * Registra la casilla elegida por el jugador al que corresponde <i>turno</i>
     *
     * @param turno turno (jugador) que ha elegido la casilla
     * @param pos casilla elegida
     */
    public void setUltimaCasilla(JuegoBantumi.Turno turno, int pos) {
        if (turno == JuegoBantumi.Turno.turnoJ1) {
            ultimaCasillaJ1.setValue(pos);
        } else if (turno == JuegoBantumi.Turno.turnoJ2) {
            ultimaCasillaJ2.setValue(pos);
        }
    }

    /**
     * Olvida las casillas elegidas (nueva partida o partida recuperada)
     */
    public void reiniciarUltimasCasillas() {
        ultimaCasillaJ1.setValue(-1);
        ultimaCasillaJ2.setValue(-1);
    }

    /**
     * @return <code>true</code> si la partida se ha modificado desde que se inició, guardó o recuperó
     */
    public boolean isPartidaModificada() {
        return partidaModificada;
    }

    public void setPartidaModificada(boolean partidaModificada) {
        this.partidaModificada = partidaModificada;
    }

    public boolean isPuntuacionGuardada() {
        return puntuacionGuardada;
    }

    public void setPuntuacionGuardada(boolean puntuacionGuardada) {
        this.puntuacionGuardada = puntuacionGuardada;
    }

    /**
     * Recupera el valor de una determinada posición
     *
     * @param pos posición
     * @return contenido de la posición <i>pos</i>
     */
    @NonNull
    public LiveData<Integer> getNumSemillas(int pos) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POSICIONES) {
            throw new ArrayIndexOutOfBoundsException();
        }

        return tablero.get(pos);
    }

    /**
     * Asigna el valor v a la posición pos del tablero
     *
     * @param pos índice
     * @param v valor
     */
    public void setNumSemillas(int pos, int v) {
        if (pos < 0 || pos >= JuegoBantumi.NUM_POSICIONES) {
            throw new ArrayIndexOutOfBoundsException();
        }
        tablero.get(pos).setValue(v);
    }
}