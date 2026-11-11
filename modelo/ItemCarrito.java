package modelo;

//Clase que representa un elemento en el carrito

public class ItemCarrito {
    private Producto producto;
    private int cantidad;
    private double precioUnitario;

    //Constructor
    public ItemCarrito(Producto producto, int cantidad){
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
    }

    //Getters y setters
    public Producto getProducto(){return producto;}
    public void setProducto(Producto producto){this.producto = producto;}

    public int getCantidad(){return cantidad;}
    public void setCantidad(int cantidad){this.cantidad = cantidad;}

    public double getPrecioUnitario(){return precioUnitario;}
    public void setPrecioUnitario(double precioUnitario){this.precioUnitario = precioUnitario;}

    //Calcular precio del item
    public double calcularSubtotal(){return cantidad * precioUnitario;}
}