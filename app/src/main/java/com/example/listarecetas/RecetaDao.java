
package com.example.listarecetas;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecetaDao {
    @Insert
    void insertar(Receta receta);

    @Query("SELECT * FROM Receta ORDER BY fechaCreacion DESC")
    List<Receta> obtenerTodas();

    @Query("SELECT * FROM Receta WHERE ingredientes LIKE '%' || :busqueda || '%' OR textoReceta LIKE '%' || :busqueda || '%'")
    List<Receta> buscarPorTexto(String busqueda);

    @Query("DELETE FROM Receta WHERE id = :id")
    void eliminar(int id);

    @Query("UPDATE Receta SET favorito = :esFavorito WHERE id = :id")
    void actualizarFavorito(int id, boolean esFavorito);
}
