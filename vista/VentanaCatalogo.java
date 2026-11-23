package vista;

//Ventana moderna para administrar el catalogo de productos

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import modelo.Producto;

public class VentanaCatalogo extends JDialog{
    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    //Componentes de tabla y formulario
    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtCategoria;

    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;

    //Constructor principal
    public VentanaCatalogo(Frame parent){
        super(parent, "Gestión de Catálogo de Productos", true);
        configurarVentana();
        inicializarComponentes();
    }

    //Configura tamano, centrado y color de fondo
    private void configurarVentana(){
        setSize(1020, 620);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO_CREMA);
        setResizable(true);
    }

    //Arma el encabezado verde, el formulario izquierdo y la tabla derecha
    private void inicializarComponentes(){
        //Barra superior verde bosque
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel lblTitulo = new JLabel("☰  Administración de Catálogo de Productos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Agrega, modifica o elimina productos y precios del menú");
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
        JPanel panelCuerpo = new JPanel(new BorderLayout(18, 0));
        panelCuerpo.setBackground(FONDO_CREMA);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        //Tarjeta izquierda con el formulario del producto
        JPanel tarjetaFormulario = new JPanel(new GridBagLayout());
        tarjetaFormulario.setBackground(Color.WHITE);
        tarjetaFormulario.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        tarjetaFormulario.setPreferredSize(new Dimension(350, 0));
        tarjetaFormulario.putClientProperty("FlatLaf.style", "arc: 20");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblSubForm = new JLabel("Datos del Producto");
        lblSubForm.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblSubForm.setForeground(VERDE_HEADER);

        //Campos de captura estilizados
        txtId = crearCampoTexto("Automático");
        txtId.setEditable(false);
        txtId.setBackground(new Color(245, 247, 245));

        txtNombre = crearCampoTexto("Ej. Sopes con nopal (10 pzas)");
        txtPrecio = crearCampoTexto("Ej. 30.00");
        txtCategoria = crearCampoTexto("Ej. Antojitos");

        //Botones de accion redondeados
        JPanel filaSuperBotones = new JPanel(new GridLayout(1, 2, 10, 0));
        filaSuperBotones.setOpaque(false);

        btnGuardar = new JButton("✓  Guardar");
        btnGuardar.setBackground(VERDE_PRIMARIO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnGuardar.setPreferredSize(new Dimension(0, 40));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.putClientProperty("FlatLaf.style", "arc: 16");

        btnNuevo = new JButton("+  Nuevo");
        btnNuevo.setBackground(new Color(232, 235, 232));
        btnNuevo.setForeground(new Color(55, 60, 58));
        btnNuevo.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnNuevo.setPreferredSize(new Dimension(0, 40));
        btnNuevo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevo.putClientProperty("FlatLaf.style", "arc: 16");

        filaSuperBotones.add(btnGuardar);
        filaSuperBotones.add(btnNuevo);

        btnEliminar = new JButton("✕  Eliminar Producto");
        btnEliminar.setBackground(ROJO_ALERTA);
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnEliminar.setPreferredSize(new Dimension(0, 40));
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEliminar.putClientProperty("FlatLaf.style", "arc: 16");

        int row = 0;
        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 14, 0);
        tarjetaFormulario.add(lblSubForm, gbc);

        row = agregarCampoVertical(tarjetaFormulario, gbc, "ID del Producto", txtId, row);
        row = agregarCampoVertical(tarjetaFormulario, gbc, "Nombre del Producto", txtNombre, row);
        row = agregarCampoVertical(tarjetaFormulario, gbc, "Precio ($)", txtPrecio, row);
        row = agregarCampoVertical(tarjetaFormulario, gbc, "Categoría", txtCategoria, row);

        gbc.gridy = row++; gbc.insets = new Insets(10, 0, 8, 0);
        tarjetaFormulario.add(filaSuperBotones, gbc);

        gbc.gridy = row; gbc.insets = new Insets(0, 0, 0, 0);
        tarjetaFormulario.add(btnEliminar, gbc);

        panelCuerpo.add(tarjetaFormulario, BorderLayout.WEST);

        //Tarjeta derecha con la tabla del catalogo
        JPanel tarjetaTabla = new JPanel(new BorderLayout(0, 10));
        tarjetaTabla.setBackground(Color.WHITE);
        tarjetaTabla.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjetaTabla.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblSubTabla = new JLabel("Productos en Catálogo");
        lblSubTabla.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSubTabla.setForeground(new Color(35, 40, 38));

        String[] columnas = {"ID", "Nombre", "Precio", "Categoría"};
        modeloTablaProductos = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int r, int c){ return false; }
        };

        tablaProductos = new JTable(modeloTablaProductos);
        tablaProductos.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaProductos.setRowHeight(34);
        tablaProductos.setShowVerticalLines(false);
        tablaProductos.setGridColor(new Color(235, 238, 235));
        tablaProductos.setSelectionBackground(new Color(225, 245, 230));
        tablaProductos.setSelectionForeground(new Color(20, 70, 35));

        tablaProductos.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaProductos.getTableHeader().setBackground(new Color(245, 247, 245));
        tablaProductos.getTableHeader().setForeground(new Color(45, 50, 48));
        tablaProductos.getTableHeader().setPreferredSize(new Dimension(0, 36));

        tablaProductos.getColumnModel().getColumn(0).setMaxWidth(65);
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(290);
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(150);

        //Renderizador para alinear columnas y resaltar precios
        tablaProductos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int r, int c){
                JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, r, c);
                cell.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

                if(c == 0){
                    cell.setHorizontalAlignment(SwingConstants.CENTER);
                }else if(c == 2){
                    cell.setHorizontalAlignment(SwingConstants.RIGHT);
                }else{
                    cell.setHorizontalAlignment(SwingConstants.LEFT);
                }

                if(!isSelected){
                    cell.setBackground(Color.WHITE);
                    if(c == 2){
                        cell.setFont(new Font("SansSerif", Font.BOLD, 13));
                        cell.setForeground(VERDE_PRIMARIO);
                    }else{
                        cell.setFont(new Font("SansSerif", Font.PLAIN, 13));
                        cell.setForeground(new Color(40, 44, 42));
                    }
                }
                return cell;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(null);
        scrollTabla.getViewport().setBackground(Color.WHITE);

        tarjetaTabla.add(lblSubTabla, BorderLayout.NORTH);
        tarjetaTabla.add(scrollTabla, BorderLayout.CENTER);

        panelCuerpo.add(tarjetaTabla, BorderLayout.CENTER);
        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Crea una caja de texto con bordes redondeados y placeholder
    private JTextField crearCampoTexto(String placeholder){
        JTextField campo = new JTextField();
        campo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        campo.setPreferredSize(new Dimension(0, 36));
        campo.putClientProperty("JTextField.placeholderText", placeholder);
        campo.putClientProperty("FlatLaf.style", "arc: 12");
        return campo;
    }

    //Agrega una etiqueta superior y su caja de texto en el formulario
    private int agregarCampoVertical(JPanel panel, GridBagConstraints gbc, String etiqueta, JComponent campo, int row){
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 13));
        lbl.setForeground(new Color(65, 70, 68));

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 4, 0);
        panel.add(lbl, gbc);

        gbc.gridy = row++; gbc.insets = new Insets(0, 0, 11, 0);
        panel.add(campo, gbc);

        return row;
    }

    //Getters para que el controlador lea la vista
    public String getIdTexto(){ return txtId.getText().trim(); }
    public String getNombreTexto(){ return txtNombre.getText().trim(); }
    public String getPrecioTexto(){ return txtPrecio.getText().trim(); }
    public String getCategoriaTexto(){ return txtCategoria.getText().trim(); }
    public int getFilaSeleccionada(){ return tablaProductos.getSelectedRow(); }

    //Metodos para actualizar la interfaz
    public void actualizarTabla(List<Producto> productos){
        modeloTablaProductos.setRowCount(0);
        for(Producto p : productos){
            modeloTablaProductos.addRow(new Object[]{
                p.getId(),
                p.getNombre(),
                String.format("$%.2f", p.getPrecio()),
                p.getCategoria()
            });
        }
    }

    //Carga los datos de la fila seleccionada en las cajas de texto
    public void cargarProductoSeleccionado(int filaIndex){
        if(filaIndex < 0) return;
        txtId.setText(tablaProductos.getValueAt(filaIndex, 0).toString());
        txtNombre.setText(tablaProductos.getValueAt(filaIndex, 1).toString());
        txtPrecio.setText(tablaProductos.getValueAt(filaIndex, 2).toString().replace("$", ""));
        txtCategoria.setText(tablaProductos.getValueAt(filaIndex, 3).toString());
    }

    //Limpia el formulario y quita la seleccion de la tabla
    public void limpiarFormulario(){
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCategoria.setText("");
        tablaProductos.clearSelection();
    }

    //Muestra ventanas emergentes de aviso o error
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    //Pide confirmacion antes de eliminar un producto
    public boolean confirmarAccion(String mensaje, String titulo){
        Object[] opciones = {"Sí", "No"};
        int opcion = JOptionPane.showOptionDialog(
            this, mensaje, titulo,
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
            null, opciones, opciones[0]
        );
        return opcion == 0;
    }

    //Registro de listeners desde el controlador
    public void agregarListeners(ActionListener onGuardar, ActionListener onNuevo,
                                 ActionListener onEliminar, ListSelectionListener onSeleccionTabla){
        btnGuardar.addActionListener(onGuardar);
        btnNuevo.addActionListener(onNuevo);
        btnEliminar.addActionListener(onEliminar);
        tablaProductos.getSelectionModel().addListSelectionListener(onSeleccionTabla);
    }
}