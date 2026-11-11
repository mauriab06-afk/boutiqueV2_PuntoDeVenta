package modelo;

//Consultas a la tabla de productos

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO{

    //Obtiene todas las categorias
    public List<String> obtenerCategorias(){
        List<String> categorias = new ArrayList<>();
        String sql = "SELECT DISTINCT categoria FROM productos WHERE activo = 1 ORDER BY categoria";

        //Intentamos la conexion
        try(Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()){
            while(rs.next()){
                categorias.add(rs.getString("categoria"));
            }
        }catch(SQLException e){
            System.err.println("Error al obtener categorías: " + e.getMessage());
        }
        return categorias;
    }

    //Obtenemos los productos por categoria
    public List<Producto> obtenerProductosPorCategoria(String categoria){
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, categoria, activo, COALESCE(stock, 0) AS stock " +
                     "FROM productos WHERE activo = 1 AND categoria = ? ORDER BY nombre";

        //Intentamos la conexion
        try(Connection conn = ConexionBD.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, categoria);
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){ //Generamos un producto por cada uno que leamos
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("categoria"),
                    rs.getInt("activo") == 1,
                    rs.getInt("stock")
                );
                productos.add(p);
            }
        }catch (SQLException e){
            System.err.println("Error al obtener productos por categoría: " + e.getMessage());
        }
        return productos;
    }

    //Obtener todos los productos sin importar la categoria
    public List<Producto> obtenerTodos(){
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, categoria, activo, COALESCE(stock, 0) AS stock FROM productos ORDER BY categoria, nombre";

        //Intentamos la conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()){
            while (rs.next()){
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("categoria"),
                    rs.getInt("activo") == 1,
                    rs.getInt("stock")
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo completo: " + e.getMessage());
        }
        return productos;
    }

    //Guardar un nuevo producto en la base de datos
    public boolean guardar(Producto p) {
        String sql = "INSERT INTO productos (nombre, precio, categoria, activo, stock) VALUES (?, ?, ?, ?, ?)";
        //INtentamos la conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, p.getNombre());
            pstmt.setDouble(2, p.getPrecio());
            pstmt.setString(3, p.getCategoria());
            pstmt.setInt(4, p.isActivo() ? 1 : 0);
            pstmt.setInt(5, p.getStock());
            return pstmt.executeUpdate() > 0;
        }catch (SQLException e){
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    //Actualizar el producto
    public boolean actualizar(Producto p){
        String sql = "UPDATE productos SET nombre = ?, precio = ?, categoria = ?, activo = ?, stock = ? WHERE id = ?";
        //Intentamos la conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, p.getNombre());
            pstmt.setDouble(2, p.getPrecio());
            pstmt.setString(3, p.getCategoria());
            pstmt.setInt(4, p.isActivo() ? 1 : 0);
            pstmt.setInt(5, p.getStock());
            pstmt.setInt(6, p.getId());
            return pstmt.executeUpdate() > 0;
        }catch(SQLException e){
            System.err.println("Error al actualizar producto: " + e.getMessage());
            return false;
        }
    }

    //Buscar productos activos
    public List<Producto> obtenerActivos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT id, nombre, precio, categoria, activo, COALESCE(stock, 0) AS stock " +
                     "FROM productos WHERE activo = 1 ORDER BY categoria, nombre";

        //INtentar conexion
        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()){
            while (rs.next()) {
                productos.add(new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getDouble("precio"),
                    rs.getString("categoria"),
                    rs.getInt("activo") == 1,
                    rs.getInt("stock")
                ));
            }
        }catch(SQLException e){
            System.err.println("Error al obtener productos activos: " + e.getMessage());
        }
        return productos;
    }
}