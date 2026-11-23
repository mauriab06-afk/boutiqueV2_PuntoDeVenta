package vista;

//Ventana moderna para administrar los usuarios del sistema

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class VentanaUsuarios extends JDialog{
    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color PILDORA_STOCK_BG = new Color(214, 240, 221);
    private final Color PILDORA_STOCK_TXT = new Color(24, 118, 54);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    //Elementos de la tabla
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    //Campos del formulario para capturar datos
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private JCheckBox chkActivo;

    //Botones de accion
    private JButton btnGuardar;
    private JButton btnEstado;
    private JButton btnLimpiar;

    //Guarda el id del usuario elegido en la tabla
    private int idUsuarioSeleccionado = -1;

    //Constructor que arma la ventana
    public VentanaUsuarios(Frame owner){
        super(owner, "Gestión de Usuarios del Sistema", true);
        configurarVentana();
        inicializarComponentes();
    }

    //Configura el tamano, centrado y color de fondo
    private void configurarVentana(){
        setSize(880, 540);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO_CREMA);
    }

    //Crea y acomoda el encabezado, la tarjeta de tabla y el formulario
    private void inicializarComponentes(){
        //Barra superior verde bosque
        JPanel panelHeader = new JPanel(new BorderLayout(15, 0));
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel lblTitulo = new JLabel("☺  Administración de Cuentas y Accesos");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Crea, edita y controla los permisos del personal");
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

        //Tarjeta izquierda con la tabla de usuarios
        JPanel tarjetaTabla = new JPanel(new BorderLayout(0, 10));
        tarjetaTabla.setBackground(Color.WHITE);
        tarjetaTabla.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        tarjetaTabla.putClientProperty("FlatLaf.style", "arc: 20");

        JLabel lblSubTabla = new JLabel("Usuarios Registrados");
        lblSubTabla.setFont(new Font("SansSerif", Font.BOLD, 16));
        lblSubTabla.setForeground(new Color(35, 40, 38));

        String[] columnas = {"ID", "Usuario", "Contraseña", "Rol", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tablaUsuarios.setRowHeight(34);
        tablaUsuarios.setShowVerticalLines(false);
        tablaUsuarios.setGridColor(new Color(235, 238, 235));
        tablaUsuarios.setSelectionBackground(new Color(225, 245, 230));
        tablaUsuarios.setSelectionForeground(new Color(20, 70, 35));

        tablaUsuarios.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        tablaUsuarios.getTableHeader().setBackground(new Color(245, 247, 245));
        tablaUsuarios.getTableHeader().setForeground(new Color(45, 50, 48));
        tablaUsuarios.getTableHeader().setPreferredSize(new Dimension(0, 36));

        tablaUsuarios.getColumnModel().getColumn(0).setMaxWidth(55);
        tablaUsuarios.getColumnModel().getColumn(3).setPreferredWidth(120);
        tablaUsuarios.getColumnModel().getColumn(4).setPreferredWidth(115);

        //Renderizador para ocultar visualmente la contrasena y colorear el estado
        tablaUsuarios.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column){
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

                if(column == 2 && value != null){
                    c.setText("••••••••");
                }

                if(column == 0 || column == 4){
                    c.setHorizontalAlignment(SwingConstants.CENTER);
                }else{
                    c.setHorizontalAlignment(SwingConstants.LEFT);
                }

                String estado = (String) table.getValueAt(row, 4);
                if(!isSelected){
                    c.setBackground(Color.WHITE);
                    c.setForeground(new Color(40, 44, 42));
                    c.setFont(new Font("SansSerif", Font.PLAIN, 13));

                    if(column == 4){
                        c.setFont(new Font("SansSerif", Font.BOLD, 12));
                        if("Habilitado".equals(estado)){
                            c.setBackground(PILDORA_STOCK_BG);
                            c.setForeground(PILDORA_STOCK_TXT);
                        }else{
                            c.setBackground(new Color(253, 232, 234));
                            c.setForeground(ROJO_ALERTA);
                        }
                    }
                }
                return c;
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);
        scrollTabla.setBorder(null);
        scrollTabla.getViewport().setBackground(Color.WHITE);

        tarjetaTabla.add(lblSubTabla, BorderLayout.NORTH);
        tarjetaTabla.add(scrollTabla, BorderLayout.CENTER);

        //Tarjeta derecha con el formulario de usuario
        JPanel tarjetaForm = new JPanel(new GridBagLayout());
        tarjetaForm.setBackground(Color.WHITE);
        tarjetaForm.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        tarjetaForm.setPreferredSize(new Dimension(330, 0));
        tarjetaForm.putClientProperty("FlatLaf.style", "arc: 20");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        JLabel lblSubForm = new JLabel("Detalle del Usuario");
        lblSubForm.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblSubForm.setForeground(VERDE_HEADER);

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUser.setForeground(new Color(65, 70, 68));

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(0, 36));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Nombre de acceso");
        txtUsuario.putClientProperty("FlatLaf.style", "arc: 12");

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPass.setForeground(new Color(65, 70, 68));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 36));
        txtPassword.putClientProperty("JTextField.placeholderText", "••••••••");
        txtPassword.putClientProperty("JTextField.showRevealButton", true);
        txtPassword.putClientProperty("FlatLaf.style", "arc: 12");

        JLabel lblRol = new JLabel("Rol del Sistema");
        lblRol.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblRol.setForeground(new Color(65, 70, 68));

        comboRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "CAJERO"});
        comboRol.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboRol.setPreferredSize(new Dimension(0, 36));
        comboRol.putClientProperty("FlatLaf.style", "arc: 12");

        chkActivo = new JCheckBox("Cuenta Activa", true);
        chkActivo.setFont(new Font("SansSerif", Font.BOLD, 13));
        chkActivo.setForeground(new Color(50, 55, 52));
        chkActivo.setOpaque(false);

        //Botones redondeados del formulario
        btnGuardar = new JButton("✓  Guardar / Actualizar");
        btnGuardar.setBackground(VERDE_PRIMARIO);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnGuardar.setPreferredSize(new Dimension(0, 40));
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.putClientProperty("FlatLaf.style", "arc: 16");

        btnEstado = new JButton("⇄  Alternar Activo/Inactivo");
        btnEstado.setBackground(ROJO_ALERTA);
        btnEstado.setForeground(Color.WHITE);
        btnEstado.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnEstado.setPreferredSize(new Dimension(0, 40));
        btnEstado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEstado.putClientProperty("FlatLaf.style", "arc: 16");

        btnLimpiar = new JButton("✕  Nuevo / Limpiar");
        btnLimpiar.setBackground(new Color(232, 235, 232));
        btnLimpiar.setForeground(new Color(55, 60, 58));
        btnLimpiar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnLimpiar.setPreferredSize(new Dimension(0, 38));
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.putClientProperty("FlatLaf.style", "arc: 16");

        //Acomodo vertical en el formulario
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 14, 0);
        tarjetaForm.add(lblSubForm, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        tarjetaForm.add(lblUser, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 12, 0);
        tarjetaForm.add(txtUsuario, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 4, 0);
        tarjetaForm.add(lblPass, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 12, 0);
        tarjetaForm.add(txtPassword, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        tarjetaForm.add(lblRol, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 10, 0);
        tarjetaForm.add(comboRol, gbc);

        gbc.gridy = 7; gbc.insets = new Insets(0, 0, 16, 0);
        tarjetaForm.add(chkActivo, gbc);

        gbc.gridy = 8; gbc.insets = new Insets(0, 0, 8, 0);
        tarjetaForm.add(btnGuardar, gbc);

        gbc.gridy = 9; gbc.insets = new Insets(0, 0, 8, 0);
        tarjetaForm.add(btnEstado, gbc);

        gbc.gridy = 10; gbc.insets = new Insets(0, 0, 0, 0);
        tarjetaForm.add(btnLimpiar, gbc);

        panelCuerpo.add(tarjetaTabla, BorderLayout.CENTER);
        panelCuerpo.add(tarjetaForm, BorderLayout.EAST);

        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Metodos para que el controlador lea lo que hay en pantalla
    public int getIdUsuarioSeleccionado(){ return idUsuarioSeleccionado; }
    public String getUsuarioTexto(){ return txtUsuario.getText().trim(); }
    public String getPasswordTexto(){ return new String(txtPassword.getPassword()).trim(); }
    public String getRolSeleccionado(){ return (String) comboRol.getSelectedItem(); }
    public boolean isActivoSeleccionado(){ return chkActivo.isSelected(); }
    public int getFilaSeleccionada(){ return tablaUsuarios.getSelectedRow(); }
    public Object getValorCelda(int fila, int columna){ return modeloTabla.getValueAt(fila, columna); }

    //Llena la tabla con las filas que manda el controlador
    public void actualizarTabla(List<Object[]> filas){
        modeloTabla.setRowCount(0);
        for(Object[] fila : filas){
            modeloTabla.addRow(fila);
        }
    }

    //Pasa los datos de la fila seleccionada hacia los campos del formulario
    public void cargarFormularioDesdeSeleccion(){
        int fila = tablaUsuarios.getSelectedRow();
        if(fila != -1){
            idUsuarioSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtUsuario.setText((String) modeloTabla.getValueAt(fila, 1));
            txtPassword.setText((String) modeloTabla.getValueAt(fila, 2));
            comboRol.setSelectedItem(modeloTabla.getValueAt(fila, 3));
            chkActivo.setSelected("Habilitado".equals(modeloTabla.getValueAt(fila, 4)));
        }
    }

    //Borra los textos de las cajas y quita la seleccion de la tabla
    public void limpiarFormulario(){
        idUsuarioSeleccionado = -1;
        txtUsuario.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tablaUsuarios.clearSelection();
    }

    //Muestra ventanas emergentes de aviso o error
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    //Conecta los botones y la tabla con las acciones del controlador
    public void agregarListeners(ActionListener onGuardar, ActionListener onCambiarEstado,
                                 ActionListener onLimpiar, ListSelectionListener onSeleccion){
        btnGuardar.addActionListener(onGuardar);
        btnEstado.addActionListener(onCambiarEstado);
        btnLimpiar.addActionListener(onLimpiar);
        tablaUsuarios.getSelectionModel().addListSelectionListener(onSeleccion);
    }
}