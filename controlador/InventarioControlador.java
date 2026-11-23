package controlador;

//Controlador para consultar y actualizar el inventario de productos

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Producto;
import modelo.ProductoDAO;
import vista.VentanaInventario;

public class InventarioControlador{
    //Referencias a la vista y al acceso de datos
    private final VentanaInventario vista;
    private final ProductoDAO productoDAO;

    //Constructor que conecta los eventos de busqueda y ajuste de stock
    public InventarioControlador(VentanaInventario vista, ProductoDAO productoDAO){
        this.vista = vista;
        this.productoDAO = productoDAO;

        //Registra el filtro en vivo y los botones de accion
        this.vista.agregarListeners(
            new KeyAdapter(){
                @Override
                public void keyReleased(KeyEvent e){
                    cargarInventario();
                }
            },
            e -> ajustarStock(),
            e -> vista.dispose()
        );

        //Carga los productos iniciales al abrir la ventana
        cargarInventario();
    }

    //Busca los productos filtrados y llena la tabla del inventario
    public void cargarInventario(){
        try{
            //Obtiene los productos activos que coinciden con el texto buscado
            List<Producto> productos = productoDAO.buscarActivosPorNombre(vista.getFiltroBusqueda());
            List<Object[]> filas = new ArrayList<>();

            //Arma cada renglon con su estado de existencia
            for(Producto p : productos){
                String estado = clasificarStock(p.getStock());
                filas.add(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getCategoria(),
                    "$" + String.format("%.2f", p.getPrecio()),
                    p.getStock(),
                    estado
                });
            }

            //Envia las filas construidas a la tabla de la vista
            vista.actualizarTabla(filas);
        }catch(SQLException e){
            vista.mostrarMensaje("Error al cargar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Devuelve la etiqueta de estado segun la cantidad disponible
    private String clasificarStock(int stock){
        if(stock <= 0) return "AGOTADO";
        if(stock <= 5) return "STOCK BAJO";
        return "SUFICIENTE";
    }

    //Valida la nueva cantidad ingresada y actualiza el stock en base de datos
    private void ajustarStock(){
        //Verifica que haya una fila seleccionada en la tabla
        int fila = vista.getFilaSeleccionada();
        if(fila == -1){
            vista.mostrarMensaje("Selecciona un producto de la tabla para ajustar su stock.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Extrae los datos del producto seleccionado
        int idProducto = (int) vista.getValorCelda(fila, 0);
        String nombreProducto = (String) vista.getValorCelda(fila, 1);
        int stockActual = (int) vista.getValorCelda(fila, 4);

        //Pide el nuevo stock al usuario y valida si cancelo
        String input = vista.solicitarCantidadStock(nombreProducto, stockActual);
        if(input == null || input.trim().isEmpty()) return;

        try{
            int nuevoStock = Integer.parseInt(input.trim());

            //Impide registrar existencias negativas
            if(nuevoStock < 0){
                vista.mostrarMensaje("El stock no puede ser un número negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            //Guarda el cambio en la base de datos y refresca la tabla
            productoDAO.actualizarStock(idProducto, nuevoStock);
            vista.mostrarMensaje("Stock actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cargarInventario();

        }catch(NumberFormatException e){
            vista.mostrarMensaje("Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }catch(SQLException e){
            vista.mostrarMensaje("Error al actualizar el stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}