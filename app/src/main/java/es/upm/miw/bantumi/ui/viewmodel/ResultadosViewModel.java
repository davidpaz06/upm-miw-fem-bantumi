package es.upm.miw.bantumi.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.List;

import es.upm.miw.bantumi.datos.Resultado;
import es.upm.miw.bantumi.datos.ResultadoRepository;

public class ResultadosViewModel extends AndroidViewModel {

    private final ResultadoRepository repositorio;
    private final MediatorLiveData<List<Resultado>> mejoresResultados = new MediatorLiveData<>();
    private LiveData<List<Resultado>> fuente;

    private String filtroNombre = "";
    private int filtroMinSemillas = 0;

    public ResultadosViewModel(@NonNull Application application) {
        super(application);
        repositorio = new ResultadoRepository(application);
        aplicarFiltro(filtroNombre, filtroMinSemillas);
    }

    /**
     * @return los diez mejores resultados que cumplen el filtro, actualizados automáticamente
     */
    public LiveData<List<Resultado>> getMejoresResultados() {
        return mejoresResultados;
    }

    /**
     * Filtra los mejores resultados
     *
     * @param nombre nombre (o parte del nombre) del jugador; vacío para no filtrar por jugador
     * @param minSemillas mínimo de semillas en el almacén ganador; 0 para no filtrar por semillas
     */
    public void aplicarFiltro(@NonNull String nombre, int minSemillas) {
        filtroNombre = nombre.trim();
        filtroMinSemillas = Math.max(minSemillas, 0);
        if (fuente != null) {
            mejoresResultados.removeSource(fuente);
        }
        fuente = repositorio.getMejoresResultados(filtroNombre, filtroMinSemillas);
        mejoresResultados.addSource(fuente, mejoresResultados::setValue);
    }

    public String getFiltroNombre() {
        return filtroNombre;
    }

    public int getFiltroMinSemillas() {
        return filtroMinSemillas;
    }

    public boolean hayFiltro() {
        return !filtroNombre.isEmpty() || filtroMinSemillas > 0;
    }

    /**
     * Elimina todos los resultados guardados
     */
    public void borrarTodos() {
        repositorio.borrarTodos();
    }
}
