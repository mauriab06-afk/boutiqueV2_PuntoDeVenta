package vista;

//Interfaz al momento de realizar el cobro

import modelo.*;
import controlador.*;

import javax.swing.*;
import java.awt.*;

public class VentanaCobro extends JDialog{
    private final double total;
    private boolean pagoConfirmado = false;

    private JTextField txtMontoRecibido;
    private JLabel lblCambio;

    //Constructor
    public VentanaCobro(Frame parent, double total) {
        super(parent, "Cobro de Venta", true);
        this.total = total;
        configurarVentana();
        inicializarComponentes();
    }

    //COnfiguracion de la ventana
    private void configurarVentana() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
    }

    //Creacion de componentes
    private void inicializarComponentes(){
        //Panel estructurado en tabla
        JPanel panelCentral = new JPanel(new GridLayout(3, 2, 10, 15));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTotalTitulo = new JLabel("Total a Pagar:");
        lblTotalTitulo.setFont(new Font("Arial", Font.BOLD, 16));

        JLabel lblTotalValor = new JLabel(String.format("$%.2f", total));
        lblTotalValor.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotalValor.setForeground(new Color(34, 139, 34));

        JLabel lblEfectivo = new JLabel("Efectivo Recibido:");
        lblEfectivo.setFont(new Font("Arial", Font.BOLD, 16));

        txtMontoRecibido = new JTextField();
        txtMontoRecibido.setFont(new Font("Arial", Font.BOLD, 18));

        //Escuchar al teclado en tiempo real
        txtMontoRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                calcularCambio();
            }
        });

        JLabel lblCambioTitulo = new JLabel("Cambio:");
        lblCambioTitulo.setFont(new Font("Arial", Font.BOLD, 16));

        lblCambio = new JLabel("$0.00");
        lblCambio.setFont(new Font("Arial", Font.BOLD, 20));
        lblCambio.setForeground(Color.BLUE);

        panelCentral.add(lblTotalTitulo);
        panelCentral.add(lblTotalValor);
        panelCentral.add(lblEfectivo);
        panelCentral.add(txtMontoRecibido);
        panelCentral.add(lblCambioTitulo);
        panelCentral.add(lblCambio);

        add(panelCentral, BorderLayout.CENTER);

        //Panel inferior para los botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        //Boton para monto exacto
        JButton btnPagoExacto = new JButton("Pago Exacto");
        btnPagoExacto.setFont(new Font("Arial", Font.BOLD, 13));
        btnPagoExacto.addActionListener(e -> {
            txtMontoRecibido.setText(String.format("%.2f", total));
            calcularCambio();
        });

        //Boton para confirmar cobro
        JButton btnConfirmar = new JButton("✓ Confirmar Venta");
        btnConfirmar.setFont(new Font("Arial", Font.BOLD, 14));
        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.addActionListener(e -> {
            if(validarPago()){
                pagoConfirmado = true;
                dispose();
            }
        });

        //Boton para cancelar
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCancelar.addActionListener(e -> dispose());

        panelInferior.add(btnPagoExacto);
        panelInferior.add(btnConfirmar);
        panelInferior.add(btnCancelar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    //Calcular cambio en base al monto ingresado
    private void calcularCambio(){
        try{
            double recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            double cambio = recibido - total;
            if(cambio >= 0){
                lblCambio.setText(String.format("$%.2f", cambio));
                lblCambio.setForeground(new Color(34, 139, 34));
            }else{
                lblCambio.setText("Falta: $" + String.format("%.2f", Math.abs(cambio)));
                lblCambio.setForeground(Color.RED);
            }
        }catch(NumberFormatException e){
            lblCambio.setText("$0.00");
            lblCambio.setForeground(Color.BLUE);
        }
    }

    //Valida que el numero ingresado sea valido
    private boolean validarPago(){
        try{
            double recibido = Double.parseDouble(txtMontoRecibido.getText().trim());
            if(recibido < total){
                JOptionPane.showMessageDialog(this, "El monto recibido es menor al total a pagar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            return true;
        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Ingrese un monto válido en efectivo.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isPagoConfirmado(){return pagoConfirmado;}
}