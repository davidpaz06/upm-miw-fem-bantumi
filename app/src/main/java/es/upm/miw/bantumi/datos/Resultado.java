package es.upm.miw.bantumi.datos;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "resultados")
public class Resultado {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String nombreJugador;

    public long fecha;

    public int semillasJ1;

    public int semillasJ2;

    public long duracionMs;

    public Resultado(String nombreJugador, long fecha, int semillasJ1, int semillasJ2, long duracionMs) {
        this.nombreJugador = nombreJugador;
        this.fecha = fecha;
        this.semillasJ1 = semillasJ1;
        this.semillasJ2 = semillasJ2;
        this.duracionMs = duracionMs;
    }
}
