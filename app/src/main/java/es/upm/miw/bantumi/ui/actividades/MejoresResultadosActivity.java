package es.upm.miw.bantumi.ui.actividades;

import android.os.Bundle;
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
import es.upm.miw.bantumi.ui.viewmodel.ResultadosViewModel;

/**
 * Muestra los diez mejores resultados
 */
public class MejoresResultadosActivity extends AppCompatActivity {

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
        ResultadosViewModel resultadosVM = new ViewModelProvider(this).get(ResultadosViewModel.class);
        resultadosVM.getMejoresResultados().observe(this, resultados -> {
            adapter.submitList(resultados);
            tvSinResultados.setVisibility(resultados.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
