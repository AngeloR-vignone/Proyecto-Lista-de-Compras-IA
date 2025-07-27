package com.example.listarecetas;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.listarecetas.ListaCompra;
import com.example.listarecetas.ListaCompraDao;

@Database(entities = {Receta.class, ListaCompra.class}, version = 2)
public abstract class RecetaDatabase extends RoomDatabase {



    public abstract RecetaDao recetaDao();
    public abstract ListaCompraDao listaCompraDao();


    private static volatile RecetaDatabase instancia;

    public static RecetaDatabase obtenerInstancia(Context context) {
        if (instancia == null) {
            instancia = Room.databaseBuilder(
                            context.getApplicationContext(),
                            RecetaDatabase.class,
                            "bd_recetas"
                    ).fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instancia;
    }
}
