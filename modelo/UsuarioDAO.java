package modelo;

//Consultar tabla de usuario

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    //Verificar a un usuario por su nombre y contraseña.
    public Usuario autenticar(String usuario, String password){
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND password = ? AND activo = 1;";
        
        //Intentar conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, usuario);
            pstmt.setString(2, password);
            //Verificamos si existe
            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("password"),
                        rs.getString("rol"),
                        rs.getInt("activo") == 1
                    );
                }
            }
        }catch (SQLException e){
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    //Obtener todos los usuarios
    public List<Usuario> obtenerTodos(){
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id ASC;";

        //INtentar conexion
        try(Connection conn = ConexionBD.conectar();Statement stmt = conn.createStatement();ResultSet rs = stmt.executeQuery(sql)){
            while(rs.next()){
                lista.add(new Usuario(
                    rs.getInt("id"),
                    rs.getString("usuario"),
                    rs.getString("password"),
                    rs.getString("rol"),
                    rs.getInt("activo") == 1
                ));
            }
        }catch(SQLException e){
            System.err.println("Error al consultar usuarios: " + e.getMessage());
        }
        return lista;
    }

    //Agregar usuario a la base de datos
    public boolean insertar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (usuario, password, rol, activo) VALUES (?, ?, ?, ?);";
        //INtentar conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, usuario.getUsuario());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getRol());
            pstmt.setInt(4, usuario.isActivo() ? 1 : 0);
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    //Actualiza usuario existente
    public boolean actualizar(Usuario usuario){
        String sql = "UPDATE usuarios SET usuario = ?, password = ?, rol = ?, activo = ? WHERE id = ?;";
        //Intentar conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, usuario.getUsuario());
            pstmt.setString(2, usuario.getPassword());
            pstmt.setString(3, usuario.getRol());
            pstmt.setInt(4, usuario.isActivo() ? 1 : 0);
            pstmt.setInt(5, usuario.getId());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }
}