package controlador;

//Controlador de la interfaz de manejo de usuarios

import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.VentanaUsuarios;

import javax.swing.*;
import java.util.List;

public class UsuarioControlador{
    private final VentanaUsuarios vista; //Interfaz
    private final UsuarioDAO usuarioDAO; //Conexion con base de datos

    //Constructor
    public UsuarioControlador(VentanaUsuarios vista){
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    //Crear o modificar usuario
    public void guardarUsuario(int idSeleccionado, String nombreUser, String pass, String rol, boolean activo){
        if(nombreUser.isEmpty() || pass.isEmpty()){ //Verifica que los campos esten llenos
            JOptionPane.showMessageDialog(vista, "El usuario y la contraseña no pueden estar vacíos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(idSeleccionado == -1){ //Si id es -1 es un nuevo usuario
            Usuario nuevo = new Usuario(0, nombreUser, pass, rol, activo);
            boolean exito = usuarioDAO.insertar(nuevo);
            if(exito){
                JOptionPane.showMessageDialog(vista, "¡Usuario registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                vista.cargarTablaUsuarios();
            }else{
                JOptionPane.showMessageDialog(vista, "Error al registrar (es posible que el nombre de usuario ya exista).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else{ //Si el id no es -1 (Es alguien que ya existe)
            Usuario editable = new Usuario(idSeleccionado, nombreUser, pass, rol, activo);
            boolean exito = usuarioDAO.actualizar(editable);
            if(exito){
                JOptionPane.showMessageDialog(vista, "¡Usuario actualizado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                vista.cargarTablaUsuarios();
            }else{
                JOptionPane.showMessageDialog(vista, "Error al actualizar la información del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Cambiar estado de un usuario
    public void cambiarEstadoUsuario(Usuario usuario, boolean nuevoEstado){
        //No se puede desactivar el admin
        if("admin".equalsIgnoreCase(usuario.getUsuario()) && !nuevoEstado){
            JOptionPane.showMessageDialog(vista, "No se puede desactivar la cuenta principal de 'admin'.", "Acción Denegada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuario.setActivo(nuevoEstado);
        boolean exito = usuarioDAO.actualizar(usuario);
        if(exito){ //Si se pudo cambiar
            JOptionPane.showMessageDialog(vista, "Estado de usuario cambiado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            vista.cargarTablaUsuarios();
        }else{
            JOptionPane.showMessageDialog(vista, "Error al cambiar el estado del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}