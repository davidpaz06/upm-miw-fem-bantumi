package es.upm.miw.bantumi.datos;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Resultado.class}, version = 1, exportSchema = false)
public abstract class BantumiRoomDatabase extends RoomDatabase {

    private static final String NOMBRE_BD = "bantumi.db";
    private static volatile BantumiRoomDatabase instancia;

    public abstract ResultadoDao resultadoDao();

    public static BantumiRoomDatabase getInstance(Context context) {
        if (instancia == null) {
            synchronized (BantumiRoomDatabase.class) {
                if (instancia == null) {
                    instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            BantumiRoomDatabase.class,
                            NOMBRE_BD
                    ).build();
                }
            }
        }
        return instancia;
    }
}
