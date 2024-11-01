package org.uv.dsweb.practica04.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.uv.dsweb.practica04.HibernateUtil;
import org.uv.dsweb.practica04.modelos.Cliente;
import org.uv.dsweb.practica04.modelos.Venta;
import org.uv.dsweb.practica04.modelos.Producto;
import org.uv.dsweb.practica04.modelos.ProductoCantidad;
import org.uv.dsweb.practica04.modelos.DetalleVenta;

import org.hibernate.Session;
import org.hibernate.Transaction;

@ManagedBean(name = "ventaBean")
@SessionScoped
public class VentaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDate fecha;
    private Cliente cliente;
    private double total;
    private List<ProductoCantidad> productos;
    private Producto producto;
    private int cantidad = 1;
    private String busqueda;

    public VentaBean() {
        productos = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<ProductoCantidad> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoCantidad> productos) {
        this.productos = productos;
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

    public String getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(String busqueda) {
        this.busqueda = busqueda;
    }

    public List<Venta> obtenerVentas() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Venta", Venta.class).list();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron obtener las ventas: " + e.getMessage()));
            return new ArrayList<>();
        }
    }

    public void agregarVenta() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Venta venta = new Venta();
            venta.setFecha(fecha);
            venta.setCliente(cliente);
            venta.setNombreCliente(cliente.getNombre());
            venta.setCorreoCliente(cliente.getCorreo());

            session.save(venta);
            agregarDetalleVenta(venta, session);
            transaction.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Venta creada correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear la venta: " + e.getMessage()));
        } finally {
            limpiarVenta();
        }
    }

    private void agregarDetalleVenta(Venta venta, Session session) {
        double totalVenta = 0;

        for (ProductoCantidad productoCantidad : productos) {
            DetalleVenta dVenta = new DetalleVenta();
            dVenta.setVenta(venta);
            dVenta.setProducto(productoCantidad.getProducto());
            dVenta.setDescripcionProducto(productoCantidad.getProducto().getDescripcion());
            dVenta.setCantidad(productoCantidad.getCantidad());
            totalVenta += productoCantidad.getValor();

            session.save(dVenta);
        }

        venta.setTotal(totalVenta);
        session.update(venta);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Detalles agregados a la venta correctamente"));
    }

    public List<DetalleVenta> obtenerDetalles() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from DetalleVenta d where d.venta.id = :ventaId", DetalleVenta.class)
                    .setParameter("ventaId", id)
                    .list();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron obtener los detalles de la venta: " + e.getMessage()));
            return new ArrayList<>();
        }
    }

    public void agregarProducto() {
        ProductoCantidad newProducto = new ProductoCantidad();
        newProducto.setProducto(producto);
        newProducto.setCantidad(cantidad);
        newProducto.setValor(producto.getPrecio() * cantidad);
        productos.add(newProducto);

        cantidad = 1;
    }

    public void quitarProducto(ProductoCantidad producto) {
        productos.remove(producto);
    }

    public List<Cliente> buscarCliente() {
        List<Cliente> resultadoBusqueda = new ArrayList<>();
        List<Cliente> clientes;

        Session session = HibernateUtil.getSessionFactory().openSession();
        clientes = session.createQuery("from Cliente", Cliente.class).list();

        for (Cliente c : clientes) {
            if (c.getNombre().equalsIgnoreCase(busqueda) || c.getCorreo().equalsIgnoreCase(busqueda)) {
                resultadoBusqueda.add(c);
            }
        }

        return resultadoBusqueda;
    }

    public void cargarVenta(Venta venta) {
        this.id = venta.getId();
        this.fecha = venta.getFecha();
        this.cliente = venta.getCliente();
        this.total = venta.getTotal();
    }

    public void limpiarVenta() {
        this.id = null;
        this.fecha = null;
        this.cliente = null;
        this.total = 0;
        this.productos.clear();
        this.producto = null;
        this.cantidad = 1;
        this.busqueda = null;
    }
}
