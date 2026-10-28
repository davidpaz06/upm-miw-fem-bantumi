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
import es.upm.miw.bantumi.ui.viewmodel.ResultadosViewModel;

/**
 * Muestra los diez mejores resultados
 */
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

    /**
     * Elimina todos los resultados (la lista se actualiza sola al observar la base de datos)
     */
    public void borrarResultados() {
        resultadosVM.borrarTodos();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.opcBorrarResultados) {
            new BorrarResultadosAlertDialog().show(getSupportFragmentManager(), "BORRAR_DIALOG");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
