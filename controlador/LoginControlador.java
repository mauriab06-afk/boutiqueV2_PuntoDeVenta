package controlador;

//Controladoe para la interfaz de inicio de sesion

import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.VentanaLogin;
import vista.VentanaPuntoDeVenta;

import javax.swing.*;

public class LoginControlador{
    private final VentanaLogin vista; //Ventana de login
    private final UsuarioDAO usuarioDAO; //Conexion con la base de datos

    //Constructor
    public LoginControlador(VentanaLogin vista){
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
    }

    //Verificar login
    public void intentarLogin(String user, String pass){
        if (user.isEmpty() || pass.isEmpty()){ //Si alguno de los campos estan vacios
            JOptionPane.showMessageDialog(vista, "Por favor complete todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = usuarioDAO.autenticar(user, pass); //Verifica su existencia

        if(u != null){
            JOptionPane.showMessageDialog(vista, "¡Bienvenido/a " + u.getUsuario() + "!\nRol: " + u.getRol(), "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            vista.dispose();
            SwingUtilities.invokeLater(() -> new VentanaPuntoDeVenta(u).setVisible(true));
        }else{
            JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            vista.limpiarCampos();
        }
    }
}