package controlador;

//Controlador para gestionar las operaciones del catalogo de productos

import javax.swing.JOptionPane;
import modelo.Producto;
import modelo.ProductoDAO;
import vista.VentanaCatalogo;

public class CatalogoControlador{
    private final VentanaCatalogo vista;
    private final ProductoDAO productoDAO;
    private final Runnable alActualizarCallback;

    //Constructor que conecta la vista con el acceso a datos
    public CatalogoControlador(VentanaCatalogo vista, ProductoDAO productoDAO, Runnable alActualizarCallback){
        this.vista = vista;
        this.productoDAO = productoDAO;
        this.alActualizarCallback = alActualizarCallback;

        this.vista.agregarListeners(
            e -> guardarOActualizarProducto(),
            e -> vista.limpiarFormulario(),
            e -> eliminarProducto(),
            e -> {
                if(!e.getValueIsAdjusting() && vista.getFilaSeleccionada() != -1){
                    vista.cargarProductoSeleccionado(vista.getFilaSeleccionada());
                }
            }
        );

        cargarTablaProductos();
    }

    //Carga los productos activos en la tabla de la vista
    public void cargarTablaProductos(){
        vista.actualizarTabla(productoDAO.obtenerActivos());
    }

    //Valida los datos del formulario y guarda o actualiza el producto
    private void guardarOActualizarProducto(){
        try{
            String nombre = vista.getNombreTexto();
            String precioText = vista.getPrecioTexto();
            String categoria = vista.getCategoriaTexto();

            if(nombre.isEmpty() || precioText.isEmpty() || categoria.isEmpty()){
                vista.mostrarMensaje("Por favor completa todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double precio = Double.parseDouble(precioText);
            String idTexto = vista.getIdTexto();

            if(idTexto.isEmpty()){
                Producto nuevo = new Producto(nombre, precio, categoria);
                nuevo.setActivo(true);
                if(productoDAO.guardar(nuevo)){
                    vista.mostrarMensaje("Producto guardado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }else{
                int id = Integer.parseInt(idTexto);
                Producto existente = new Producto(id, nombre, precio, categoria, true);
                if(productoDAO.actualizar(existente)){
                    vista.mostrarMensaje("Producto actualizado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            vista.limpiarFormulario();
            cargarTablaProductos();
            notificarCambio();

        }catch(NumberFormatException e){
            vista.mostrarMensaje("Ingrese un precio numérico válido.", "Error de validación", JOptionPane.ERROR_MESSAGE);
        }catch(Exception e){
            vista.mostrarMensaje(e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Desactiva el producto seleccionado previa confirmacion del usuario
    private void eliminarProducto(){
        String idTexto = vista.getIdTexto();
        if(idTexto.isEmpty()){
            vista.mostrarMensaje("Por favor, selecciona un producto de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idTexto);
        String nombre = vista.getNombreTexto();

        boolean confirmado = vista.confirmarAccion(
            "¿Desea eliminar el producto '" + nombre + "' del catálogo?",
            "Confirmar Eliminación"
        );

        if(confirmado){
            Producto producto = new Producto(
                id, nombre, Double.parseDouble(vista.getPrecioTexto()), vista.getCategoriaTexto(), false
            );

            if(productoDAO.actualizar(producto)){
                vista.mostrarMensaje("Producto eliminado con éxito.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarTablaProductos();
                notificarCambio();
            }else{
                vista.mostrarMensaje("Ocurrió un error al intentar eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Avisa a la ventana principal que hubo cambios en el catalogo
    private void notificarCambio(){
        if(alActualizarCallback != null){
            alActualizarCallback.run();
        }
    }
}