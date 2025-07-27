package com.example.listarecetas;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AdaptadorProducto extends RecyclerView.Adapter<AdaptadorProducto.VistaProducto> {

    private final List<Producto> listaProductos;
    private final OnListaActualizadaListener listener;

    public AdaptadorProducto(List<Producto> listaProductos, OnListaActualizadaListener listener) {
        this.listaProductos = listaProductos;
        this.listener = listener;
    }


    public static class VistaProducto extends RecyclerView.ViewHolder {
        TextView textoNombre, textoPrecio, btnSumar, btnRestar, btnEliminar;

        public VistaProducto(View itemView) {
            super(itemView);
            textoNombre = itemView.findViewById(R.id.nombreProducto);
            textoPrecio = itemView.findViewById(R.id.precioProducto);
            btnSumar = itemView.findViewById(R.id.btnSumar);
            btnRestar = itemView.findViewById(R.id.btnRestar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }


    @Override
    public VistaProducto onCreateViewHolder(ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new VistaProducto(vista);
    }

    @Override
    public void onBindViewHolder(VistaProducto holder, int posicion) {
        Producto producto = listaProductos.get(posicion);
        holder.textoNombre.setText(producto.getNombre() + " x" + producto.getCantidad());
        holder.textoPrecio.setText(String.format("$%.2f", producto.getPrecioUsd() * producto.getCantidad()));


        holder.btnSumar.setOnClickListener(v -> {
            producto.setCantidad(producto.getCantidad() + 1);
            notifyItemChanged(posicion);
            listener.onListaActualizada();
        });

        holder.btnRestar.setOnClickListener(v -> {
            if (producto.getCantidad() > 1) {
                producto.setCantidad(producto.getCantidad() - 1);
                notifyItemChanged(posicion);
                listener.onListaActualizada();
            }
        });

        holder.btnEliminar.setOnClickListener(v -> {
            listaProductos.remove(posicion);
            notifyItemRemoved(posicion);
            notifyItemRangeChanged(posicion, listaProductos.size());
            listener.onListaActualizada();
        });
    }


    @Override
    public int getItemCount() {
        return listaProductos.size();
    }
}

