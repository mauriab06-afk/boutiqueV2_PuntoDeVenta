package controlador;

//Controlador para pagina principal

import modelo.*;
import vista.VentanaCobro;
import vista.VentanaPuntoDeVenta;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class PuntoDeVentaControlador{
    private final VentanaPuntoDeVenta vista; //Interfaz
    private final ProductoDAO productoDAO; //Conexion con base de datos
    private final VentaDAO ventaDAO; 
    private final List<ItemCarrito> carrito; //Productos actuales

    //Constructor
    public PuntoDeVentaControlador(VentanaPuntoDeVenta vista){
        this.vista = vista;
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
        this.carrito = new ArrayList<>();
    }

    public List<ItemCarrito> getCarrito(){return carrito;}

    public List<String> obtenerCategorias(){return productoDAO.obtenerCategorias();}

    public List<Producto> obtenerProductosPorCategoria(String categoria){
        return productoDAO.obtenerProductosPorCategoria(categoria);
    }

    //AGrega una unidad del producto seleccionado
    public void agregarAlCarrito(Producto producto){
        int cantidadActualEnCarrito = 0;
        //Verifica si el producto ya esta en el carrito
        for(ItemCarrito item : carrito){ 
            if(item.getProducto().getId() == producto.getId()){
                cantidadActualEnCarrito = item.getCantidad();
                break;
            }
        }

        //Verifica la existencia
        if(cantidadActualEnCarrito + 1 > producto.getStock()){
            JOptionPane.showMessageDialog(
                    vista,
                    "No hay suficiente stock para agregar otra unidad de " + producto.getNombre() + ".\nStock disponible: " + producto.getStock(),
                    "Stock Insuficiente",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean existe = false;
        for(ItemCarrito item : carrito){
            if (item.getProducto().getId() == producto.getId()) {
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        //Si ya existe, se aumenta la cantidad
        if(!existe){carrito.add(new ItemCarrito(producto, 1));}

        vista.actualizarTablaCarrito(); //Actualizar
    }

    //Modificacion directa en la tabla
    public void actualizarCantidadItem(int fila, int nuevaCant){
        if(fila < 0 || fila >= carrito.size())return; //Verificar que exista la fila

        ItemCarrito item = carrito.get(fila);
        if(nuevaCant <= 0){ //Si la cantidad es menor que 0 se quita el producto
            carrito.remove(fila);
        }else if(nuevaCant > item.getProducto().getStock()){ //Verifica que haya suficiente stock
            JOptionPane.showMessageDialog(vista,
                    "Stock insuficiente (" + item.getProducto().getStock() + " disponibles).",
                    "Stock Insuficiente", JOptionPane.WARNING_MESSAGE);
        }else{ //Se modifica
            item.setCantidad(nuevaCant);
        }
        vista.actualizarTablaCarrito();
    }

    //Eliminar producto directamente
    public void eliminarItemSeleccionado(int fila){
        if(fila >= 0 && fila < carrito.size()){
            carrito.remove(fila);
            vista.actualizarTablaCarrito();
        }
    }

    //Procesar cobro
    public void procesarCobro(){
        if(carrito.isEmpty()){ //Verifica que el carrito tenga productos
            JOptionPane.showMessageDialog(vista, "El carrito de compras está vacío.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Calcular el total
        double total = 0.0;
        for(ItemCarrito item : carrito){ 
            total += item.calcularSubtotal();
        }

        Venta venta = new Venta();
        venta.setTotal(total);
        venta.setDetalles(new ArrayList<>(carrito));

        VentanaCobro vCobro = new VentanaCobro(vista, total);
        vCobro.setVisible(true);

        //Si se completo el pago
        if(vCobro.isPagoConfirmado()){
            boolean exito = ventaDAO.guardarVenta(venta);
            if(exito){
                JOptionPane.showMessageDialog(vista, "¡Venta registrada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                carrito.clear();
                vista.refrescarProductos();
            }else{
                JOptionPane.showMessageDialog(vista, "Ocurrió un error al guardar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}