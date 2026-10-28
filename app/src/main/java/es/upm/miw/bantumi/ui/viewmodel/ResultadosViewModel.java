package es.upm.miw.bantumi.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.upm.miw.bantumi.datos.Resultado;
import es.upm.miw.bantumi.datos.ResultadoRepository;

public class ResultadosViewModel extends AndroidViewModel {

    private final ResultadoRepository repositorio;
    private final LiveData<List<Resultado>> mejoresResultados;

    public ResultadosViewModel(@NonNull Application application) {
        super(application);
        repositorio = new ResultadoRepository(application);
        mejoresResultados = repositorio.getMejoresResultados();
    }

    /**
     * Elimina todos los resultados guardados
     */
    public void borrarTodos() {
        repositorio.borrarTodos();
    }

    /**
     * @return los diez mejores resultados, actualizados automáticamente
     */
    public LiveData<List<Resultado>> getMejoresResultados() {
        return mejoresResultados;
    }
}
