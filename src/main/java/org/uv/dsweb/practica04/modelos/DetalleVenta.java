package org.uv.dsweb.practica04.modelos;

import java.io.Serializable;
import javax.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "detalleventa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVenta implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "id_venta", nullable = false)
    Venta venta;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    Producto producto;
    
    @Column(name = "descripcion_producto")
    String descripcionProducto;
    
    @Column(name = "cantidad")
    int cantidad;
}
