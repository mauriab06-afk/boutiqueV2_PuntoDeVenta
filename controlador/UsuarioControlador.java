package controlador;

//Controlador que maneja toda la logica de los usuarios

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.VentanaUsuarios;

public class UsuarioControlador{
    //Referencia a la ventana y a la clase que conecta con la base de datos
    private final VentanaUsuarios vista;
    private final UsuarioDAO usuarioDAO;

    //Constructor que conecta los botones y carga la tabla inicial
    public UsuarioControlador(VentanaUsuarios vista){
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();

        //Conecta los eventos de la vista con los metodos de este controlador
        this.vista.agregarListeners(
            e -> guardarUsuario(),
            e -> cambiarEstadoUsuario(),
            e -> vista.limpiarFormulario(),
            e -> {
                if(!e.getValueIsAdjusting() && vista.getFilaSeleccionada() != -1){
                    vista.cargarFormularioDesdeSeleccion();
                }
            }
        );

        cargarTablaUsuarios();
    }

    //Pide todos los usuarios al DAO y manda las filas listas a la vista
    public void cargarTablaUsuarios(){
        List<Object[]> filas = new ArrayList<>();
        for(Usuario u : usuarioDAO.obtenerTodos()){
            filas.add(new Object[]{
                u.getId(),
                u.getUsuario(),
                u.getPassword(),
                u.getRol(),
                u.isActivo() ? "Habilitado" : "Deshabilitado"
            });
        }
        vista.actualizarTabla(filas);
    }

    //Crea un usuario nuevo o actualiza uno que ya existe
    private void guardarUsuario(){
        int idSeleccionado = vista.getIdUsuarioSeleccionado();
        String nombreUser = vista.getUsuarioTexto();
        String pass = vista.getPasswordTexto();
        String rol = vista.getRolSeleccionado();
        boolean activo = vista.isActivoSeleccionado();

        //Verifica que no haya campos vacios
        if(nombreUser.isEmpty() || pass.isEmpty()){
            vista.mostrarMensaje("El usuario y la contraseña no pueden estar vacíos.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if(idSeleccionado == -1){
            //Si el id es -1 significa que es un registro nuevo
            Usuario nuevo = new Usuario(0, nombreUser, pass, rol, activo);
            if(usuarioDAO.insertar(nuevo)){
                vista.mostrarMensaje("¡Usuario registrado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarTablaUsuarios();
            }else{
                vista.mostrarMensaje("Error al registrar (es posible que el nombre de usuario ya exista).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //Si ya tiene un id se actualizan sus datos
            Usuario editable = new Usuario(idSeleccionado, nombreUser, pass, rol, activo);
            if(usuarioDAO.actualizar(editable)){
                vista.mostrarMensaje("¡Usuario actualizado con éxito!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                vista.limpiarFormulario();
                cargarTablaUsuarios();
            }else{
                vista.mostrarMensaje("Error al actualizar la información del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Invierte el estado de activo a inactivo del usuario seleccionado en la tabla
    private void cambiarEstadoUsuario(){
        int fila = vista.getFilaSeleccionada();
        if(fila == -1){
            vista.mostrarMensaje("Seleccione un usuario de la tabla.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Lee los valores de la fila elegida
        int id = (int) vista.getValorCelda(fila, 0);
        String u = (String) vista.getValorCelda(fila, 1);
        String p = (String) vista.getValorCelda(fila, 2);
        String r = (String) vista.getValorCelda(fila, 3);
        boolean estadoActual = "Habilitado".equals(vista.getValorCelda(fila, 4));
        boolean nuevoEstado = !estadoActual;

        //Evita que se desactive la cuenta principal de administrador
        if("admin".equalsIgnoreCase(u) && !nuevoEstado){
            vista.mostrarMensaje("No se puede desactivar la cuenta principal de 'admin'.", "Acción Denegada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //Guarda el cambio de estado en la base de datos
        Usuario usr = new Usuario(id, u, p, r, nuevoEstado);
        if(usuarioDAO.actualizar(usr)){
            vista.mostrarMensaje("Estado de usuario cambiado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            vista.limpiarFormulario();
            cargarTablaUsuarios();
        }else{
            vista.mostrarMensaje("Error al cambiar el estado del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}