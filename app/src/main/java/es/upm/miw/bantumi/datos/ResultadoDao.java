package es.upm.miw.bantumi.datos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultadoDao {

    @Insert
    void insertar(Resultado resultado);

    /**
     * @param nombre nombre (o parte) del jugador, sin distinguir mayúsculas; vacío para todos
     * @param minSemillas mínimo de semillas obtenido por cualquiera de los dos jugadores
     * @return los diez mejores resultados que cumplen el filtro, ordenados por el mayor
     * número de semillas obtenido por cualquiera de los dos jugadores
     */
    @Query("SELECT * FROM resultados"
            + " WHERE instr(lower(nombreJugador), lower(:nombre)) > 0"
            + " AND MAX(semillasJ1, semillasJ2) >= :minSemillas"
            + " ORDER BY MAX(semillasJ1, semillasJ2) DESC, fecha DESC LIMIT 10")
    LiveData<List<Resultado>> mejoresResultados(String nombre, int minSemillas);

    @Query("DELETE FROM resultados")
    void borrarTodos();
}
