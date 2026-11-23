package controlador;

//Controlador para gestionar el calculo de cambio y confirmacion de cobro

import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import vista.VentanaCobro;

public class CobroControlador{
    private final VentanaCobro vista;
    private final double total;
    private boolean pagoConfirmado = false;

    //Constructor que conecta los eventos de la ventana de cobro
    public CobroControlador(VentanaCobro vista, double total){
        this.vista = vista;
        this.total = total;

        this.vista.agregarListeners(
            new KeyAdapter(){
                @Override
                public void keyReleased(KeyEvent e){
                    calcularCambio();
                }
            },
            e -> aplicarPagoExacto(),
            e -> confirmarVenta(),
            e -> vista.dispose()
        );
    }

    //Coloca el monto exacto del total en la caja de efectivo
    private void aplicarPagoExacto(){
        vista.setMontoRecibidoTexto(String.format("%.2f", total));
        calcularCambio();
    }

    //Calcula y actualiza en pantalla el cambio o el saldo faltante
    private void calcularCambio(){
        try{
            double recibido = Double.parseDouble(vista.getMontoRecibidoTexto());
            double cambio = recibido - total;
            if(cambio >= 0){
                vista.actualizarEstadoCambio(String.format("$%.2f", cambio), new Color(34, 153, 72));
            }else{
                vista.actualizarEstadoCambio("Falta: $" + String.format("%.2f", Math.abs(cambio)), new Color(220, 53, 69));
            }
        }catch(NumberFormatException e){
            vista.actualizarEstadoCambio("$0.00", new Color(26, 108, 52));
        }
    }

    //Valida que el efectivo alcance y marca la venta como confirmada
    private void confirmarVenta(){
        try{
            double recibido = Double.parseDouble(vista.getMontoRecibidoTexto());
            if(recibido < total){
                vista.mostrarMensaje("El monto recibido es menor al total a pagar.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }
            pagoConfirmado = true;
            vista.dispose();
        }catch(NumberFormatException e){
            vista.mostrarMensaje("Ingrese un monto válido en efectivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Indica si el cobro fue confirmado correctamente
    public boolean isPagoConfirmado(){
        return pagoConfirmado;
    }
}