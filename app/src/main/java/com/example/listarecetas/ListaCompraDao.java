package com.example.listarecetas;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ListaCompraDao {
    @Insert
    void insertar(ListaCompra lista);

    @Query("SELECT * FROM ListaCompra ORDER BY fechaCreacion DESC")
    List<ListaCompra> obtenerTodas();

    @Query("DELETE FROM ListaCompra WHERE id = :id")
    void eliminar(int id);
}
