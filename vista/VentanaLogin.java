package vista;

//Ventana moderna de inicio de sesion con diseno dividido

import com.formdev.flatlaf.FlatLightLaf;
import controlador.LoginControlador;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class VentanaLogin extends JFrame {
    //Campos de texto y controlador
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private final LoginControlador controlador;

    //Colores principales de la marca
    private final Color VERDE_OSCURO = new Color(20, 90, 50);
    private final Color VERDE_MEDIO = new Color(34, 139, 68);
    private final Color VERDE_CLARO = new Color(46, 175, 80);

    //Constructor que arma la ventana y arranca el controlador
    public VentanaLogin() {
        this.controlador = new LoginControlador(this);
        this.controlador.inicializarSistema();

        configurarVentana();
        inicializarComponentes();
    }

    //Configura el tamano, titulo y centrado de la ventana
    private void configurarVentana() {
        setTitle("Acceso al Sistema - La Boutique del Maíz y el Nopal");
        setSize(820, 490);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    //Divide la pantalla en dos mitades: bienvenida/logo y formulario
    private void inicializarComponentes() {
        JPanel panelContenedor = new JPanel(new GridLayout(1, 2));

        //MITAD IZQUIERDA: Panel verde con ondas y logo circular centrado
        JPanel panelIzquierdo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                //Fondo degradado diagonal
                GradientPaint gp = new GradientPaint(0, 0, VERDE_CLARO, w, h, VERDE_OSCURO);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);

                //Primera onda decorativa trasera
                g2.setColor(new Color(255, 255, 255, 20));
                GeneralPath onda1 = new GeneralPath();
                onda1.moveTo(0, h * 0.68);
                onda1.curveTo(w * 0.30, h * 0.55, w * 0.70, h * 0.80, w, h * 0.64);
                onda1.lineTo(w, h);
                onda1.lineTo(0, h);
                onda1.closePath();
                g2.fill(onda1);

                //Segunda onda decorativa frontal
                g2.setColor(new Color(255, 255, 255, 30));
                GeneralPath onda2 = new GeneralPath();
                onda2.moveTo(0, h * 0.78);
                onda2.curveTo(w * 0.40, h * 0.88, w * 0.65, h * 0.68, w, h * 0.76);
                onda2.lineTo(w, h);
                onda2.lineTo(0, h);
                onda2.closePath();
                g2.fill(onda2);
            }
        };
        panelIzquierdo.setLayout(new GridBagLayout());
        panelIzquierdo.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        //Contenedor interno centrado para el logo y los textos
        JPanel contenidoIzq = new JPanel();
        contenidoIzq.setOpaque(false);
        contenidoIzq.setLayout(new BoxLayout(contenidoIzq, BoxLayout.Y_AXIS));

        //Medallon circular blanco con halo suave para el logo
        JPanel panelLogoCircular = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int s = Math.min(getWidth(), getHeight());
                //Aro exterior semitransparente
                g2.setColor(new Color(255, 255, 255, 55));
                g2.fill(new Ellipse2D.Double(0, 0, s, s));

                //Circulo blanco interior
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Double(8, 8, s - 16, s - 16));
            }
        };
        panelLogoCircular.setOpaque(false);
        panelLogoCircular.setPreferredSize(new Dimension(196, 196));
        panelLogoCircular.setMaximumSize(new Dimension(196, 196));
        panelLogoCircular.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelLogoCircular.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JLabel lblImagenLogo = new JLabel("", SwingConstants.CENTER);
        cargarImagenLogo(lblImagenLogo, 148);
        panelLogoCircular.add(lblImagenLogo, BorderLayout.CENTER);

        //Titulo principal centrado
        JLabel lblBienvenida = new JLabel("¡Bienvenido!");
        lblBienvenida.setFont(new Font("SansSerif", Font.BOLD, 28));
        lblBienvenida.setForeground(Color.WHITE);
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Subtitulo con el nombre del negocio
        JLabel lblNegocio = new JLabel("La Boutique del Maíz y el Nopal");
        lblNegocio.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblNegocio.setForeground(new Color(220, 255, 228));
        lblNegocio.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Texto descriptivo centrado
        JLabel lblDescripcion = new JLabel("<html><center>Control de ventas, inventario y reportes<br>en un solo lugar.</center></html>", SwingConstants.CENTER);
        lblDescripcion.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblDescripcion.setForeground(new Color(235, 248, 238));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Acomodo vertical centrado
        contenidoIzq.add(panelLogoCircular);
        contenidoIzq.add(Box.createVerticalStrut(20));
        contenidoIzq.add(lblBienvenida);
        contenidoIzq.add(Box.createVerticalStrut(6));
        contenidoIzq.add(lblNegocio);
        contenidoIzq.add(Box.createVerticalStrut(10));
        contenidoIzq.add(lblDescripcion);

        panelIzquierdo.add(contenidoIzq);

        //MITAD DERECHA: Formulario limpio en fondo blanco
        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setBackground(Color.WHITE);
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(30, 45, 30, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        //Titulo y subtitulo del login
        JLabel lblTituloLogin = new JLabel("Iniciar Sesión");
        lblTituloLogin.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTituloLogin.setForeground(VERDE_OSCURO);

        JLabel lblSubtitulo = new JLabel("Ingresa tus credenciales para continuar");
        lblSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(120, 125, 130));

        //Campo de usuario
        JLabel lblUsuario = new JLabel("Usuario");
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUsuario.setForeground(new Color(70, 75, 80));

        txtUsuario = new JTextField();
        txtUsuario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtUsuario.setPreferredSize(new Dimension(0, 40));
        txtUsuario.putClientProperty("JTextField.placeholderText", "Ej. admin");

        //Campo de contrasena
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblPassword.setForeground(new Color(70, 75, 80));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(0, 40));
        txtPassword.putClientProperty("JTextField.placeholderText", "••••••••");
        txtPassword.putClientProperty("JTextField.showRevealButton", true);

        //Boton principal verde
        JButton btnIngresar = new JButton("INGRESAR");
        btnIngresar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnIngresar.setBackground(VERDE_MEDIO);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setPreferredSize(new Dimension(0, 44));
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setFocusPainted(false);

        //Acomodo de filas en el formulario derecho
        gbc.gridy = 0; gbc.insets = new Insets(0, 0, 4, 0);
        panelDerecho.add(lblTituloLogin, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        panelDerecho.add(lblSubtitulo, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 6, 0);
        panelDerecho.add(lblUsuario, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(0, 0, 16, 0);
        panelDerecho.add(txtUsuario, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(0, 0, 6, 0);
        panelDerecho.add(lblPassword, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 24, 0);
        panelDerecho.add(txtPassword, gbc);

        gbc.gridy = 6; gbc.insets = new Insets(0, 0, 18, 0);
        panelDerecho.add(btnIngresar, gbc);


        //Eventos para iniciar sesion con clic o tecla Enter
        btnIngresar.addActionListener(e -> procesarLogin());
        txtPassword.addActionListener(e -> procesarLogin());

        panelContenedor.add(panelIzquierdo);
        panelContenedor.add(panelDerecho);
        add(panelContenedor);
    }

    //Escala el logo manteniendo su proporcion original para que no se deforme ni se corte
    private void cargarImagenLogo(JLabel destino, int tamanoMax) {
        try {
            BufferedImage imgOriginal = ImageIO.read(new File("img/logo.jpg"));
            int origW = imgOriginal.getWidth();
            int origH = imgOriginal.getHeight();

            double escala = Math.min((double) tamanoMax / origW, (double) tamanoMax / origH);
            int nuevoW = (int) (origW * escala);
            int nuevoH = (int) (origH * escala);

            Image imgEscalada = imgOriginal.getScaledInstance(nuevoW, nuevoH, Image.SCALE_SMOOTH);
            destino.setIcon(new ImageIcon(imgEscalada));
        } catch (Exception e) {
            destino.setText("<html><center><b>La Boutique del<br>Maíz y el Nopal</b></center></html>");
            destino.setForeground(VERDE_OSCURO);
        }
    }

    //Lee usuario y contrasena y los manda al controlador
    private void procesarLogin() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        controlador.intentarLogin(user, pass);
    }

    //Borra la contrasena y regresa el cursor al campo de usuario
    public void limpiarCampos() {
        txtPassword.setText("");
        txtUsuario.requestFocus();
    }

    //Punto de entrada principal con tema claro moderno FlatLaf
    public static void main(String[] args) {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.rowHeight", 30);
        } catch (Exception ex) {
            System.err.println("No se pudo iniciar FlatLaf: " + ex.getMessage());
        }

        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}