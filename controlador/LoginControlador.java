package controlador;

//Controlador para la interfaz de inicio de sesion

import modelo.ConexionBD;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.VentanaLogin;
import vista.VentanaPuntoDeVenta;

import javax.swing.*;

public class LoginControlador{
    //Referencias a la ventana de login y al acceso de usuarios en la base de datos
    private final VentanaLogin vista;
    private final UsuarioDAO usuarioDAO;

    //Constructor que enlaza la vista con el controlador
    public LoginControlador(VentanaLogin vista){
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
    }

    //Valida los datos ingresados y verifica si el usuario existe
    public void intentarLogin(String user, String pass){
        //Verifica que ninguno de los campos este vacio
        if (user.isEmpty() || pass.isEmpty()){
            JOptionPane.showMessageDialog(vista, "Por favor complete todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Busca el usuario y contrasena en la base de datos
        Usuario u = usuarioDAO.autenticar(user, pass);

        if(u != null){
            //Si los datos son correctos abre el punto de venta y cierra el login
            JOptionPane.showMessageDialog(vista, "¡Bienvenido/a " + u.getUsuario() + "!\nRol: " + u.getRol(), "Acceso Concedido", JOptionPane.INFORMATION_MESSAGE);
            vista.dispose();
            SwingUtilities.invokeLater(() -> new VentanaPuntoDeVenta(u).setVisible(true));
        }else{
            //Si son incorrectos muestra error y limpia la contrasena
            JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos.", "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
            vista.limpiarCampos();
        }
    }

    //Verifica respaldos e inicializa las tablas de la base de datos al arrancar
    public void inicializarSistema() {
        GestionBackup.verificarYRestaurarSiFalta();
        ConexionBD.inicializarBaseDatos();
        GestionBackup.crearCopiaAutomatica();
    }
}