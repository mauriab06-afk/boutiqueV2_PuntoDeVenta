package vista;

//Ventana moderna para consultar y reabastecer el inventario

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class VentanaInventario extends JDialog{
    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color PILDORA_STOCK_BG = new Color(214, 240, 221);
    private final Color PILDORA_STOCK_TXT = new Color(24, 118, 54);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    //Componentes de la interfaz
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    private JButton btnReabastecer;
    private JButton btnCerrar;

    //Constructor principal
    public VentanaInventario(Frame parent){
        super(parent, "Gestión e Inventario de Productos", true);
        configurarVentana();
        inicializarComponentes();
    }

    //Configura tamano, centrado y color de fondo
    private void configurarVentana(){
        setSize(900, 560);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO_CREMA);
    }

    //Arma el encabezado verde, la tarjeta con tabla y los botones inferiores
    private void inicializarComponentes(){
        //Barra superior verde bosque
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel lblTitulo = new JLabel("⊞  Control de Inventario y Existencias");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Consulta existencias y actualiza el stock disponible");
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

        //Contenedor central con margen sobre fondo crema
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 14));
        panelCuerpo.setBackground(FONDO_CREMA);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        //Tarjeta superior para la barra de busqueda
        JPanel tarjetaBusqueda = new JPanel(new BorderLayout(12, 0));
        tarjetaBusqueda.setBackground(Color.WHITE);
        tarjetaBusqueda.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        tarjetaBusqueda.putClientProperty("FlatLaf.style", "arc: 18");

        JLabel lblBuscar = new JLabel("⌕  Filtrar:");
        lblBuscar.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblBuscar.setForeground(new Color(55, 60, 58));

        txtBuscar = new JTextField();
        txtBuscar.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBuscar.setPreferredSize(new Dimension(0, 36));
        txtBuscar.putClientProperty("JTextField.placeholderText", "Escribe el nombre o categoría del producto...");
        txtBuscar.putClientProperty("FlatLaf.style", "arc: 14");

        tarjetaBusqueda.add(lblBuscar, BorderLayout.WEST);
        tarjetaBusqueda.add(txtBuscar, BorderLayout.CENTER);

        //Tarjeta central para la tabla de inventario
        JPanel tarjetaTabla = new JPanel(new BorderLayout());
        tarjetaTabla.setBackground(Color.WHITE);
        tarjetaTabla.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        tarjetaTabla.putClientProperty("FlatLaf.style", "arc: 20");

        String[] columnas = {"ID", "Producto", "Categoría", "Precio", "Stock Actual", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){ return false; }
        };

        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaInventario.setRowHeight(34);
        tablaInventario.setShowVerticalLines(false);
        tablaInventario.setGridColor(new Color(235, 238, 235));
        tablaInventario.setSelectionBackground(new Color(225, 245, 230));
        tablaInventario.setSelectionForeground(new Color(20, 70, 35));

        tablaInventario.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaInventario.getTableHeader().setBackground(new Color(245, 247, 245));
        tablaInventario.getTableHeader().setForeground(new Color(45, 50, 48));
        tablaInventario.getTableHeader().setPreferredSize(new Dimension(0, 38));

        //Ajuste de ancho de columnas
        tablaInventario.getColumnModel().getColumn(0).setMaxWidth(65);
        tablaInventario.getColumnModel().getColumn(3).setPreferredWidth(100);
        tablaInventario.getColumnModel().getColumn(4).setPreferredWidth(110);
        tablaInventario.getColumnModel().getColumn(5).setPreferredWidth(130);

        //Renderizador personalizado para colores de estado y alineacion
        tablaInventario.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column){
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                //Alineacion segun el tipo de dato de la columna
                if(column == 0 || column == 4 || column == 5){
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                }else if(column == 3){
                    c.setHorizontalAlignment(SwingConstants.RIGHT);
                }else{
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                }

                String estado = (String) table.getValueAt(row, 5);

                if(!isSelected){
                    c.setBackground(Color.WHITE);
                    c.setForeground(new Color(40, 44, 42));
                    c.setFont(new Font("SansSerif", Font.PLAIN, 13));

                    if(column == 5){
                        c.setFont(new Font("SansSerif", Font.BOLD, 12));
                        if("AGOTADO".equals(estado)){
                            c.setBackground(new Color(253, 232, 234));
                            c.setForeground(ROJO_ALERTA);
                        }else if("STOCK BAJO".equals(estado)){
                            c.setBackground(new Color(255, 244, 214));
                            c.setForeground(new Color(165, 110, 0));
                        }else{
                            c.setBackground(PILDORA_STOCK_BG);
                            c.setForeground(PILDORA_STOCK_TXT);
                        }
                    }else if("AGOTADO".equals(estado)){
                        c.setBackground(new Color(255, 248, 248));
                    }
                }
                return c;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaInventario);
        scrollTabla.setBorder(null);
        scrollTabla.getViewport().setBackground(Color.WHITE);
        tarjetaTabla.add(scrollTabla, BorderLayout.CENTER);

        //Panel inferior con botones redondeados
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panelInferior.setOpaque(false);

        btnReabastecer = new JButton("+  Reabastecer / Ajustar Stock");
        btnReabastecer.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnReabastecer.setBackground(VERDE_PRIMARIO);
        btnReabastecer.setForeground(Color.WHITE);
        btnReabastecer.setPreferredSize(new Dimension(245, 42));
        btnReabastecer.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReabastecer.putClientProperty("FlatLaf.style", "arc: 18");

        btnCerrar = new JButton("✕  Cerrar");
        btnCerrar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCerrar.setBackground(new Color(228, 232, 228));
        btnCerrar.setForeground(new Color(55, 60, 58));
        btnCerrar.setPreferredSize(new Dimension(115, 42));
        btnCerrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrar.putClientProperty("FlatLaf.style", "arc: 18");

        panelInferior.add(btnReabastecer);
        panelInferior.add(btnCerrar);

        panelCuerpo.add(tarjetaBusqueda, BorderLayout.NORTH);
        panelCuerpo.add(tarjetaTabla, BorderLayout.CENTER);
        panelCuerpo.add(panelInferior, BorderLayout.SOUTH);

        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Getters para que el controlador lea la vista
    public String getFiltroBusqueda(){ return txtBuscar.getText().trim(); }
    public int getFilaSeleccionada(){ return tablaInventario.getSelectedRow(); }
    public Object getValorCelda(int fila, int columna){ return tablaInventario.getValueAt(fila, columna); }

    //Llena la tabla con los registros que envia el controlador
    public void actualizarTabla(List<Object[]> filas){
        modeloTabla.setRowCount(0);
        for(Object[] fila : filas){
            modeloTabla.addRow(fila);
        }
    }

    //Abre el cuadro para capturar la nueva cantidad de inventario
    public String solicitarCantidadStock(String nombreProducto, int stockActual){
        return JOptionPane.showInputDialog(
            this,
            "Producto: " + nombreProducto + "\nStock actual: " + stockActual + "\n\nIngrese la nueva cantidad total en inventario:",
            "Reabastecer Inventario",
            JOptionPane.QUESTION_MESSAGE
        );
    }

    //Muestra ventanas emergentes de aviso o error
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    //Conecta los eventos de la ventana con el controlador
    public void agregarListeners(KeyListener onBuscar, ActionListener onReabastecer, ActionListener onCerrar){
        txtBuscar.addKeyListener(onBuscar);
        btnReabastecer.addActionListener(onReabastecer);
        btnCerrar.addActionListener(onCerrar);
    }
}