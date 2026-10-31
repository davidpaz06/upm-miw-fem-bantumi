package es.upm.miw.bantumi.ui.actividades;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.ui.adaptadores.ResultadoAdapter;
import es.upm.miw.bantumi.ui.fragmentos.BorrarResultadosAlertDialog;
import es.upm.miw.bantumi.ui.fragmentos.FiltroResultadosDialog;
import es.upm.miw.bantumi.ui.viewmodel.ResultadosViewModel;

public class MejoresResultadosActivity extends AppCompatActivity {

    private ResultadosViewModel resultadosVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mejores_resultados);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.txtOpcionMejoresResultados);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ResultadoAdapter adapter = new ResultadoAdapter();
        RecyclerView rvResultados = findViewById(R.id.rvResultados);
        rvResultados.setLayoutManager(new LinearLayoutManager(this));
        rvResultados.setAdapter(adapter);

        View tvSinResultados = findViewById(R.id.tvSinResultados);
        resultadosVM = new ViewModelProvider(this).get(ResultadosViewModel.class);
        actualizarSubtitulo();
        resultadosVM.getMejoresResultados().observe(this, resultados -> {
            adapter.submitList(resultados);
            tvSinResultados.setVisibility(resultados.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_resultados, menu);
        return true;
    }

    public String getFiltroNombre() {
        return resultadosVM.getFiltroNombre();
    }

    public int getFiltroMinSemillas() {
        return resultadosVM.getFiltroMinSemillas();
    }

    public void aplicarFiltro(String nombre, String minSemillas) {
        int minimo = 0;
        try {
            minimo = Integer.parseInt(minSemillas.trim());
        } catch (NumberFormatException e) {
        }
        resultadosVM.aplicarFiltro(nombre, minimo);
        actualizarSubtitulo();
    }

    private void actualizarSubtitulo() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        if (!resultadosVM.hayFiltro()) {
            actionBar.setSubtitle(null);
            return;
        }
        StringBuilder filtro = new StringBuilder();
        if (!resultadosVM.getFiltroNombre().isEmpty()) {
            filtro.append(resultadosVM.getFiltroNombre());
        }
        if (resultadosVM.getFiltroMinSemillas() > 0) {
            if (filtro.length() > 0) {
                filtro.append(", ");
            }
            filtro.append("≥ ").append(resultadosVM.getFiltroMinSemillas());
        }
        actionBar.setSubtitle(getString(R.string.txtFiltroActivo, filtro));
    }

    public void borrarResultados() {
        resultadosVM.borrarTodos();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.opcFiltrarResultados) {
            new FiltroResultadosDialog().show(getSupportFragmentManager(), "FILTRO_DIALOG");
            return true;
        } else if (item.getItemId() == R.id.opcBorrarResultados) {
            new BorrarResultadosAlertDialog().show(getSupportFragmentManager(), "BORRAR_DIALOG");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
