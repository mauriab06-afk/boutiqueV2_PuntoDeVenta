package vista;

//Ventana moderna para procesar el cobro y calcular el cambio

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.*;

public class VentanaCobro extends JDialog{
    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color PILDORA_STOCK_BG = new Color(214, 240, 221);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    //Componentes de captura y botones
    private JTextField txtMontoRecibido;
    private JLabel lblCambio;

    private JButton btnPagoExacto;
    private JButton btnConfirmar;
    private JButton btnCancelar;

    //Constructor principal
    public VentanaCobro(Frame parent, double total){
        super(parent, "Cobro de Venta", true);
        configurarVentana();
        inicializarComponentes(total);
    }

    //Configura tamano, centrado y color de fondo
    private void configurarVentana(){
        setSize(480, 410);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO_CREMA);
        setResizable(false);
    }

    //Arma el encabezado verde, la tarjeta de cobro y los botones inferiores
    private void inicializarComponentes(double total){
        //Barra superior verde bosque
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setBackground(VERDE_HEADER);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(14, 22, 14, 22));

        JLabel lblTitulo = new JLabel("✓  Finalizar y Cobrar Venta");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Ingresa el efectivo recibido para calcular el cambio");
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

        //Contenedor principal sobre fondo crema
        JPanel panelCuerpo = new JPanel(new BorderLayout(0, 14));
        panelCuerpo.setBackground(FONDO_CREMA);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        //Tarjeta blanca redondeada con los montos
        JPanel tarjetaCobro = new JPanel(new GridBagLayout());
        tarjetaCobro.setBackground(Color.WHITE);
        tarjetaCobro.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        tarjetaCobro.putClientProperty("FlatLaf.style", "arc: 20");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 4, 8, 4);

        JLabel lblTotalTitulo = new JLabel("Total a Pagar:");
        lblTotalTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTotalTitulo.setForeground(new Color(65, 70, 68));

        JLabel lblTotalValor = new JLabel(String.format("$%.2f", total), SwingConstants.RIGHT);
        lblTotalValor.setFont(new Font("SansSerif", Font.BOLD, 26));
        lblTotalValor.setForeground(VERDE_HEADER);

        JLabel lblEfectivo = new JLabel("Efectivo Recibido:");
        lblEfectivo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblEfectivo.setForeground(new Color(65, 70, 68));

        txtMontoRecibido = new JTextField();
        txtMontoRecibido.setFont(new Font("SansSerif", Font.BOLD, 20));
        txtMontoRecibido.setHorizontalAlignment(SwingConstants.RIGHT);
        txtMontoRecibido.setPreferredSize(new Dimension(160, 42));
        txtMontoRecibido.putClientProperty("JTextField.placeholderText", "0.00");
        txtMontoRecibido.putClientProperty("FlatLaf.style", "arc: 14");

        JLabel lblCambioTitulo = new JLabel("Cambio:");
        lblCambioTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblCambioTitulo.setForeground(new Color(65, 70, 68));

        lblCambio = new JLabel("$0.00", SwingConstants.RIGHT);
        lblCambio.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblCambio.setForeground(VERDE_PRIMARIO);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.45;
        tarjetaCobro.add(lblTotalTitulo, gbc);
        gbc.gridx = 1; gbc.weightx = 0.55;
        tarjetaCobro.add(lblTotalValor, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.45;
        tarjetaCobro.add(lblEfectivo, gbc);
        gbc.gridx = 1; gbc.weightx = 0.55;
        tarjetaCobro.add(txtMontoRecibido, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.45;
        tarjetaCobro.add(lblCambioTitulo, gbc);
        gbc.gridx = 1; gbc.weightx = 0.55;
        tarjetaCobro.add(lblCambio, gbc);

        panelCuerpo.add(tarjetaCobro, BorderLayout.CENTER);

        //Panel inferior con botones redondeados
        JPanel panelInferior = new JPanel(new BorderLayout(0, 10));
        panelInferior.setOpaque(false);

        btnPagoExacto = new JButton("=  Pago Exacto");
        btnPagoExacto.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnPagoExacto.setBackground(PILDORA_STOCK_BG);
        btnPagoExacto.setForeground(VERDE_HEADER);
        btnPagoExacto.setPreferredSize(new Dimension(0, 38));
        btnPagoExacto.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPagoExacto.putClientProperty("FlatLaf.style", "arc: 16");

        JPanel filaPrincipales = new JPanel(new GridLayout(1, 2, 12, 0));
        filaPrincipales.setOpaque(false);

        btnCancelar = new JButton("✕  Cancelar");
        btnCancelar.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCancelar.setBackground(ROJO_ALERTA);
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setPreferredSize(new Dimension(0, 44));
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.putClientProperty("FlatLaf.style", "arc: 18");

        btnConfirmar = new JButton("✓  Confirmar Venta");
        btnConfirmar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnConfirmar.setBackground(VERDE_PRIMARIO);
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setPreferredSize(new Dimension(0, 44));
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.putClientProperty("FlatLaf.style", "arc: 18");

        filaPrincipales.add(btnCancelar);
        filaPrincipales.add(btnConfirmar);

        panelInferior.add(btnPagoExacto, BorderLayout.NORTH);
        panelInferior.add(filaPrincipales, BorderLayout.SOUTH);

        panelCuerpo.add(panelInferior, BorderLayout.SOUTH);
        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Metodos para que el controlador lea y actualice la vista
    public String getMontoRecibidoTexto(){ return txtMontoRecibido.getText().trim(); }
    public void setMontoRecibidoTexto(String texto){ txtMontoRecibido.setText(texto); }

    //Actualiza el texto y color del cambio calculado
    public void actualizarEstadoCambio(String texto, Color color){
        lblCambio.setText(texto);
        lblCambio.setForeground(color);
    }

    //Muestra ventanas emergentes de aviso o error
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    //Registro de listeners desde el controlador
    public void agregarListeners(KeyListener onTecladoMonto, ActionListener onPagoExacto,
                                 ActionListener onConfirmar, ActionListener onCancelar){
        txtMontoRecibido.addKeyListener(onTecladoMonto);
        btnPagoExacto.addActionListener(onPagoExacto);
        btnConfirmar.addActionListener(onConfirmar);
        btnCancelar.addActionListener(onCancelar);
    }
}