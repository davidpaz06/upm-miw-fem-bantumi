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

/**
 * Persiste una única partida en un fichero del almacenamiento interno de la app
 */
public class RepositorioPartida {

    static final String NOMBRE_FICHERO = "partida.txt";

    private final Context context;

    public RepositorioPartida(@NonNull Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * @return <code>true</code> si hay una partida guardada
     */
    public boolean existe() {
        return new File(context.getFilesDir(), NOMBRE_FICHERO).isFile();
    }

    /**
     * Guarda la partida, sustituyendo la anterior
     *
     * @param juegoSerializado estado del juego (ver <code>JuegoBantumi.serializa()</code>)
     * @throws IOException si no se puede escribir el fichero
     */
    public void guardar(@NonNull String juegoSerializado) throws IOException {
        try (Writer writer = new OutputStreamWriter(
                context.openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE),
                StandardCharsets.UTF_8)) {
            writer.write(juegoSerializado);
        }
    }

    /**
     * @return estado de la partida guardada, o <code>null</code> si no existe
     * @throws IOException si no se puede leer el fichero
     */
    @Nullable
    public String cargar() throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                context.openFileInput(NOMBRE_FICHERO),
                StandardCharsets.UTF_8))) {
            return reader.readLine();
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}
