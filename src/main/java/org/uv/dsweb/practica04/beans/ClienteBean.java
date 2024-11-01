package org.uv.dsweb.practica04.beans;

import java.io.Serializable;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.uv.dsweb.practica04.HibernateUtil;
import org.uv.dsweb.practica04.modelos.Cliente;

import org.hibernate.Session;
import org.hibernate.Transaction;

@ManagedBean(name = "clienteBean")
@SessionScoped
public class ClienteBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String nombre;
    private String correo;
    private String telefono;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public List<Cliente> obtenerClientes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Cliente", Cliente.class).list();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudieron obtener los clientes: " + e.getMessage()));
            return null;
        }
    }

    public void guardarCliente() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setCorreo(correo);
            cliente.setTelefono(telefono);

            session.save(cliente);
            transaction.commit();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente creado correctamente"));
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo crear el cliente: " + e.getMessage()));
        } finally {
            limpiarCliente();
        }
    }

    public void editarCliente() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Cliente cliente = session.get(Cliente.class, id);
            if (cliente != null) {
                cliente.setNombre(nombre);
                cliente.setCorreo(correo);
                cliente.setTelefono(telefono);
                session.update(cliente);
                transaction.commit();

                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente actualizado correctamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Cliente no encontrado"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo actualizar el cliente: " + e.getMessage()));
        }
    }

    public void eliminarCliente(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            Cliente cliente = session.get(Cliente.class, id);
            if (cliente != null) {
                session.delete(cliente);
                transaction.commit();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cliente eliminado correctamente"));
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Advertencia", "Cliente no encontrado"));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar el cliente: " + e.getMessage()));
        }
    }

    public void cargarCliente(Cliente cliente) {
        this.id = cliente.getId();
        this.nombre = cliente.getNombre();
        this.correo = cliente.getCorreo();
        this.telefono = cliente.getTelefono();
    }

    public void limpiarCliente() {
        this.id = null;
        this.nombre = null;
        this.correo = null;
        this.telefono = null;
    }
}
