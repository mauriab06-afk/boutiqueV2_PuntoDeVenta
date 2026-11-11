package modelo;

//Clase encargada de la conexion con la base de datos del archivo local por medio de SQLite

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD{
    //Ruta al archivo de base de datos SQLite
    private static final String URL = "jdbc:sqlite:boutique.db";

    //Establece y retorna una nueva conexión con la base de datos.
    public static Connection conectar() throws SQLException{
        return DriverManager.getConnection(URL);
    }

    //Verificamos la existencia de las tablas y si no se crean (IF NOT)
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

        //En caso de que no se tengan los usuarios de prueba (ACTIVAR)
        //String sqlAdminSeed = "INSERT OR IGNORE INTO usuarios (usuario, password, rol) VALUES ('admin', 'admin123', 'ADMINISTRADOR');";
        //String sqlCajeroSeed = "INSERT OR IGNORE INTO usuarios (usuario, password, rol) VALUES ('cajero', '1234', 'CAJERO');";

        // Ejecución de sentencias usando autoclose para liberar la conexión al terminar
        try(Connection conn = conectar(); Statement stmt = conn.createStatement()){
            stmt.execute(sqlProductos);
            stmt.execute(sqlVentas);
            stmt.execute(sqlDetalle);
            stmt.execute(sqlUsuarios);
            //stmt.execute(sqlAdminSeed);
            //stmt.execute(sqlCajeroSeed);
        }catch (SQLException e){
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}