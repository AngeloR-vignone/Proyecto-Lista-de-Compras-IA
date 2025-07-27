package com.example.listarecetas;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ListaCompra {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String contenidoJson;
    public long fechaCreacion;
}
