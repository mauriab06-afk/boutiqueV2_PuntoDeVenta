package modelo;

//Consultar tabla de usuarios

import java.sql.*;
import java.util.*;

public class VentaDAO {

    //Registra una venta completa en la base de datos
    public boolean guardarVenta(Venta venta){
        String sqlVenta = "INSERT INTO ventas (fecha, total) VALUES (datetime('now', 'localtime'), ?)";
        String sqlDetalle = "INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlStock = "UPDATE productos SET stock = stock - ? WHERE id = ?";

        Connection conn = null;
        try{ //Revisamos que se pueda conectar
            conn = ConexionBD.conectar();
            if(conn == null)return false;

            conn.setAutoCommit(false);
            long idVentaGenerada;

            //Insertar y obtener el id
            try(PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)){
                pstmtVenta.setDouble(1, venta.getTotal());
                pstmtVenta.executeUpdate();

                try(ResultSet rsKeys = pstmtVenta.getGeneratedKeys()){
                    if(rsKeys.next()){
                        idVentaGenerada = rsKeys.getLong(1);
                    }else{
                        conn.rollback();
                        return false;
                    }
                }
            }

            //Insertar cada item
            try(PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)){
                for(ItemCarrito item : venta.getDetalles()){
                    pstmtDetalle.setLong(1, idVentaGenerada);
                    pstmtDetalle.setInt(2, item.getProducto().getId());
                    pstmtDetalle.setInt(3, item.getCantidad());
                    pstmtDetalle.setDouble(4, item.getPrecioUnitario());
                    pstmtDetalle.setDouble(5, item.calcularSubtotal());
                    pstmtDetalle.addBatch();
                }
                pstmtDetalle.executeBatch();
            }

            //Eliminar del stock del inventario
            try(PreparedStatement pstmtStock = conn.prepareStatement(sqlStock)){
                for(ItemCarrito item : venta.getDetalles()){
                    pstmtStock.setInt(1, item.getCantidad());
                    pstmtStock.setInt(2, item.getProducto().getId());
                    pstmtStock.addBatch();
                }
                pstmtStock.executeBatch();
            }

            //Confirmar cambios
            conn.commit();
            return true;

        }catch(SQLException e){
            System.err.println("Error al guardar la venta: " + e.getMessage());
            //En caso de errores revertir
            if(conn != null){
                try{
                    conn.rollback();
                }catch(SQLException ex) {
                    System.err.println("Error al realizar rollback: " + ex.getMessage());
                }
            }
            return false;
        }finally{
            if(conn != null){
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    //Elimina una venta
    public boolean eliminarVenta(int idVenta){
        String sqlDetalle = "DELETE FROM detalle_ventas WHERE id_venta = ?";
        String sqlVenta = "DELETE FROM ventas WHERE id = ?";

        Connection conn = null;
        try{
            conn = ConexionBD.conectar();
            if(conn == null) return false;

            conn.setAutoCommit(false);

            //Borra primero los detalles de la venta
            try(PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle)) {
                pstmtDetalle.setInt(1, idVenta);
                pstmtDetalle.executeUpdate();
            }

            //Borrar la cabecera de la venta
            try(PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta)) {
                pstmtVenta.setInt(1, idVenta);
                pstmtVenta.executeUpdate();
            }

            conn.commit();
            return true;

        }catch(SQLException e){
            System.err.println("Error al eliminar la venta: " + e.getMessage());
            if(conn != null){
                try{
                    conn.rollback();
                }catch(SQLException ex){
                    System.err.println("Error al realizar rollback: " + ex.getMessage());
                }
            }
            return false;
        }finally{
            if(conn != null){
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    //Obtener ventas en base a los limites definidos
    public List<Object[]> obtenerVentasPorRango(String fechaInicio, String fechaFin){
        List<Object[]> resultados = new ArrayList<>();
        String sql = "SELECT id, fecha, total FROM ventas " +
                     "WHERE date(fecha) >= date(?) AND date(fecha) <= date(?) " +
                     "ORDER BY fecha DESC";

        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, fechaInicio);
            pstmt.setString(2, fechaFin);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                resultados.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("fecha"),
                    "$" + String.format("%.2f", rs.getDouble("total"))
                });
            }
        }catch(SQLException e){
            System.err.println("Error al consultar ventas por rango: " + e.getMessage());
        }
        return resultados;
    }

    //Desglose individual de una venta
    public List<Object[]> obtenerDetalleVenta(int idVenta){
        List<Object[]> detalles = new ArrayList<>();
        String sql = "SELECT p.nombre, dv.cantidad, dv.precio_unitario, dv.subtotal " +
                     "FROM detalle_ventas dv " +
                     "JOIN productos p ON dv.id_producto = p.id " +
                     "WHERE dv.id_venta = ?";

        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, idVenta);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){ //Todos los items
                detalles.add(new Object[]{
                    rs.getString("nombre"),
                    rs.getInt("cantidad"),
                    "$" + String.format("%.2f", rs.getDouble("precio_unitario")),
                    "$" + String.format("%.2f", rs.getDouble("subtotal"))
                });
            }
        }catch(SQLException e){
            System.err.println("Error al obtener detalle de la venta: " + e.getMessage());
        }
        return detalles;
    }

    //Ventas anuales
    public Map<String, Double> obtenerVentasAnuales(int anio) {
        Map<String, Double> ventasPorMes = new LinkedHashMap<>();
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        for(String m : meses){ventasPorMes.put(m, 0.0);} //Inisializar cada mes (para ir sumando en el mismo)

        String sql = "SELECT strftime('%m', fecha) AS mes, SUM(total) AS total_mes " +
                     "FROM ventas " +
                     "WHERE strftime('%Y', fecha) = ? " +
                     "GROUP BY mes";

        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, String.format("%04d", anio));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()){
                int numMes = Integer.parseInt(rs.getString("mes")) - 1;
                if (numMes >= 0 && numMes < 12) {
                    ventasPorMes.put(meses[numMes], rs.getDouble("total_mes"));
                }
            }
        }catch(SQLException e){
            System.err.println("Error al obtener ventas anuales: " + e.getMessage());
        }
        return ventasPorMes;
    }

    //Top de productos
    public List<Object[]> obtenerTopProductos(boolean masVendidos, int limite){
        List<Object[]> resultados = new ArrayList<>();
        String orden = masVendidos ? "DESC" : "ASC";

        String sql = "SELECT p.nombre, " +
                     "COALESCE(SUM(dv.cantidad), 0) AS total_cant, " +
                     "COALESCE(SUM(dv.subtotal), 0) AS total_monto " +
                     "FROM productos p " +
                     "LEFT JOIN detalle_ventas dv ON p.id = dv.id_producto " +
                     "WHERE p.activo = 1 " +
                     "GROUP BY p.id, p.nombre " +
                     "ORDER BY total_cant " + orden + ", p.nombre ASC " +
                     "LIMIT " + limite;

        try (Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()){
            while (rs.next()) {
                resultados.add(new Object[]{
                    rs.getString("nombre"),
                    rs.getInt("total_cant"),
                    "$" + String.format("%.2f", rs.getDouble("total_monto"))
                });
            }
        } catch(SQLException e){
            System.err.println("Error al obtener top de productos: " + e.getMessage());
        }
        return resultados;
    }
}