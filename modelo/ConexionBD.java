package modelo;

//Clase encargada de la conexion con la base de datos SQLite y creacion de tablas

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.mindrot.jbcrypt.BCrypt;

public class ConexionBD{
    //Ruta del archivo local de la base de datos
    private static final String URL = "jdbc:sqlite:boutique.db";

    //Abre y devuelve una nueva conexion hacia SQLite
    public static Connection conectar() throws SQLException{
        return DriverManager.getConnection(URL);
    }

    //Verifica la existencia de las tablas principales y las crea si faltan
    public static void inicializarBaseDatos(){
        String sqlProductos = "CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "precio REAL NOT NULL, " +
                "categoria TEXT NOT NULL, " +
                "activo INTEGER DEFAULT 1, " +
                "stock INTEGER DEFAULT 50);";

        String sqlVentas = "CREATE TABLE IF NOT EXISTS ventas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha TEXT NOT NULL, " +
                "total REAL NOT NULL);";

        String sqlDetalle = "CREATE TABLE IF NOT EXISTS detalle_ventas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_venta INTEGER NOT NULL, " +
                "id_producto INTEGER NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "precio_unitario REAL NOT NULL, " +
                "subtotal REAL NOT NULL, " +
                "FOREIGN KEY (id_venta) REFERENCES ventas(id), " +
                "FOREIGN KEY (id_producto) REFERENCES productos(id));";

        String sqlUsuarios = "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "rol TEXT NOT NULL, " +
                "activo INTEGER DEFAULT 1);";

        //Ejecuta la creacion de tablas cerrando recursos automaticamente
        try(Connection conn = conectar(); Statement stmt = conn.createStatement()){
            stmt.execute(sqlProductos);
            stmt.execute(sqlVentas);
            stmt.execute(sqlDetalle);
            stmt.execute(sqlUsuarios);

            //Si la tabla de usuarios esta vacia crea un administrador inicial cifrado
            crearAdminSiNoExiste(conn);
        }catch(SQLException e){
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }

    //Inserta el usuario administrador por defecto con contrasena cifrada si no hay usuarios
    private static void crearAdminSiNoExiste(Connection conn) throws SQLException{
        String sqlContar = "SELECT COUNT(*) FROM usuarios";
        try(Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sqlContar)){
            if(rs.next() && rs.getInt(1) == 0){
                String sqlInsert = "INSERT INTO usuarios (usuario, password, rol, activo) VALUES (?, ?, ?, 1)";
                try(PreparedStatement pstmt = conn.prepareStatement(sqlInsert)){
                    pstmt.setString(1, "admin");
                    pstmt.setString(2, BCrypt.hashpw("admin123", BCrypt.gensalt()));
                    pstmt.setString(3, "ADMINISTRADOR");
                    pstmt.executeUpdate();
                }
            }
        }
    }
}