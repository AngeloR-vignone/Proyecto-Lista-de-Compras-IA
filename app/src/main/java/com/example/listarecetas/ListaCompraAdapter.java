package com.example.listarecetas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListaCompraAdapter extends RecyclerView.Adapter<ListaCompraAdapter.VistaLista> {
    private final List<ListaCompra> listas;
    private final HistorialListaActivity actividad;

    public ListaCompraAdapter(List<ListaCompra> listas, HistorialListaActivity actividad) {
        this.listas = listas;
        this.actividad = actividad;
    }

    public static class VistaLista extends RecyclerView.ViewHolder {
        TextView textoFecha;
        ImageButton botonReutilizar, botonEliminar;

        public VistaLista(View itemView) {
            super(itemView);
            textoFecha = itemView.findViewById(R.id.textoFechaLista);
            botonReutilizar = itemView.findViewById(R.id.botonReutilizarLista);
            botonEliminar = itemView.findViewById(R.id.botonEliminarLista);
        }
    }

    @Override
    public VistaLista onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_compra, parent, false);
        return new VistaLista(vista);
    }

    @Override
    public void onBindViewHolder(VistaLista holder, int posicion) {
        ListaCompra lista = listas.get(posicion);
        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date(lista.fechaCreacion));
        holder.textoFecha.setText("Lista guardada el: " + fecha);

        holder.botonEliminar.setOnClickListener(v -> {
            RecetaDatabase.obtenerInstancia(actividad).listaCompraDao().eliminar(lista.id);
            listas.remove(posicion);
            notifyItemRemoved(posicion);
            actividad.mostrarMensaje("Lista eliminada");
        });

        holder.botonReutilizar.setOnClickListener(v -> {
            actividad.reutilizarLista(lista.contenidoJson);
        });
    }

    @Override
    public int getItemCount() {
        return listas.size();
    }
}
