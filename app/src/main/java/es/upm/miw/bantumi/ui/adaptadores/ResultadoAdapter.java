package es.upm.miw.bantumi.ui.adaptadores;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;

import es.upm.miw.bantumi.R;
import es.upm.miw.bantumi.datos.Resultado;

/**
 * Muestra el listado de resultados
 */
public class ResultadoAdapter extends ListAdapter<Resultado, ResultadoAdapter.ResultadoViewHolder> {

    private static final DiffUtil.ItemCallback<Resultado> DIFF = new DiffUtil.ItemCallback<Resultado>() {
        @Override
        public boolean areItemsTheSame(@NonNull Resultado a, @NonNull Resultado b) {
            return a.id == b.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Resultado a, @NonNull Resultado b) {
            return a.semillasJ1 == b.semillasJ1
                    && a.semillasJ2 == b.semillasJ2
                    && a.fecha == b.fecha
                    && a.duracionMs == b.duracionMs
                    && a.nombreJugador.equals(b.nombreJugador);
        }
    };

    private final DateFormat formatoFecha = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);

    public ResultadoAdapter() {
        super(DIFF);
    }

    @NonNull
    @Override
    public ResultadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_resultado, parent, false);
        return new ResultadoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultadoViewHolder holder, int position) {
        Resultado resultado = getItem(position);
        holder.tvPosicion.setText(String.valueOf(position + 1));
        holder.tvNombre.setText(resultado.nombreJugador);
        holder.tvFecha.setText(holder.itemView.getContext().getString(
                R.string.txtFechaDuracion,
                formatoFecha.format(new Date(resultado.fecha)),
                DateUtils.formatElapsedTime(resultado.duracionMs / 1000)));
        holder.tvSemillas.setText(holder.itemView.getContext().getString(
                R.string.txtResultadoSemillas, resultado.semillasJ1, resultado.semillasJ2));
    }

    static class ResultadoViewHolder extends RecyclerView.ViewHolder {
        final TextView tvPosicion;
        final TextView tvNombre;
        final TextView tvFecha;
        final TextView tvSemillas;

        ResultadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPosicion = itemView.findViewById(R.id.tvPosicion);
            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvSemillas = itemView.findViewById(R.id.tvSemillas);
        }
    }
}
