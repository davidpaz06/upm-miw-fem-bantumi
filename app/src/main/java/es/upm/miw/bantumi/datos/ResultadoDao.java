package es.upm.miw.bantumi.datos;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface ResultadoDao {

    @Insert
    void insertar(Resultado resultado);
}
