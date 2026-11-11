package vista;

//Interfaz para la ventana del login

import controlador.GestionBackup;
import controlador.LoginControlador;
import modelo.ConexionBD;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame{
    private JTextField txtUsuario; //Campos de texto
    private JPasswordField txtPassword;
    private final LoginControlador controlador; //Logica de controlador

    //Constructor
    public VentanaLogin(){
        GestionBackup.verificarYRestaurarSiFalta();
        ConexionBD.inicializarBaseDatos();
        GestionBackup.crearCopiaAutomatica();
        configurarVentana();
        inicializarComponentes();
        this.controlador = new LoginControlador(this);
    }

    //Configuracion de ventana
    private void configurarVentana(){
        setTitle("Acceso al Sistema - La Boutique del Maíz y el Nopal");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Inicializacion de interfaz
    private void inicializarComponentes() {
        JPanel panelPrincipal = new JPanel(new BorderLayout());
        panelPrincipal.setBackground(new Color(245, 245, 245));

        //Encabezado de la ventana
        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(45, 45, 45));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        //Formulario
        JLabel lblTitulo = new JLabel("🌽 Iniciar Sesión 🌵");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        panelHeader.add(lblTitulo);

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setOpaque(false);
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.BOLD, 14));
        txtUsuario = new JTextField(15);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Arial", Font.BOLD, 14));
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 0; gbc.gridy = 0;
        panelForm.add(lblUsuario, gbc);
        gbc.gridx = 1;
        panelForm.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelForm.add(lblPassword, gbc);
        gbc.gridx = 1;
        panelForm.add(txtPassword, gbc);


        //Boton de ingreso
        JPanel panelBoton = new JPanel();
        panelBoton.setOpaque(false);
        panelBoton.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JButton btnIngresar = new JButton("Ingresar al Sistema");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIngresar.setBackground(new Color(40, 167, 69));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setPreferredSize(new Dimension(200, 40));

        //Actions listners
        btnIngresar.addActionListener(e -> procesarLogin());
        txtPassword.addActionListener(e -> procesarLogin());

        panelBoton.add(btnIngresar);

        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        panelPrincipal.add(panelForm, BorderLayout.CENTER);
        panelPrincipal.add(panelBoton, BorderLayout.SOUTH);

        add(panelPrincipal);
    }

    //Probar login
    private void procesarLogin(){
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        controlador.intentarLogin(user, pass);
    }

    public void limpiarCampos(){
        txtPassword.setText("");
        txtUsuario.requestFocus();
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}