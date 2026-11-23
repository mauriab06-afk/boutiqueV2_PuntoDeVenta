package controlador;

//Controlador para gestionar las consultas y acciones de la ventana de reportes

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import modelo.VentaDAO;
import vista.VentanaReportes;

public class ReportesControlador{
    //Referencias a la vista de reportes y al acceso de datos de ventas
    private final VentanaReportes vista;
    private final VentaDAO ventaDAO;

    //Constructor que conecta los botones de filtros y carga los datos iniciales
    public ReportesControlador(VentanaReportes vista, VentaDAO ventaDAO){
        this.vista = vista;
        this.ventaDAO = ventaDAO;

        //Registra las acciones de los botones y del selector de anio
        this.vista.agregarListeners(
            e -> consultarVentasPorRango(),
            e -> verDetalleVentaSeleccionada(),
            e -> eliminarVentaSeleccionada(),
            e -> cargarDatosAnuales()
        );

        //Llena las tres pestanas al abrir la ventana
        consultarVentasPorRango();
        cargarDatosAnuales();
        cargarEstadisticas();
    }

    //Busca las ventas entre dos fechas y calcula el monto total del periodo
    public void consultarVentasPorRango(){
        String inicio = vista.getFechaInicioTexto();
        String fin = vista.getFechaFinTexto();
        if(inicio.isEmpty() || fin.isEmpty()){
            vista.mostrarMensaje("Por favor ingrese ambas fechas en formato AAAA-MM-DD.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Consulta las ventas en el rango y suma los totales de cada fila
        List<Object[]> ventas = ventaDAO.obtenerVentasPorRango(inicio, fin);
        double totalSuma = 0.0;
        for(Object[] fila : ventas){
            String montoStr = fila[2].toString().replace("$", "").replace(",", "");
            try{
                totalSuma += Double.parseDouble(montoStr);
            }catch(NumberFormatException ignored){}
        }

        //Actualiza la tabla y la etiqueta con el total calculado
        String textoTotal = String.format("Total del Periodo (%s a %s): $%.2f", inicio, fin, totalSuma);
        vista.actualizarTablaRango(ventas, textoTotal);
    }

    //Abre una ventana emergente con los productos vendidos en el ticket seleccionado
    private void verDetalleVentaSeleccionada(){
        int fila = vista.getFilaRangoSeleccionada();
        if(fila == -1){
            vista.mostrarMensaje("Selecciona una venta de la tabla para ver sus detalles.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Obtiene el id, fecha y total de la fila elegida
        int idVenta = (int) vista.getValorCeldaRango(fila, 0);
        String fechaStr = vista.getValorCeldaRango(fila, 1).toString();
        String totalStr = vista.getValorCeldaRango(fila, 2).toString();

        //Consulta los articulos de esa venta y muestra el dialogo de detalle
        List<Object[]> detalles = ventaDAO.obtenerDetalleVenta(idVenta);
        vista.mostrarDialogoDetalleVenta(idVenta, fechaStr, totalStr, detalles);
    }

    //Elimina permanentemente la venta elegida previa confirmacion del usuario
    private void eliminarVentaSeleccionada(){
        int fila = vista.getFilaRangoSeleccionada();
        if(fila == -1){
            vista.mostrarMensaje("Selecciona una venta de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Lee los datos de la venta para mostrarlos en el mensaje de advertencia
        int idVenta = (int) vista.getValorCeldaRango(fila, 0);
        String fechaStr = vista.getValorCeldaRango(fila, 1).toString();
        String totalStr = vista.getValorCeldaRango(fila, 2).toString();

        boolean confirmado = vista.confirmarAccion(
            "¿Desea eliminar permanentemente la Venta #" + idVenta + " (" + fechaStr + " - " + totalStr + ")?",
            "Confirmar Eliminación de Venta"
        );

        //Si el usuario acepta borra la venta y refresca todas las pestanas
        if(confirmado){
            if(ventaDAO.eliminarVenta(idVenta)){
                vista.mostrarMensaje("La venta ha sido eliminada con éxito.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                consultarVentasPorRango();
                cargarDatosAnuales();
                cargarEstadisticas();
            }else{
                vista.mostrarMensaje("Ocurrió un error al eliminar la venta de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Consulta los ingresos mes por mes del anio seleccionado y suma el total anual
    public void cargarDatosAnuales(){
        Integer anio = vista.getAnioSeleccionado();
        if(anio == null) return;

        Map<String, Double> ventasPorMes = ventaDAO.obtenerVentasAnuales(anio);
        List<Object[]> filas = new ArrayList<>();
        double sumaAnual = 0.0;

        //Recorre cada mes para armar las filas y acumular el total del anio
        for(Map.Entry<String, Double> entry : ventasPorMes.entrySet()){
            double totalMes = entry.getValue();
            sumaAnual += totalMes;
            filas.add(new Object[]{entry.getKey(), String.format("$%.2f", totalMes)});
        }

        String textoTotal = String.format("Total Acumulado Año %d: $%.2f", anio, sumaAnual);
        vista.actualizarTablaAnual(filas, textoTotal);
    }

    //Carga las tablas de los 3 productos mas vendidos y los 3 menos vendidos
    public void cargarEstadisticas(){
        List<Object[]> topMas = ventaDAO.obtenerTopProductos(true, 3);
        List<Object[]> topMenos = ventaDAO.obtenerTopProductos(false, 3);
        vista.actualizarTablasTop(topMas, topMenos);
    }
}