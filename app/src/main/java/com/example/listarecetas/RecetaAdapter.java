package com.example.listarecetas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.VistaReceta> {
    private final List<Receta> recetas;
    private final Context contexto;

    public RecetaAdapter(List<Receta> recetas, Context contexto) {
        this.recetas = recetas;
        this.contexto = contexto;
    }

    public static class VistaReceta extends RecyclerView.ViewHolder {
        TextView texto;
        ImageButton botonEliminar, botonFavorito;

        public VistaReceta(View itemView) {
            super(itemView);
            texto = itemView.findViewById(R.id.textoReceta);
            botonEliminar = itemView.findViewById(R.id.botonEliminar);
            botonFavorito = itemView.findViewById(R.id.botonFavorito);
        }
    }

    @Override
    public VistaReceta onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);
        return new VistaReceta(vista);
    }

    @Override
    public void onBindViewHolder(VistaReceta holder, int posicion) {
        Receta receta = recetas.get(posicion);
        holder.texto.setText(receta.textoReceta);

        holder.botonEliminar.setOnClickListener(v -> {
            RecetaDatabase.obtenerInstancia(contexto).recetaDao().eliminar(receta.id);
            recetas.remove(posicion);
            notifyItemRemoved(posicion);
        });

        holder.botonFavorito.setImageResource(
                receta.favorito ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off
        );
        holder.botonFavorito.setOnClickListener(v -> {
            receta.favorito = !receta.favorito;
            RecetaDatabase.obtenerInstancia(contexto).recetaDao().actualizarFavorito(receta.id, receta.favorito);
            notifyItemChanged(posicion);
        });
    }

    @Override
    public int getItemCount() {
        return recetas.size();
    }
}
