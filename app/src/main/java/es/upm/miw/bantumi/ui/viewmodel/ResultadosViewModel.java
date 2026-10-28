package es.upm.miw.bantumi.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import es.upm.miw.bantumi.datos.Resultado;
import es.upm.miw.bantumi.datos.ResultadoRepository;

public class ResultadosViewModel extends AndroidViewModel {

    private final LiveData<List<Resultado>> mejoresResultados;

    public ResultadosViewModel(@NonNull Application application) {
        super(application);
        mejoresResultados = new ResultadoRepository(application).getMejoresResultados();
    }

    /**
     * @return los diez mejores resultados, actualizados automáticamente
     */
    public LiveData<List<Resultado>> getMejoresResultados() {
        return mejoresResultados;
    }
}
