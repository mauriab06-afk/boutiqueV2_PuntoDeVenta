package vista;

//Interfaz de manejo de usuarios

import controlador.UsuarioControlador;
import modelo.Usuario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaUsuarios extends JDialog{
    private final UsuarioControlador controlador; //Conexion y manejo de datos

    private JTable tablaUsuarios;
    private DefaultTableModel modeloTabla;

    //Captura de datos
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    private JCheckBox chkActivo;

    private int idUsuarioSeleccionado = -1;

    //Constructor
    public VentanaUsuarios(Frame owner){
        super(owner, "Gestión de Usuarios del Sistema", true);
        this.controlador = new UsuarioControlador(this);
        configurarVentana();
        inicializarComponentes();
        cargarTablaUsuarios();
    }

    //Configuracion de ventana
    private void configurarVentana(){
        setSize(750, 480);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes(){
        //Encabezado
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(45, 45, 45));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblTitulo = new JLabel("👥 Administración de Cuentas y Accesos");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo);

        add(panelHeader, BorderLayout.NORTH);

        // Tabla de usuarios
        String[] columnas = {"ID", "Usuario", "Contraseña", "Rol", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaUsuarios = new JTable(modeloTabla);
        tablaUsuarios.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaUsuarios.setRowHeight(24);
        tablaUsuarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        //Seleccionar tabla
        tablaUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaUsuarios.getSelectedRow() != -1) {
                cargarFormularioDesdeSeleccion();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Usuarios Registrados"));
        add(scrollTabla, BorderLayout.CENTER);

        //Formulario
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Detalle del Usuario"));
        panelForm.setPreferredSize(new Dimension(300, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);

        JLabel lblUser = new JLabel("Usuario:");
        txtUsuario = new JTextField(12);

        JLabel lblPass = new JLabel("Contraseña:");
        txtPassword = new JPasswordField(12);

        JLabel lblRol = new JLabel("Rol:");
        comboRol = new JComboBox<>(new String[]{"ADMINISTRADOR", "CAJERO"});

        chkActivo = new JCheckBox("Cuenta Activa", true);

        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(lblUser, gbc);
        gbc.gridx = 1; panelForm.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(lblPass, gbc);
        gbc.gridx = 1; panelForm.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(lblRol, gbc);
        gbc.gridx = 1; panelForm.add(comboRol, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        panelForm.add(chkActivo, gbc);

        //Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton btnGuardar = new JButton("💾 Guardar / Actualizar");
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 12));
        btnGuardar.addActionListener(e -> procesarGuardar());

        JButton btnEstado = new JButton("🔄 Alternar Activo/Inactivo");
        btnEstado.setBackground(new Color(220, 53, 69));
        btnEstado.setForeground(Color.WHITE);
        btnEstado.setFont(new Font("Arial", Font.BOLD, 12));
        btnEstado.addActionListener(e -> procesarCambioEstado());

        JButton btnLimpiar = new JButton("🧹 Nuevo / Limpiar");
        btnLimpiar.setFont(new Font("Arial", Font.PLAIN, 12));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        panelBotones.add(btnGuardar);
        panelBotones.add(btnEstado);
        panelBotones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        panelForm.add(panelBotones, gbc);

        add(panelForm, BorderLayout.EAST);
    }

    //Tomar los datos y colocarlos en la tabla
    public void cargarTablaUsuarios(){
        modeloTabla.setRowCount(0);
        for(Usuario u : controlador.obtenerUsuarios()){
            modeloTabla.addRow(new Object[]{
                u.getId(),
                u.getUsuario(),
                u.getPassword(),
                u.getRol(),
                u.isActivo() ? "Habilitado" : "Deshabilitado"
            });
        }
    }

    //Toma los datos del formulario
    private void cargarFormularioDesdeSeleccion(){
        int fila = tablaUsuarios.getSelectedRow();
        if(fila != -1){
            idUsuarioSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtUsuario.setText((String) modeloTabla.getValueAt(fila, 1));
            txtPassword.setText((String) modeloTabla.getValueAt(fila, 2));
            comboRol.setSelectedItem(modeloTabla.getValueAt(fila, 3));
            chkActivo.setSelected("Habilitado".equals(modeloTabla.getValueAt(fila, 4)));
        }
    }

    //Enviar informacion al controlador
    private void procesarGuardar(){
        String u = txtUsuario.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();
        String r = (String) comboRol.getSelectedItem();
        boolean act = chkActivo.isSelected();

        controlador.guardarUsuario(idUsuarioSeleccionado, u, p, r, act);
    }

    //Alternar estado
    private void procesarCambioEstado(){
        int fila = tablaUsuarios.getSelectedRow();
        if(fila == -1){
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabla.getValueAt(fila, 0);
        String u = (String) modeloTabla.getValueAt(fila, 1);
        String p = (String) modeloTabla.getValueAt(fila, 2);
        String r = (String) modeloTabla.getValueAt(fila, 3);
        boolean estadoActual = "Habilitado".equals(modeloTabla.getValueAt(fila, 4));

        Usuario usr = new Usuario(id, u, p, r, estadoActual);
        controlador.cambiarEstadoUsuario(usr, !estadoActual);
    }

    //Limpiar formulario
    public void limpiarFormulario(){
        idUsuarioSeleccionado = -1;
        txtUsuario.setText("");
        txtPassword.setText("");
        comboRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tablaUsuarios.clearSelection();
    }
}