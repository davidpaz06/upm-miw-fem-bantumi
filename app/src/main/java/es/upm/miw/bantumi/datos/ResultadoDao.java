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

    @Query("SELECT * FROM resultados"
            + " WHERE instr(lower(nombreJugador), lower(:nombre)) > 0"
            + " AND MAX(semillasJ1, semillasJ2) >= :minSemillas"
            + " ORDER BY MAX(semillasJ1, semillasJ2) DESC, fecha DESC LIMIT 10")
    LiveData<List<Resultado>> mejoresResultados(String nombre, int minSemillas);

    @Query("DELETE FROM resultados")
    void borrarTodos();
}
