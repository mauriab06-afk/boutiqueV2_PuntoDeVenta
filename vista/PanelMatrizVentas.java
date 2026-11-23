package vista;

//Panel moderno para visualizar la matriz y grafica de ventas por periodo

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.MatrizVentas.ProductoFila;

public final class PanelMatrizVentas extends JPanel{
    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color PILDORA_STOCK_BG = new Color(214, 240, 221);
    private final Color PILDORA_STOCK_TXT = new Color(24, 118, 54);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    //Componentes de control y visualizacion
    private final JTextField txtFecha = new JTextField(LocalDate.now().toString(), 11);
    private final JComboBox<String> cmbPeriodo = new JComboBox<>(new String[]{
        "Semana", "Mes", "Restar semana anterior", "Sumar semana anterior"
    });
    private final JButton btnCalcular = new JButton("↗  Calcular Matriz");
    private final JTable tabla = new JTable();
    private final GraficaVentas grafica = new GraficaVentas();
    private final JLabel lblRango = new JLabel(" ");
    private final JLabel lblResultado = new JLabel(" ");

    //Constructor principal
    public PanelMatrizVentas(){
        super(new BorderLayout());
        setBackground(FONDO_CREMA);
        inicializarComponentes();
    }

    //Arma el encabezado verde, los filtros, la grafica y la tabla
    private void inicializarComponentes(){
        //Barra superior verde bosque
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel lblTitulo = new JLabel("↗  Matriz y Comparativa de Ventas");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Analiza unidades vendidas por producto, dia y comparativas semanales");
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
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 14));
        panelCuerpo.setBackground(FONDO_CREMA);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(16, 22, 16, 22));

        //Tarjeta superior de controles y rango calculado
        JPanel tarjetaControles = new JPanel(new BorderLayout(10, 8));
        tarjetaControles.setBackground(Color.WHITE);
        tarjetaControles.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        tarjetaControles.putClientProperty("FlatLaf.style", "arc: 18");

        JPanel filaControles = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filaControles.setOpaque(false);

        JLabel lblFecha = new JLabel("Fecha referencia (AAAA-MM-DD):");
        lblFecha.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblFecha.setForeground(new Color(55, 60, 58));

        txtFecha.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtFecha.setPreferredSize(new Dimension(125, 34));
        txtFecha.putClientProperty("FlatLaf.style", "arc: 12");

        cmbPeriodo.setFont(new Font("SansSerif", Font.BOLD, 13));
        cmbPeriodo.setPreferredSize(new Dimension(215, 34));
        cmbPeriodo.putClientProperty("FlatLaf.style", "arc: 12");

        btnCalcular.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCalcular.setBackground(VERDE_PRIMARIO);
        btnCalcular.setForeground(Color.WHITE);
        btnCalcular.setPreferredSize(new Dimension(165, 36));
        btnCalcular.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalcular.putClientProperty("FlatLaf.style", "arc: 14");

        filaControles.add(lblFecha);
        filaControles.add(txtFecha);
        filaControles.add(cmbPeriodo);
        filaControles.add(btnCalcular);

        lblRango.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblRango.setForeground(VERDE_HEADER);
        lblRango.setBorder(BorderFactory.createEmptyBorder(2, 6, 0, 0));

        tarjetaControles.add(filaControles, BorderLayout.NORTH);
        tarjetaControles.add(lblRango, BorderLayout.SOUTH);

        panelCuerpo.add(tarjetaControles, BorderLayout.NORTH);

        //Tarjeta para la grafica de barras
        JPanel tarjetaGrafica = new JPanel(new BorderLayout(0, 8));
        tarjetaGrafica.setBackground(Color.WHITE);
        tarjetaGrafica.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        tarjetaGrafica.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblTituloGrafica = new JLabel("▤  Totales por Producto (Unidades)");
        lblTituloGrafica.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloGrafica.setForeground(new Color(35, 40, 38));

        grafica.setBackground(Color.WHITE);
        JScrollPane scrollGrafica = new JScrollPane(grafica);
        scrollGrafica.setBorder(null);
        scrollGrafica.getViewport().setBackground(Color.WHITE);
        scrollGrafica.setPreferredSize(new Dimension(420, 220));
        scrollGrafica.getVerticalScrollBar().setUnitIncrement(28);

        tarjetaGrafica.add(lblTituloGrafica, BorderLayout.NORTH);
        tarjetaGrafica.add(scrollGrafica, BorderLayout.CENTER);

        //Tarjeta para la tabla matriz
        JPanel tarjetaTabla = new JPanel(new BorderLayout(0, 8));
        tarjetaTabla.setBackground(Color.WHITE);
        tarjetaTabla.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        tarjetaTabla.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblTituloTabla = new JLabel("⊞  Matriz Detallada por Producto y Dia");
        lblTituloTabla.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTituloTabla.setForeground(new Color(35, 40, 38));

        estilizarTabla();

        JScrollPane scrollTabla = new JScrollPane(tabla);
        scrollTabla.setBorder(null);
        scrollTabla.getViewport().setBackground(Color.WHITE);

        tarjetaTabla.add(lblTituloTabla, BorderLayout.NORTH);
        tarjetaTabla.add(scrollTabla, BorderLayout.CENTER);

        //Division vertical limpia entre grafica y tabla
        JSplitPane resultados = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tarjetaGrafica, tarjetaTabla);
        resultados.setResizeWeight(0.50);
        resultados.setDividerSize(10);
        resultados.setBorder(null);
        resultados.setBackground(FONDO_CREMA);

        panelCuerpo.add(resultados, BorderLayout.CENTER);

        //Tarjeta inferior de resumen o mensaje de estado
        JPanel tarjetaPie = new JPanel(new BorderLayout());
        tarjetaPie.setBackground(PILDORA_STOCK_BG);
        tarjetaPie.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        tarjetaPie.putClientProperty("FlatLaf.style", "arc: 14");

        lblResultado.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblResultado.setForeground(PILDORA_STOCK_TXT);
        tarjetaPie.add(lblResultado, BorderLayout.CENTER);

        panelCuerpo.add(tarjetaPie, BorderLayout.SOUTH);
        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Configura el estilo visual de la tabla matriz
    private void estilizarTabla(){
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabla.setFillsViewportHeight(true);
        tabla.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabla.setRowHeight(32);
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

                if(column == 0){
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                    c.setFont(new Font("SansSerif", Font.BOLD, 13));
                }else{
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                    c.setFont(new Font("SansSerif", Font.PLAIN, 13));
                }

                if(!isSelected){
                    c.setBackground(Color.WHITE);
                    c.setForeground(new Color(40, 44, 42));
                }
                return c;
            }
        });
    }

    //Metodos para que el controlador lea los controles
    public String getFechaTexto(){ return txtFecha.getText().trim(); }
    public int getIndicePeriodo(){ return cmbPeriodo.getSelectedIndex(); }

    //Conecta los eventos de calculo con el controlador
    public void agregarListenerActualizar(ActionListener listener){
        btnCalcular.addActionListener(listener);
        txtFecha.addActionListener(listener);
        cmbPeriodo.addActionListener(listener);
    }

    //Actualiza la grafica, la tabla y los textos de resumen
    public void mostrarDatos(String textoRango, String textoResultado, String[] columnas,
                             Object[][] filas, List<ProductoFila> productos, long[] totales){
        lblRango.setText(textoRango);
        lblResultado.setForeground(PILDORA_STOCK_TXT);
        lblResultado.setText(textoResultado);

        DefaultTableModel modelo = new DefaultTableModel(filas, columnas){
            @Override
            public boolean isCellEditable(int fila, int columna){
                return false;
            }
        };
        tabla.setModel(modelo);
        if(columnas.length > 0){
            tabla.getColumnModel().getColumn(0).setPreferredWidth(210);
            for(int i = 1; i < columnas.length; i++){
                tabla.getColumnModel().getColumn(i).setPreferredWidth(85);
            }
        }
        grafica.mostrar(productos, totales);
    }

    //Limpia los datos y muestra el aviso de error en rojo
    public void mostrarError(String mensaje){
        grafica.mostrar(List.of(), new long[0]);
        tabla.setModel(new DefaultTableModel());
        lblRango.setText(" ");
        lblResultado.setForeground(ROJO_ALERTA);
        lblResultado.setText("✕  " + mensaje);
    }
}