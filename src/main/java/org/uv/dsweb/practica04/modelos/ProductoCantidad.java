package org.uv.dsweb.practica04.modelos;

import java.util.Objects;

public class ProductoCantidad {

    private Producto producto;
    private int cantidad;
    private double valor;

    public ProductoCantidad() {
    }

    public ProductoCantidad(Producto producto, int cantidad, double valor) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.valor = valor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ProductoCantidad that = (ProductoCantidad) obj;
        return cantidad == that.cantidad && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(producto, cantidad);
    }
}
