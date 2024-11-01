package org.uv.dsweb.practica04.beans;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Properties;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import lombok.Data;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.uv.dsweb.practica04.HibernateUtil;
import org.uv.dsweb.practica04.modelos.*;

@ManagedBean(name = "serviciosBean")
@SessionScoped
@Data
public class ServiciosBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public void imprimir(Venta venta) {
    List<DetalleVenta> detallesVenta = obtenerDetalles(venta.getId());
    String destino = System.getProperty("user.home") + "/Downloads/ticket-" + venta.getId() + ".pdf";

    Document document = new Document();
    try {
        PdfWriter.getInstance(document, new FileOutputStream(destino));
        document.open();

        // Fuentes
        Font fontHeader = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
        Font fontNormal = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font fontTotal = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

        // Encabezado
        document.add(new Paragraph("                Ticket de Compra", fontHeader));
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph("Fecha: " + venta.getFecha(), fontNormal));
        document.add(new Paragraph("Cliente: " + venta.getNombreCliente(), fontNormal));
        document.add(new Paragraph("Correo: " + venta.getCorreoCliente(), fontNormal));
        document.add(new Paragraph(" ", fontNormal));
        document.add(new Paragraph("Detalle de Compra:", fontNormal));
        document.add(new Paragraph(" ", fontNormal));

        // Detalles de la venta
        for (DetalleVenta detalle : detallesVenta) {
            String nombre = detalle.getDescripcionProducto();
            int cantidad = detalle.getCantidad();
            double precioUnitario = detalle.getProducto().getPrecio();
            double subtotal = cantidad * precioUnitario;

            // Formato del detalle
            document.add(new Paragraph(String.format("%-30s x%d", nombre, cantidad), fontNormal));
            document.add(new Paragraph(String.format("Subtotal: $%.2f", subtotal), fontNormal));
            document.add(new Paragraph(" ", fontNormal)); // Espacio entre los detalles
        }

        // Total
        double total = 0;
        for (DetalleVenta detalle : detallesVenta) {
            total += detalle.getCantidad() * detalle.getProducto().getPrecio();
        }

        document.add(new Paragraph(" ", fontNormal)); // Espacio antes del total
        Paragraph totalParagraph = new Paragraph("Total: $" + String.format("%.2f", total), fontTotal);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalParagraph);

        document.add(new Paragraph(" ", fontNormal)); // Espacio antes del mensaje de agradecimiento
        document.add(new Paragraph("¡Gracias por su compra!", fontNormal));

        // Cierre del documento
        document.close();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(
                        FacesMessage.SEVERITY_INFO,
                        "Éxito", "Ticket generado exitosamente en " + destino));
    } catch (FileNotFoundException e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error", "Archivo no encontrado:  " + e.getMessage()));
    } catch (DocumentException e) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(
                        FacesMessage.SEVERITY_ERROR,
                        "Error", "Documento no válido:  " + e.getMessage()));
    }
}


    public void enviar(Venta venta) {
        Properties propiedades = new Properties();
        propiedades.put("mail.smtp.host", "smtp.gmail.com");
        propiedades.put("mail.smtp.port", "587");
        propiedades.put("mail.smtp.auth", "true");
        propiedades.put("mail.smtp.starttls.enable", "true");

        // Autenticación
        final String usuario = "joseluis1710.jlg@gmail.com"; // Cambia por tu correo
        final String contrasena = "gcfazepnrvohbmsz"; // Cambia por tu contraseña

        javax.mail.Session session = javax.mail.Session.getInstance(propiedades, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usuario, contrasena);
            }
        });

        try {
            // Crear el objeto MimeMessage
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(usuario));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(venta.getCorreoCliente()));
            message.setSubject("Ticket");

            // Crear el contenido del mensaje
            BodyPart cuerpo = new MimeBodyPart();
            cuerpo.setText("Gracias por su compar");

            // Adjuntar el PDF
            MimeBodyPart adjunto = new MimeBodyPart();
            imprimir(venta);
            adjunto.attachFile(new File(System.getProperty("user.home") + "/Downloads/ticket-" + venta.getId() + ".pdf"));

            // Combinar partes
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(cuerpo);
            multipart.addBodyPart(adjunto);

            message.setContent(multipart);

            // Enviar el correo
            Transport.send(message);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_INFO,
                            "Éxito", "Correo enviado exitosamente en " + venta.getCorreoCliente()));

        } catch (IOException | MessagingException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(
                            FacesMessage.SEVERITY_ERROR,
                            "Error", "Correo no enviado:  " + e.getMessage()));
        }
    }

    private List<DetalleVenta> obtenerDetalles(Long idVenta) {
        List<DetalleVenta> detallesVenta;

        Session session = HibernateUtil.getSessionFactory().openSession();
        detallesVenta = session.createQuery("from DetalleVenta d where d.venta.id = :ventaId", DetalleVenta.class)
                .setParameter("ventaId", idVenta)
                .list();

        return detallesVenta;
    }
}
