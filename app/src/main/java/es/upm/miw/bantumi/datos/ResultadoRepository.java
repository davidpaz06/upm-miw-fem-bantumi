package es.upm.miw.bantumi.datos;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Acceso a los resultados guardados. Las operaciones de escritura se
 * ejecutan en un hilo secundario para no bloquear la interfaz.
 */
public class ResultadoRepository {

    private static final ExecutorService EJECUTOR = Executors.newSingleThreadExecutor();

    private final ResultadoDao resultadoDao;

    public ResultadoRepository(@NonNull Context context) {
        this.resultadoDao = BantumiRoomDatabase.getInstance(context).resultadoDao();
    }

    public void insertar(@NonNull Resultado resultado) {
        EJECUTOR.execute(() -> resultadoDao.insertar(resultado));
    }

    public void borrarTodos() {
        EJECUTOR.execute(resultadoDao::borrarTodos);
    }

    public LiveData<List<Resultado>> getMejoresResultados() {
        return resultadoDao.mejoresResultados();
    }
}
