package controlador;

import java.awt.Desktop;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import modelo.*;
import vista.*;

public class PuntoDeVentaControlador {
    private final VentanaPuntoDeVenta vista;
    private final ProductoDAO productoDAO;
    private final VentaDAO ventaDAO;
    private final List<ItemCarrito> carrito;

    public PuntoDeVentaControlador(VentanaPuntoDeVenta vista) {
        this.vista = vista;
        this.productoDAO = new ProductoDAO();
        this.ventaDAO = new VentaDAO();
        this.carrito = new ArrayList<>();
    }

    public void inicializarSistema() {
        GestionBackup.verificarYRestaurarSiFalta();
        ConexionBD.inicializarBaseDatos();
        GestionBackup.crearCopiaAutomatica();
    }

    public List<ItemCarrito> getCarrito() { return carrito; }

    public List<String> obtenerCategorias() { return productoDAO.obtenerCategorias(); }

    public List<Producto> obtenerProductosPorCategoria(String categoria) {
        return productoDAO.obtenerProductosPorCategoria(categoria);
    }

    public void agregarAlCarrito(Producto producto) {
        int cantidadActualEnCarrito = 0;
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getId() == producto.getId()) {
                cantidadActualEnCarrito = item.getCantidad();
                break;
            }
        }

        if (cantidadActualEnCarrito + 1 > producto.getStock()) {
            vista.mostrarMensaje(
                "No hay suficiente stock para agregar otra unidad de " + producto.getNombre() + ".\nStock disponible: " + producto.getStock(),
                "Stock Insuficiente",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        boolean existe = false;
        for (ItemCarrito item : carrito) {
            if (item.getProducto().getId() == producto.getId()) {
                item.setCantidad(item.getCantidad() + 1);
                existe = true;
                break;
            }
        }

        if (!existe) {
            carrito.add(new ItemCarrito(producto, 1));
        }

        vista.actualizarTablaCarrito();
    }

    public void procesarEdicionCantidadTabla(int fila, String textoCantidad) {
        try {
            int nuevaCant = Integer.parseInt(textoCantidad.trim());
            actualizarCantidadItem(fila, nuevaCant);
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Ingrese una cantidad entera válida.", "Error", JOptionPane.ERROR_MESSAGE);
            vista.actualizarTablaCarrito();
        }
    }

    public void actualizarCantidadItem(int fila, int nuevaCant) {
        if (fila < 0 || fila >= carrito.size()) return;

        ItemCarrito item = carrito.get(fila);
        if (nuevaCant <= 0) {
            carrito.remove(fila);
        } else if (nuevaCant > item.getProducto().getStock()) {
            vista.mostrarMensaje(
                "Stock insuficiente (" + item.getProducto().getStock() + " disponibles).",
                "Stock Insuficiente",
                JOptionPane.WARNING_MESSAGE
            );
        } else {
            item.setCantidad(nuevaCant);
        }
        vista.actualizarTablaCarrito();
    }

    public void eliminarItemSeleccionado(int fila) {
        if (fila >= 0 && fila < carrito.size()) {
            carrito.remove(fila);
            vista.actualizarTablaCarrito();
        }
    }

    public void procesarCobro() {
        if (carrito.isEmpty()) {
            vista.mostrarMensaje("El carrito de compras está vacío.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double total = 0.0;
        for (ItemCarrito item : carrito) {
            total += item.calcularSubtotal();
        }

        Venta venta = new Venta();
        venta.setTotal(total);
        venta.setDetalles(new ArrayList<>(carrito));

        VentanaCobro vCobro = new VentanaCobro(vista, total);
        CobroControlador cobroCtrl = new CobroControlador(vCobro, total);
        vCobro.setVisible(true);

        if (cobroCtrl.isPagoConfirmado()) {
            boolean exito = ventaDAO.guardarVenta(venta);
            if (exito) {
                vista.mostrarMensaje("¡Venta registrada con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                carrito.clear();
                vista.refrescarProductos();
            } else {
                vista.mostrarMensaje("Ocurrió un error al guardar la venta.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- Navegación y acciones administrativas ---
    public void abrirInventario() {
        VentanaInventario vInv = new VentanaInventario(vista);
        new InventarioControlador(vInv, productoDAO);
        vInv.setVisible(true);
        vista.refrescarProductos();
    }

    public void abrirCatalogo() {
        VentanaCatalogo vCat = new VentanaCatalogo(vista);
        new CatalogoControlador(vCat, productoDAO, vista::refrescarProductos);
        vCat.setVisible(true);
        vista.refrescarProductos();
    }

    public void abrirReportes() {
        VentanaReportes vRep = new VentanaReportes(vista);
        new ReportesControlador(vRep, ventaDAO);
        vRep.setVisible(true);
    }

    public void ejecutarBackupManual() {
        GestionBackup.crearCopiaManual(vista);
    }

    public void abrirMatrizVentas() {
        JDialog dialogo = new JDialog(vista, "Matriz y Gráfica de Ventas por Producto", true);
        PanelMatrizVentas panelMatriz = new PanelMatrizVentas();
        MatrizVentasControlador ctrl = new MatrizVentasControlador(panelMatriz, ventaDAO);
        ctrl.actualizar();

        dialogo.setContentPane(panelMatriz);
        dialogo.setSize(950, 620);
        dialogo.setLocationRelativeTo(vista);
        dialogo.setVisible(true);
    }

    public void abrirUsuarios() {
        VentanaUsuarios vUsr = new VentanaUsuarios(vista);
        new UsuarioControlador(vUsr);
        vUsr.setVisible(true);
    }

    public void abrirGuiaUsuario() {
        String url = "https://natsukimybeloved.infinityfreeapp.com/?i=2";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                Runtime.getRuntime().exec(new String[]{"xdg-open", url});
            }
        } catch (Exception ex) {
            vista.mostrarMensaje(
                "No se pudo abrir el navegador automáticamente.\nVisita: " + url,
                "Error al abrir enlace",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}