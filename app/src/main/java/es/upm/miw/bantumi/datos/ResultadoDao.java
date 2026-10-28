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
     * @return los diez mejores resultados, ordenados por el mayor número de
     * semillas obtenido por cualquiera de los dos jugadores
     */
    @Query("SELECT * FROM resultados ORDER BY MAX(semillasJ1, semillasJ2) DESC, fecha DESC LIMIT 10")
    LiveData<List<Resultado>> mejoresResultados();

    @Query("DELETE FROM resultados")
    void borrarTodos();
}
