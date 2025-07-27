package com.example.listarecetas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Receta {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String textoReceta;
    public String ingredientes;
    public long fechaCreacion;
    public boolean favorito;
}

