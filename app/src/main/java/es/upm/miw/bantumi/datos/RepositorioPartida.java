package es.upm.miw.bantumi.datos;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RepositorioPartida {

    private static final String PREFIJO = "partida_";
    private static final String SUFIJO = ".txt";

    public static class PartidaGuardada {
        public final String nombreFichero;
        public final long fecha;
        public final int semillasJ1;
        public final int semillasJ2;

        PartidaGuardada(String nombreFichero, long fecha, int semillasJ1, int semillasJ2) {
            this.nombreFichero = nombreFichero;
            this.fecha = fecha;
            this.semillasJ1 = semillasJ1;
            this.semillasJ2 = semillasJ2;
        }
    }

    private final Context context;

    public RepositorioPartida(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean existe() {
        return !nombresFicheros().isEmpty();
    }

    public void guardar(@NonNull String juegoSerializado) throws IOException {
        String nombre = PREFIJO + System.currentTimeMillis() + SUFIJO;
        try (Writer writer = new OutputStreamWriter(
                context.openFileOutput(nombre, Context.MODE_PRIVATE),
                StandardCharsets.UTF_8)) {
            writer.write(juegoSerializado);
        }
    }

    @NonNull
    public List<PartidaGuardada> listar() {
        List<PartidaGuardada> partidas = new ArrayList<>();
        for (String nombre : nombresFicheros()) {
            long fecha = Long.parseLong(nombre.substring(PREFIJO.length(), nombre.length() - SUFIJO.length()));
            int semillasJ1 = -1;
            int semillasJ2 = -1;
            try {
                String contenido = cargar(nombre);
                String[] valores = contenido.split("\n")[0].split(";")[1].split(",");
                semillasJ1 = Integer.parseInt(valores[6]);
                semillasJ2 = Integer.parseInt(valores[13]);
            } catch (IOException | RuntimeException e) {
            }
            partidas.add(new PartidaGuardada(nombre, fecha, semillasJ1, semillasJ2));
        }
        Collections.sort(partidas, (a, b) -> Long.compare(b.fecha, a.fecha));
        return partidas;
    }

    @Nullable
    public String cargar(@NonNull String nombreFichero) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.openFileInput(nombreFichero),
                StandardCharsets.UTF_8))) {
            StringBuilder contenido = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (contenido.length() > 0) {
                    contenido.append("\n");
                }
                contenido.append(linea);
            }
            return contenido.toString();
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private List<String> nombresFicheros() {
        List<String> nombres = new ArrayList<>();
        String[] ficheros = context.getFilesDir().list();
        if (ficheros != null) {
            for (String fichero : ficheros) {
                if (esFicheroPartida(fichero)) {
                    nombres.add(fichero);
                }
            }
        }
        return nombres;
    }

    private static boolean esFicheroPartida(String nombre) {
        return nombre.matches(PREFIJO + "\\d+" + "\\.txt");
    }
}
