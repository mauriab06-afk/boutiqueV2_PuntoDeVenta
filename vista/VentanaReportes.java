package vista;

//Interfaz del manejo de reportes de venta

import modelo.*;
import controlador.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class VentanaReportes extends JDialog {

    private final VentaDAO ventaDAO; //Conexion con base de datos
    private JTabbedPane tabReportes;

    //Pestaña 1 (Rango de fechas)
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JTable tablaRangoFechas;
    private DefaultTableModel modeloRangoFechas;
    private JLabel lblTotalRango;

    //Pestaña 2 (Ventas anuales)
    private JComboBox<Integer> comboAnios;
    private JTable tablaVentasAnuales;
    private DefaultTableModel modeloVentasAnuales;
    private JLabel lblTotalAnual;

    //Pestaña 3 (Mas vendidos)
    private JTable tablaMasVendidos;
    private DefaultTableModel modeloMasVendidos;
    private JTable tablaMenosVendidos;
    private DefaultTableModel modeloMenosVendidos;

    //Constructor
    public VentanaReportes(Frame parent){
        super(parent, "Reportes y Estadísticas de Ventas", true);
        this.ventaDAO = new VentaDAO();

        configurarVentana();
        inicializarComponentes();

        consultarVentasPorRango();
        cargarDatosAnuales();
        cargarEstadisticas();
    }

    //Configuracion de la ventana
    private void configurarVentana() {
        setSize(950, 650);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(true);
    }

    //Inicializacion de componentes
    private void inicializarComponentes(){
        tabReportes = new JTabbedPane();
        tabReportes.setFont(new Font("Arial", Font.BOLD, 14));

        //Pestaña 1 (Rango de fechas)
        JPanel panelRango = new JPanel(new BorderLayout(10, 10));
        panelRango.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltroFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fechaInicioMes = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        txtFechaInicio = new JTextField(fechaInicioMes, 10);
        txtFechaFin = new JTextField(fechaHoy, 10);
        txtFechaInicio.setFont(new Font("Arial", Font.PLAIN, 14));
        txtFechaFin.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton btnBuscarFechas = new JButton("Filtrar / Buscar");
        btnBuscarFechas.setFont(new Font("Arial", Font.BOLD, 13));
        btnBuscarFechas.setBackground(new Color(70, 130, 180));
        btnBuscarFechas.setForeground(Color.WHITE);
        btnBuscarFechas.addActionListener(e -> consultarVentasPorRango());

        panelFiltroFechas.add(new JLabel("Desde (AAAA-MM-DD):"));
        panelFiltroFechas.add(txtFechaInicio);
        panelFiltroFechas.add(new JLabel("Hasta (AAAA-MM-DD):"));
        panelFiltroFechas.add(txtFechaFin);
        panelFiltroFechas.add(btnBuscarFechas);

        panelRango.add(panelFiltroFechas, BorderLayout.NORTH);

        String[] colRango = {"ID Venta", "Fecha y Hora", "Total ($)"};
        modeloRangoFechas = new DefaultTableModel(colRango, 0){
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };

        tablaRangoFechas = new JTable(modeloRangoFechas);
        tablaRangoFechas.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaRangoFechas.setRowHeight(26);
        tablaRangoFechas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        panelRango.add(new JScrollPane(tablaRangoFechas), BorderLayout.CENTER);

        JPanel panelAccionesFechas = new JPanel(new BorderLayout(10, 5));

        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton btnVerDetalle = new JButton("Ver Detalles de la Venta");
        btnVerDetalle.setFont(new Font("Arial", Font.BOLD, 13));
        btnVerDetalle.setBackground(new Color(40, 167, 69));
        btnVerDetalle.setForeground(Color.WHITE);
        btnVerDetalle.addActionListener(e -> verDetalleVentaSeleccionada());

        JButton btnEliminarVenta = new JButton("Eliminar Venta");
        btnEliminarVenta.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminarVenta.setBackground(new Color(220, 53, 69));
        btnEliminarVenta.setForeground(Color.WHITE);
        btnEliminarVenta.addActionListener(e -> eliminarVentaSeleccionada());

        panelBotonesAccion.add(btnVerDetalle);
        panelBotonesAccion.add(btnEliminarVenta);

        lblTotalRango = new JLabel("Total del Periodo: $0.00", SwingConstants.RIGHT);
        lblTotalRango.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalRango.setForeground(new Color(34, 139, 34));

        panelAccionesFechas.add(panelBotonesAccion, BorderLayout.WEST);
        panelAccionesFechas.add(lblTotalRango, BorderLayout.EAST);

        panelRango.add(panelAccionesFechas, BorderLayout.SOUTH);

        tabReportes.addTab("Ventas por Fechas", panelRango);

        //Pestaña 2 (Ventas anuales)
        JPanel panelAnual = new JPanel(new BorderLayout(10, 10));
        panelAnual.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelFiltroAnio = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltroAnio.add(new JLabel("Seleccionar Año:"));

        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] anios = new Integer[5];
        for (int i = 0; i < 5; i++) {
            anios[i] = anioActual - i;
        }
        comboAnios = new JComboBox<>(anios);
        comboAnios.setFont(new Font("Arial", Font.BOLD, 14));
        comboAnios.addActionListener(e -> cargarDatosAnuales());
        panelFiltroAnio.add(comboAnios);

        panelAnual.add(panelFiltroAnio, BorderLayout.NORTH);

        String[] colAnual = {"Mes", "Total Recaudado"};
        modeloVentasAnuales = new DefaultTableModel(colAnual, 0) {
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };

        tablaVentasAnuales = new JTable(modeloVentasAnuales);
        tablaVentasAnuales.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaVentasAnuales.setRowHeight(26);
        tablaVentasAnuales.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        panelAnual.add(new JScrollPane(tablaVentasAnuales), BorderLayout.CENTER);

        lblTotalAnual = new JLabel("Total del Año: $0.00", SwingConstants.RIGHT);
        lblTotalAnual.setFont(new Font("Arial", Font.BOLD, 20));
        lblTotalAnual.setForeground(new Color(34, 139, 34));
        panelAnual.add(lblTotalAnual, BorderLayout.SOUTH);

        tabReportes.addTab("Ventas Anuales", panelAnual);

         //Pestaña 3 (Mas vendidos)
        JPanel panelEstadisticas = new JPanel(new GridLayout(1, 2, 15, 10));
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] colTop = {"Producto", "Unidades", "Total Recaudado"};

        JPanel panelMasVendidos = new JPanel(new BorderLayout(5, 5));
        panelMasVendidos.setBorder(BorderFactory.createTitledBorder("Top 3 - Más Vendidos"));

        modeloMasVendidos = new DefaultTableModel(colTop, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {return false;}
        };
        tablaMasVendidos = new JTable(modeloMasVendidos);
        tablaMasVendidos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaMasVendidos.setRowHeight(26);
        tablaMasVendidos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        panelMasVendidos.add(new JScrollPane(tablaMasVendidos), BorderLayout.CENTER);

        JPanel panelMenosVendidos = new JPanel(new BorderLayout(5, 5));
        panelMenosVendidos.setBorder(BorderFactory.createTitledBorder("Top 3 - Menos Vendidos"));

        modeloMenosVendidos = new DefaultTableModel(colTop, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaMenosVendidos = new JTable(modeloMenosVendidos);
        tablaMenosVendidos.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaMenosVendidos.setRowHeight(26);
        tablaMenosVendidos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        panelMenosVendidos.add(new JScrollPane(tablaMenosVendidos), BorderLayout.CENTER);

        panelEstadisticas.add(panelMasVendidos);
        panelEstadisticas.add(panelMenosVendidos);

        tabReportes.addTab("Estadísticas (Top 3)", panelEstadisticas);

        add(tabReportes, BorderLayout.CENTER);
    }

    //Consultar las ventas en un rango de fechas
    private void consultarVentasPorRango(){
        String inicio = txtFechaInicio.getText().trim();
        String fin = txtFechaFin.getText().trim();
        if(inicio.isEmpty() || fin.isEmpty()){ //Campos vacios
            JOptionPane.showMessageDialog(this, "Por favor ingrese ambas fechas en formato AAAA-MM-DD.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloRangoFechas.setRowCount(0);
        List<Object[]> ventas = ventaDAO.obtenerVentasPorRango(inicio, fin);

        double totalSuma = 0.0;
        for(Object[] fila : ventas){
            modeloRangoFechas.addRow(fila);
            String montoStr = fila[2].toString().replace("$", "").replace(",", "");
            try{
                totalSuma += Double.parseDouble(montoStr);
            }catch(NumberFormatException ignored){}
        }
        lblTotalRango.setText(String.format("Total del Periodo (%s a %s): $%.2f", inicio, fin, totalSuma));
    }

    //Desglose de una venta en espesifico
    private void verDetalleVentaSeleccionada(){
        int fila = tablaRangoFechas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona una venta de la tabla para ver sus detalles.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idVenta = (int) tablaRangoFechas.getValueAt(fila, 0);
        String fechaStr = tablaRangoFechas.getValueAt(fila, 1).toString();
        String totalStr = tablaRangoFechas.getValueAt(fila, 2).toString();

        List<Object[]> detalles = ventaDAO.obtenerDetalleVenta(idVenta);

        JDialog dialogDetalle = new JDialog(this, "Detalles de Venta #" + idVenta, true);
        dialogDetalle.setSize(550, 350);
        dialogDetalle.setLocationRelativeTo(this);
        dialogDetalle.setLayout(new BorderLayout(10, 10));

        JPanel panelHeader = new JPanel(new GridLayout(2, 1, 5, 5));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        JLabel lblInfoFecha = new JLabel("Fecha y Hora: " + fechaStr);
        lblInfoFecha.setFont(new Font("Arial", Font.BOLD, 14));
        JLabel lblInfoTotal = new JLabel("Monto Total: " + totalStr);
        lblInfoTotal.setFont(new Font("Arial", Font.BOLD, 16));
        lblInfoTotal.setForeground(new Color(34, 139, 34));

        panelHeader.add(lblInfoFecha);
        panelHeader.add(lblInfoTotal);
        dialogDetalle.add(panelHeader, BorderLayout.NORTH);

        String[] cols = {"Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        DefaultTableModel modelDet = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){return false;}
        };

        for(Object[] d : detalles){
            modelDet.addRow(d);
        }

        JTable tablaDet = new JTable(modelDet);
        tablaDet.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaDet.setRowHeight(24);
        tablaDet.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        dialogDetalle.add(new JScrollPane(tablaDet), BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialogDetalle.dispose());
        JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSouth.add(btnCerrar);
        dialogDetalle.add(panelSouth, BorderLayout.SOUTH);

        dialogDetalle.setVisible(true);
    }

    //Elimina la venta y actualiza la pantalla
    private void eliminarVentaSeleccionada(){
        int fila = tablaRangoFechas.getSelectedRow();
        if(fila == -1){
            JOptionPane.showMessageDialog(this, "Selecciona una venta de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idVenta = (int) tablaRangoFechas.getValueAt(fila, 0);
        String fechaStr = tablaRangoFechas.getValueAt(fila, 1).toString();
        String totalStr = tablaRangoFechas.getValueAt(fila, 2).toString();

        Object[] opciones = {"Sí", "No"};
        int confirm = JOptionPane.showOptionDialog(
                this,
                "¿Desea eliminar permanentemente la Venta #" + idVenta + " (" + fechaStr + " - " + totalStr + ")?",
                "Confirmar Eliminación de Venta",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[1]
        );

        if(confirm == 0){
            boolean exito = ventaDAO.eliminarVenta(idVenta);
            if(exito){
                JOptionPane.showMessageDialog(this, "La venta ha sido eliminada con éxito.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                consultarVentasPorRango();
                cargarDatosAnuales();
                cargarEstadisticas();
            }else{
                JOptionPane.showMessageDialog(this, "Ocurrió un error al eliminar la venta de la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Carga los datos anuales en tablas
    private void cargarDatosAnuales(){
        if(comboAnios.getSelectedItem() == null) return;
        int anio = (Integer) comboAnios.getSelectedItem();

        modeloVentasAnuales.setRowCount(0);
        Map<String, Double> ventasPorMes = ventaDAO.obtenerVentasAnuales(anio);

        double sumaAnual = 0.0;
        for(Map.Entry<String, Double> entry : ventasPorMes.entrySet()){
            double totalMes = entry.getValue();
            sumaAnual += totalMes;

            Object[] fila = {
                entry.getKey(),
                String.format("$%.2f", totalMes)
            };
            modeloVentasAnuales.addRow(fila);
        }

        lblTotalAnual.setText(String.format("Total Acumulado Año %d: $%.2f", anio, sumaAnual));
    }

    //Carga las estadisticas de los 3 mas vendidos
    private void cargarEstadisticas(){
        modeloMasVendidos.setRowCount(0);
        List<Object[]> topMas = ventaDAO.obtenerTopProductos(true, 3);
        for (Object[] fila : topMas) {
            modeloMasVendidos.addRow(fila);
        }

        modeloMenosVendidos.setRowCount(0);
        List<Object[]> topMenos = ventaDAO.obtenerTopProductos(false, 3);
        for (Object[] fila : topMenos) {
            modeloMenosVendidos.addRow(fila);
        }
    }
}