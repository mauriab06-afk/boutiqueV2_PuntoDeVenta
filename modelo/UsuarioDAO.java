package modelo;

//Clase de acceso a datos para consultar, registrar y autenticar usuarios con BCrypt

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO{

    //Verifica el usuario y compara su contrasena contra el hash guardado en la base de datos
    public Usuario autenticar(String usuario, String password){
        String sql = "SELECT * FROM usuarios WHERE usuario = ? AND activo = 1;";

        //Busca el registro activo por nombre de usuario
        try(Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, usuario);

            try(ResultSet rs = pstmt.executeQuery()){
                if(rs.next()){
                    int id = rs.getInt("id");
                    String hashGuardado = rs.getString("password");
                    boolean valida = false;

                    //Comprueba si la contrasena ya esta cifrada con BCrypt o sigue en texto plano
                    if(hashGuardado != null && (hashGuardado.startsWith("$2a$") || hashGuardado.startsWith("$2b$"))){
                        valida = BCrypt.checkpw(password, hashGuardado);
                    }else if(password.equals(hashGuardado)){
                        //Si estaba en texto plano la valida y la migra a BCrypt automaticamente
                        valida = true;
                        String nuevoHash = BCrypt.hashpw(password, BCrypt.gensalt());
                        actualizarPasswordCifrada(conn, id, nuevoHash);
                        hashGuardado = nuevoHash;
                    }

                    if(valida){
                        return new Usuario(
                            id,
                            rs.getString("usuario"),
                            hashGuardado,
                            rs.getString("rol"),
                            rs.getInt("activo") == 1
                        );
                    }
                }
            }
        }catch(SQLException e){
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    //Actualiza en silencio la contrasena de un usuario antiguo al formato cifrado
    private void actualizarPasswordCifrada(Connection conn, int idUsuario, String hash){
        String sql = "UPDATE usuarios SET password = ? WHERE id = ?;";
        try(PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, hash);
            pstmt.setInt(2, idUsuario);
            pstmt.executeUpdate();
        }catch(SQLException e){
            System.err.println("Error al migrar contrasena: " + e.getMessage());
        }
    }

    //Obtiene la lista completa de usuarios ordenados por identificador
    public List<Usuario> obtenerTodos(){
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY id ASC;";

        //Recorre la tabla y construye cada objeto Usuario
        try(Connection conn = ConexionBD.conectar(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)){
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

    //Registra un nuevo usuario cifrando su contrasena antes de guardarla
    public boolean insertar(Usuario usuario){
        String sql = "INSERT INTO usuarios (usuario, password, rol, activo) VALUES (?, ?, ?, ?);";

        try(Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, usuario.getUsuario());
            pstmt.setString(2, cifrarSiHaceFalta(usuario.getPassword()));
            pstmt.setString(3, usuario.getRol());
            pstmt.setInt(4, usuario.isActivo() ? 1 : 0);
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    //Actualiza los datos de un usuario y cifra la contrasena si fue modificada
    public boolean actualizar(Usuario usuario){
        String sql = "UPDATE usuarios SET usuario = ?, password = ?, rol = ?, activo = ? WHERE id = ?;";

        try(Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, usuario.getUsuario());
            pstmt.setString(2, cifrarSiHaceFalta(usuario.getPassword()));
            pstmt.setString(3, usuario.getRol());
            pstmt.setInt(4, usuario.isActivo() ? 1 : 0);
            pstmt.setInt(5, usuario.getId());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    //Evita volver a cifrar una cadena que ya tiene formato de hash BCrypt
    private String cifrarSiHaceFalta(String password){
        if(password != null && (password.startsWith("$2a$") || password.startsWith("$2b$"))){
            return password;
        }
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
}