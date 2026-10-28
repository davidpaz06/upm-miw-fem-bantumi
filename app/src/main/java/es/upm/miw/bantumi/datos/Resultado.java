package es.upm.miw.bantumi.datos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Resultado de una partida finalizada
 */
@Entity(tableName = "resultados")
public class Resultado {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String nombreJugador;

    /** Fecha y hora de la partida (milisegundos desde epoch) */
    public long fecha;

    /** Semillas en el almacén del jugador 1 */
    public int semillasJ1;

    /** Semillas en el almacén del jugador 2 (computador) */
    public int semillasJ2;

    public Resultado(String nombreJugador, long fecha, int semillasJ1, int semillasJ2) {
        this.nombreJugador = nombreJugador;
        this.fecha = fecha;
        this.semillasJ1 = semillasJ1;
        this.semillasJ2 = semillasJ2;
    }
}
