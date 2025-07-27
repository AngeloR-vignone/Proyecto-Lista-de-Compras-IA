package com.example.listarecetas;

public class Producto {
    private final String nombre;
    private int cantidad;
    private final double precioUsd;

    public Producto(String nombre, int cantidad, double precioUsd) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUsd = precioUsd;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioUsd() {
        return precioUsd;
    }

    public double getPrecioTotal() {
        return precioUsd * cantidad;
    }
}
