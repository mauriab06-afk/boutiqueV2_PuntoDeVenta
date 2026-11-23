package vista;

//Ventana moderna para consultar reportes y estadisticas de ventas

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class VentanaReportes extends JDialog{
    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color PILDORA_STOCK_BG = new Color(214, 240, 221);
    private final Color PILDORA_STOCK_TXT = new Color(24, 118, 54);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    private JTabbedPane tabReportes;

    //Pestana 1 (Rango de fechas)
    private JTextField txtFechaInicio;
    private JTextField txtFechaFin;
    private JButton btnBuscarFechas;
    private JTable tablaRangoFechas;
    private DefaultTableModel modeloRangoFechas;
    private JButton btnVerDetalle;
    private JButton btnEliminarVenta;
    private JLabel lblTotalRango;

    //Pestana 2 (Ventas anuales)
    private JComboBox<Integer> comboAnios;
    private JTable tablaVentasAnuales;
    private DefaultTableModel modeloVentasAnuales;
    private JLabel lblTotalAnual;

    //Pestana 3 (Mas y menos vendidos)
    private JTable tablaMasVendidos;
    private DefaultTableModel modeloMasVendidos;
    private JTable tablaMenosVendidos;
    private DefaultTableModel modeloMenosVendidos;

    //Constructor principal
    public VentanaReportes(Frame parent){
        super(parent, "Reportes y Estadísticas de Ventas", true);
        configurarVentana();
        inicializarComponentes();
    }

    //Configura tamano, centrado y fondo general
    private void configurarVentana(){
        setSize(1060, 680);
        setMinimumSize(new Dimension(920, 600));
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO_CREMA);
        setResizable(true);
    }

    //Construye el encabezado superior y las tres pestanas de reportes
    private void inicializarComponentes(){
        //Barra superior verde bosque
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel lblTitulo = new JLabel("▤  Reportes y Estadísticas de Ventas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Consulta ingresos por periodo, resumen anual y ranking de productos");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(210, 240, 220));

        JPanel panelTitulos = new JPanel();
        panelTitulos.setOpaque(false);
        panelTitulos.setLayout(new BoxLayout(panelTitulos, BoxLayout.Y_AXIS));
        panelTitulos.add(lblTitulo);
        panelTitulos.add(Box.createVerticalStrut(3));
        panelTitulos.add(lblSubtitulo);

        panelHeader.add(panelTitulos, BorderLayout.WEST);
        add(panelHeader, BorderLayout.NORTH);

        //Contenedor principal con margen sobre fondo crema
        JPanel panelCuerpo = new JPanel(new BorderLayout());
        panelCuerpo.setBackground(FONDO_CREMA);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(16, 24, 18, 24));

        tabReportes = new JTabbedPane();
        tabReportes.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabReportes.setBackground(FONDO_CREMA);

        //Pestana 1: Rango de fechas
        JPanel panelRango = new JPanel(new BorderLayout(0, 14));
        panelRango.setBackground(FONDO_CREMA);
        panelRango.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));

        //Tarjeta de filtros de fecha
        JPanel tarjetaFiltroFechas = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        tarjetaFiltroFechas.setBackground(Color.WHITE);
        tarjetaFiltroFechas.putClientProperty("FlatLaf.style", "arc: 18");

        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fechaInicioMes = LocalDate.now().withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        JLabel lblDesde = new JLabel("Desde (AAAA-MM-DD):");
        lblDesde.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblDesde.setForeground(new Color(55, 60, 58));

        txtFechaInicio = new JTextField(fechaInicioMes, 11);
        txtFechaInicio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtFechaInicio.setPreferredSize(new Dimension(120, 34));
        txtFechaInicio.putClientProperty("FlatLaf.style", "arc: 12");

        JLabel lblHasta = new JLabel("Hasta (AAAA-MM-DD):");
        lblHasta.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblHasta.setForeground(new Color(55, 60, 58));

        txtFechaFin = new JTextField(fechaHoy, 11);
        txtFechaFin.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtFechaFin.setPreferredSize(new Dimension(120, 34));
        txtFechaFin.putClientProperty("FlatLaf.style", "arc: 12");

        btnBuscarFechas = new JButton("⌕  Filtrar / Buscar");
        btnBuscarFechas.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnBuscarFechas.setBackground(VERDE_PRIMARIO);
        btnBuscarFechas.setForeground(Color.WHITE);
        btnBuscarFechas.setPreferredSize(new Dimension(165, 36));
        btnBuscarFechas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscarFechas.putClientProperty("FlatLaf.style", "arc: 14");

        tarjetaFiltroFechas.add(lblDesde);
        tarjetaFiltroFechas.add(txtFechaInicio);
        tarjetaFiltroFechas.add(lblHasta);
        tarjetaFiltroFechas.add(txtFechaFin);
        tarjetaFiltroFechas.add(btnBuscarFechas);

        panelRango.add(tarjetaFiltroFechas, BorderLayout.NORTH);

        //Tarjeta con tabla de ventas por fechas
        JPanel tarjetaTablaRango = new JPanel(new BorderLayout());
        tarjetaTablaRango.setBackground(Color.WHITE);
        tarjetaTablaRango.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tarjetaTablaRango.putClientProperty("FlatLaf.style", "arc: 20");

        String[] colRango = {"ID Venta", "Fecha y Hora", "Total ($)"};
        modeloRangoFechas = new DefaultTableModel(colRango, 0){
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };

        tablaRangoFechas = new JTable(modeloRangoFechas);
        estilizarTabla(tablaRangoFechas, new int[]{SwingConstants.CENTER, SwingConstants.CENTER, SwingConstants.RIGHT});
        tablaRangoFechas.getColumnModel().getColumn(0).setMaxWidth(110);

        JScrollPane scrollRango = new JScrollPane(tablaRangoFechas);
        scrollRango.setBorder(null);
        scrollRango.getViewport().setBackground(Color.WHITE);
        tarjetaTablaRango.add(scrollRango, BorderLayout.CENTER);

        panelRango.add(tarjetaTablaRango, BorderLayout.CENTER);

        //Barra inferior con separacion amplia entre botones y total
        JPanel panelAccionesFechas = new JPanel(new BorderLayout(24, 0));
        panelAccionesFechas.setOpaque(false);
        panelAccionesFechas.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        JPanel panelBotonesAccion = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        panelBotonesAccion.setOpaque(false);

        btnVerDetalle = new JButton("☰  Ver Detalles");
        btnVerDetalle.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnVerDetalle.setBackground(VERDE_HEADER);
        btnVerDetalle.setForeground(Color.WHITE);
        btnVerDetalle.setPreferredSize(new Dimension(160, 42));
        btnVerDetalle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerDetalle.putClientProperty("FlatLaf.style", "arc: 16");

        btnEliminarVenta = new JButton("✕  Eliminar Venta");
        btnEliminarVenta.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnEliminarVenta.setBackground(ROJO_ALERTA);
        btnEliminarVenta.setForeground(Color.WHITE);
        btnEliminarVenta.setPreferredSize(new Dimension(160, 42));
        btnEliminarVenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminarVenta.putClientProperty("FlatLaf.style", "arc: 16");

        panelBotonesAccion.add(btnVerDetalle);
        panelBotonesAccion.add(btnEliminarVenta);

        //Tarjeta pildora para que el total respire y no se encime
        JPanel tarjetaTotalRango = new JPanel(new BorderLayout());
        tarjetaTotalRango.setBackground(PILDORA_STOCK_BG);
        tarjetaTotalRango.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        tarjetaTotalRango.putClientProperty("FlatLaf.style", "arc: 16");

        lblTotalRango = new JLabel("Total del Periodo: $0.00", SwingConstants.RIGHT);
        lblTotalRango.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblTotalRango.setForeground(VERDE_HEADER);
        tarjetaTotalRango.add(lblTotalRango, BorderLayout.CENTER);

        panelAccionesFechas.add(panelBotonesAccion, BorderLayout.WEST);
        panelAccionesFechas.add(tarjetaTotalRango, BorderLayout.EAST);

        panelRango.add(panelAccionesFechas, BorderLayout.SOUTH);
        tabReportes.addTab("Ventas por Fechas", panelRango);

        //Pestana 2: Ventas anuales
        JPanel panelAnual = new JPanel(new BorderLayout(0, 14));
        panelAnual.setBackground(FONDO_CREMA);
        panelAnual.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));

        JPanel tarjetaFiltroAnio = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        tarjetaFiltroAnio.setBackground(Color.WHITE);
        tarjetaFiltroAnio.putClientProperty("FlatLaf.style", "arc: 18");

        JLabel lblAnio = new JLabel("Seleccionar Año:");
        lblAnio.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblAnio.setForeground(new Color(55, 60, 58));

        int anioActual = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] anios = new Integer[5];
        for(int i = 0; i < 5; i++){
            anios[i] = anioActual - i;
        }
        comboAnios = new JComboBox<>(anios);
        comboAnios.setFont(new Font("SansSerif", Font.BOLD, 14));
        comboAnios.setPreferredSize(new Dimension(120, 34));
        comboAnios.putClientProperty("FlatLaf.style", "arc: 12");

        tarjetaFiltroAnio.add(lblAnio);
        tarjetaFiltroAnio.add(comboAnios);

        panelAnual.add(tarjetaFiltroAnio, BorderLayout.NORTH);

        JPanel tarjetaTablaAnual = new JPanel(new BorderLayout());
        tarjetaTablaAnual.setBackground(Color.WHITE);
        tarjetaTablaAnual.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tarjetaTablaAnual.putClientProperty("FlatLaf.style", "arc: 20");

        String[] colAnual = {"Mes", "Total Recaudado"};
        modeloVentasAnuales = new DefaultTableModel(colAnual, 0){
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };

        tablaVentasAnuales = new JTable(modeloVentasAnuales);
        estilizarTabla(tablaVentasAnuales, new int[]{SwingConstants.LEFT, SwingConstants.RIGHT});

        JScrollPane scrollAnual = new JScrollPane(tablaVentasAnuales);
        scrollAnual.setBorder(null);
        scrollAnual.getViewport().setBackground(Color.WHITE);
        tarjetaTablaAnual.add(scrollAnual, BorderLayout.CENTER);

        panelAnual.add(tarjetaTablaAnual, BorderLayout.CENTER);

        JPanel filaTotalAnual = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaTotalAnual.setOpaque(false);

        JPanel tarjetaTotalAnual = new JPanel(new BorderLayout());
        tarjetaTotalAnual.setBackground(PILDORA_STOCK_BG);
        tarjetaTotalAnual.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        tarjetaTotalAnual.putClientProperty("FlatLaf.style", "arc: 16");

        lblTotalAnual = new JLabel("Total del Año: $0.00", SwingConstants.RIGHT);
        lblTotalAnual.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblTotalAnual.setForeground(VERDE_HEADER);
        tarjetaTotalAnual.add(lblTotalAnual, BorderLayout.CENTER);

        filaTotalAnual.add(tarjetaTotalAnual);
        panelAnual.add(filaTotalAnual, BorderLayout.SOUTH);

        tabReportes.addTab("Ventas Anuales", panelAnual);

        //Pestana 3: Estadisticas Top 3
        JPanel panelEstadisticas = new JPanel(new GridLayout(1, 2, 18, 0));
        panelEstadisticas.setBackground(FONDO_CREMA);
        panelEstadisticas.setBorder(BorderFactory.createEmptyBorder(14, 4, 4, 4));

        String[] colTop = {"Producto", "Unidades", "Total Recaudado"};

        //Tarjeta de productos mas vendidos
        JPanel tarjetaMasVendidos = new JPanel(new BorderLayout(0, 10));
        tarjetaMasVendidos.setBackground(Color.WHITE);
        tarjetaMasVendidos.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjetaMasVendidos.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblMas = new JLabel("↗  Top 3 - Más Vendidos");
        lblMas.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblMas.setForeground(VERDE_HEADER);

        modeloMasVendidos = new DefaultTableModel(colTop, 0){
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };
        tablaMasVendidos = new JTable(modeloMasVendidos);
        estilizarTabla(tablaMasVendidos, new int[]{SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT});

        JScrollPane scrollMas = new JScrollPane(tablaMasVendidos);
        scrollMas.setBorder(null);
        scrollMas.getViewport().setBackground(Color.WHITE);

        tarjetaMasVendidos.add(lblMas, BorderLayout.NORTH);
        tarjetaMasVendidos.add(scrollMas, BorderLayout.CENTER);

        //Tarjeta de productos menos vendidos
        JPanel tarjetaMenosVendidos = new JPanel(new BorderLayout(0, 10));
        tarjetaMenosVendidos.setBackground(Color.WHITE);
        tarjetaMenosVendidos.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjetaMenosVendidos.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblMenos = new JLabel("↘  Top 3 - Menos Vendidos");
        lblMenos.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblMenos.setForeground(ROJO_ALERTA);

        modeloMenosVendidos = new DefaultTableModel(colTop, 0){
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };
        tablaMenosVendidos = new JTable(modeloMenosVendidos);
        estilizarTabla(tablaMenosVendidos, new int[]{SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT});

        JScrollPane scrollMenos = new JScrollPane(tablaMenosVendidos);
        scrollMenos.setBorder(null);
        scrollMenos.getViewport().setBackground(Color.WHITE);

        tarjetaMenosVendidos.add(lblMenos, BorderLayout.NORTH);
        tarjetaMenosVendidos.add(scrollMenos, BorderLayout.CENTER);

        panelEstadisticas.add(tarjetaMasVendidos);
        panelEstadisticas.add(tarjetaMenosVendidos);

        tabReportes.addTab("Estadísticas (Top 3)", panelEstadisticas);

        panelCuerpo.add(tabReportes, BorderLayout.CENTER);
        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Aplica el formato moderno uniforme a cualquier tabla de reportes
    private void estilizarTabla(JTable tabla, int[] alineaciones){
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(34);
        tabla.setShowVerticalLines(false);
        tabla.setGridColor(new Color(235, 238, 235));
        tabla.setSelectionBackground(new Color(225, 245, 230));
        tabla.setSelectionForeground(new Color(20, 70, 35));

        tabla.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tabla.getTableHeader().setBackground(new Color(245, 247, 245));
        tabla.getTableHeader().setForeground(new Color(45, 50, 48));
        tabla.getTableHeader().setPreferredSize(new Dimension(0, 36));

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column){
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if(column < alineaciones.length){
                    c.setHorizontalAlignment(alineaciones[column]);
                }
                if(!isSelected){
                    c.setBackground(Color.WHITE);
                    c.setForeground(new Color(40, 44, 42));
                }
                return c;
            }
        });
    }

    //Getters para que el controlador lea la vista
    public String getFechaInicioTexto(){ return txtFechaInicio.getText().trim(); }
    public String getFechaFinTexto(){ return txtFechaFin.getText().trim(); }
    public int getFilaRangoSeleccionada(){ return tablaRangoFechas.getSelectedRow(); }
    public Object getValorCeldaRango(int fila, int columna){ return tablaRangoFechas.getValueAt(fila, columna); }
    public Integer getAnioSeleccionado(){ return (Integer) comboAnios.getSelectedItem(); }

    //Metodos para actualizar la interfaz
    public void actualizarTablaRango(List<Object[]> filas, String textoTotal){
        modeloRangoFechas.setRowCount(0);
        for(Object[] fila : filas){
            modeloRangoFechas.addRow(fila);
        }
        lblTotalRango.setText(textoTotal);
    }

    public void actualizarTablaAnual(List<Object[]> filas, String textoTotal){
        modeloVentasAnuales.setRowCount(0);
        for(Object[] fila : filas){
            modeloVentasAnuales.addRow(fila);
        }
        lblTotalAnual.setText(textoTotal);
    }

    public void actualizarTablasTop(List<Object[]> topMas, List<Object[]> topMenos){
        modeloMasVendidos.setRowCount(0);
        for(Object[] fila : topMas){
            modeloMasVendidos.addRow(fila);
        }

        modeloMenosVendidos.setRowCount(0);
        for(Object[] fila : topMenos){
            modeloMenosVendidos.addRow(fila);
        }
    }

    //Muestra el subdialogo con los productos vendidos en un ticket
    public void mostrarDialogoDetalleVenta(int idVenta, String fechaStr, String totalStr, List<Object[]> detalles){
        JDialog dialogDetalle = new JDialog(this, "Detalles de Venta #" + idVenta, true);
        dialogDetalle.setSize(700, 410);
        dialogDetalle.setLocationRelativeTo(this);
        dialogDetalle.setLayout(new BorderLayout());
        dialogDetalle.getContentPane().setBackground(FONDO_CREMA);

        //Encabezado verde del subdialogo
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel lblInfoFecha = new JLabel("Ticket #" + idVenta + "  •  " + fechaStr);
        lblInfoFecha.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblInfoFecha.setForeground(Color.WHITE);

        JLabel lblInfoTotal = new JLabel(totalStr, SwingConstants.RIGHT);
        lblInfoTotal.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblInfoTotal.setForeground(PILDORA_STOCK_BG);

        panelHeader.add(lblInfoFecha, BorderLayout.WEST);
        panelHeader.add(lblInfoTotal, BorderLayout.EAST);
        dialogDetalle.add(panelHeader, BorderLayout.NORTH);

        //Tarjeta central con los articulos del ticket
        JPanel panelCentro = new JPanel(new BorderLayout(0, 12));
        panelCentro.setBackground(FONDO_CREMA);
        panelCentro.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));

        JPanel tarjetaDet = new JPanel(new BorderLayout());
        tarjetaDet.setBackground(Color.WHITE);
        tarjetaDet.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        tarjetaDet.putClientProperty("FlatLaf.style", "arc: 18");

        String[] cols = {"Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        DefaultTableModel modelDet = new DefaultTableModel(cols, 0){
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };
        for(Object[] d : detalles){
            modelDet.addRow(d);
        }

        JTable tablaDet = new JTable(modelDet);
        estilizarTabla(tablaDet, new int[]{SwingConstants.LEFT, SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.RIGHT});

        JScrollPane scrollDet = new JScrollPane(tablaDet);
        scrollDet.setBorder(null);
        scrollDet.getViewport().setBackground(Color.WHITE);
        tarjetaDet.add(scrollDet, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("✕  Cerrar");
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCerrar.setBackground(new Color(228, 232, 228));
        btnCerrar.setForeground(new Color(55, 60, 58));
        btnCerrar.setPreferredSize(new Dimension(115, 38));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.putClientProperty("FlatLaf.style", "arc: 16");
        btnCerrar.addActionListener(e -> dialogDetalle.dispose());

        JPanel panelSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelSouth.setOpaque(false);
        panelSouth.add(btnCerrar);

        panelCentro.add(tarjetaDet, BorderLayout.CENTER);
        panelCentro.add(panelSouth, BorderLayout.SOUTH);
        dialogDetalle.add(panelCentro, BorderLayout.CENTER);

        dialogDetalle.setVisible(true);
    }

    //Muestra ventanas emergentes de aviso o error
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    //Pide confirmacion al usuario antes de eliminar una venta
    public boolean confirmarAccion(String mensaje, String titulo){
        Object[] opciones = {"Sí", "No"};
        int opcion = JOptionPane.showOptionDialog(
            this, mensaje, titulo,
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
            null, opciones, opciones[1]
        );
        return opcion == 0;
    }

    //Registro de listeners desde el controlador
    public void agregarListeners(ActionListener onFiltrarFechas, ActionListener onVerDetalle,
                                 ActionListener onEliminarVenta, ActionListener onCambiarAnio){
        btnBuscarFechas.addActionListener(onFiltrarFechas);
        btnVerDetalle.addActionListener(onVerDetalle);
        btnEliminarVenta.addActionListener(onEliminarVenta);
        comboAnios.addActionListener(onCambiarAnio);
    }
}