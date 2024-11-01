package org.uv.dsweb.practica04.beans;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.uv.dsweb.practica04.HibernateUtil;
import org.uv.dsweb.practica04.modelos.Producto;

import org.hibernate.Session;
import org.hibernate.Transaction;

@ManagedBean(name = "productoBean")
@SessionScoped
public class ProductoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String descripcion;
    private double precio;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public List<Producto> obtenerProductos() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Producto", Producto.class).list();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron obtener los productos: " + e.getMessage()));
            return null;
        }
    }

    public void guardarProducto() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Producto producto = new Producto();
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);

            session.save(producto);
            transaction.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto creado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el producto: " + e.getMessage()));
        } finally {
            limpiarProducto();
        }
    }

    public void editarProducto() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Producto producto = session.get(Producto.class, id);
            if (producto != null) {
                producto.setDescripcion(descripcion);
                producto.setPrecio(precio);
                session.update(producto);
                transaction.commit();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto actualizado correctamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Producto no encontrado"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el producto: " + e.getMessage()));
        }
    }

    public void eliminarProducto(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Producto producto = session.get(Producto.class, id);
            if (producto != null) {
                session.delete(producto);
                transaction.commit();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Producto eliminado correctamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Producto no encontrado"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el producto: " + e.getMessage()));
        }
    }

    public void cargarProducto(Producto producto) {
        this.id = producto.getId();
        this.descripcion = producto.getDescripcion();
        this.precio = producto.getPrecio();
    }

    public void limpiarProducto() {
        this.id = null;
        this.descripcion = null;
        this.precio = 0;
    }
}
