package modelo;

//Clase que representa una venta

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Venta {
    private Integer id;
    private LocalDateTime fechaHora;
    private double total;
    private List<ItemCarrito> detalles;

    //Constructor
    public Venta(){this.detalles = new ArrayList<>();}

    //Constructor para la base de datos
    public Venta(Integer id, LocalDateTime fechaHora, double total, List<ItemCarrito> detalles){
        this.id = id;
        setFechaHora(fechaHora);
        setTotal(total);
        setDetalles(detalles);
    }

    //Constructor
    public Venta(Carrito carrito){
        if(carrito == null || carrito.getItems().isEmpty()){
            throw new IllegalArgumentException("No se puede registrar una venta con el carrito vacío.");
        }
        this.fechaHora = LocalDateTime.now();
        this.total = carrito.calcularTotal();
        this.detalles = new ArrayList<>(carrito.getItems());
    }

    //Getters y setters
    public Integer getId(){return id;}
    public LocalDateTime getFechaHora(){return fechaHora;}
    public double getTotal(){return total;}
    public List<ItemCarrito> getDetalles(){return detalles;}
    public List<ItemCarrito> getItems(){return detalles;}

    public void setId(Integer id){this.id = id;}

    public void setFechaHora(LocalDateTime fechaHora){ 
        if(fechaHora == null) throw new IllegalArgumentException("La fecha y hora no pueden ser nulas.");
        this.fechaHora = fechaHora; 
    }

    public void setTotal(double total){
        if(total <= 0)throw new IllegalArgumentException("El total de la venta debe ser mayor a 0.");
        this.total = total;
    }

    public void setDetalles(List<ItemCarrito> detalles){ 
        if (detalles == null || detalles.isEmpty())throw new IllegalArgumentException("La venta debe contener al menos un producto.");
        this.detalles = detalles; 
    }
}