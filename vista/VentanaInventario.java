package vista;

//Interfaz de inventario

import modelo.*;
import controlador.*;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class VentanaInventario extends JDialog {
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;

    //Constructor
    public VentanaInventario(Frame parent){
        super(parent, "Gestión e Inventario de Productos", true);
        configurarVentana();
        inicializarComponentes();
        cargarInventario("");
    }

    //Configuracion de la ventana
    private void configurarVentana(){
        setSize(800, 500);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes(){
        //Buscador
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panelSuperior.add(new JLabel("Buscar Producto:"));

        txtBuscar = new JTextField(20);
        txtBuscar.setFont(new Font("Arial", Font.PLAIN, 14));
        //Buscar en tiwmpo real conforme escribes
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                cargarInventario(txtBuscar.getText().trim());
            }
        });
        panelSuperior.add(txtBuscar);

        add(panelSuperior, BorderLayout.NORTH);

        //Tabla de inventario
        String[] columnas = {"ID", "Producto", "Categoría", "Precio", "Stock Actual", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column){ return false;}
        };

        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaInventario.setRowHeight(28);
        tablaInventario.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        tablaInventario.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String estado = (String) table.getValueAt(row, 5);

                //Aplicar estilos de fondo en base al stock
                if(!isSelected){
                    if("AGOTADO".equals(estado)){
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(Color.RED);
                    }else if("STOCK BAJO".equals(estado)){
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(133, 100, 4));
                    }else{
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });

        add(new JScrollPane(tablaInventario), BorderLayout.CENTER);

        //Botones para manejar los datos
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        JButton btnReabastecer = new JButton("➕ Reabastecer / Ajustar Stock");
        btnReabastecer.setFont(new Font("Arial", Font.BOLD, 13));
        btnReabastecer.setBackground(new Color(40, 167, 69));
        btnReabastecer.setForeground(Color.WHITE);
        btnReabastecer.addActionListener(e -> ajustarStock());

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setFont(new Font("Arial", Font.PLAIN, 13));
        btnCerrar.addActionListener(e -> dispose());

        panelInferior.add(btnReabastecer);
        panelInferior.add(btnCerrar);

        add(panelInferior, BorderLayout.SOUTH);
    }

    //Obtener la informacion del inventario
    private void cargarInventario(String filtro){
        modeloTabla.setRowCount(0);
        String sql = "SELECT id, nombre, categoria, precio, COALESCE(stock, 0) AS stock " +
                     "FROM productos WHERE activo = 1 AND nombre LIKE ? ORDER BY categoria, nombre";

        try(Connection conn = ConexionBD.conectar();PreparedStatement pstmt = conn.prepareStatement(sql)){

            pstmt.setString(1, "%" + filtro + "%");
            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");

                //Clasificacion de nivel de inventario
                String estado = "SUFICIENTE";
                if (stock <= 0) {
                    estado = "AGOTADO";
                } else if (stock <= 5) {
                    estado = "STOCK BAJO";
                }

                modeloTabla.addRow(new Object[]{
                    id,
                    nombre,
                    categoria,
                    "$" + String.format("%.2f", precio),
                    stock,
                    estado
                });
            }
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error al cargar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Cambia el stock
    private void ajustarStock(){
        int fila = tablaInventario.getSelectedRow();
        if(fila == -1){
            JOptionPane.showMessageDialog(this, "Selecciona un producto de la tabla para ajustar su stock.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = (int) tablaInventario.getValueAt(fila, 0);
        String nombreProducto = (String) tablaInventario.getValueAt(fila, 1);
        int stockActual = (int) tablaInventario.getValueAt(fila, 4);

        //Desplegar dialogo para solicitar el nuevo stock
        String input = JOptionPane.showInputDialog(
                this,
                "Producto: " + nombreProducto + "\nStock actual: " + stockActual + "\n\nIngrese la nueva cantidad total en inventario:",
                "Reabastecer Inventario",
                JOptionPane.QUESTION_MESSAGE
        );

        if(input != null && !input.trim().isEmpty()){
            try{
                int nuevoStock = Integer.parseInt(input.trim());
                if(nuevoStock < 0){
                    JOptionPane.showMessageDialog(this, "El stock no puede ser un número negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //Guardar la actualizacion en la base de datos
                actualizarStockEnBD(idProducto, nuevoStock);
                cargarInventario(txtBuscar.getText().trim());
            }catch(NumberFormatException e){
                JOptionPane.showMessageDialog(this, "Ingrese un número entero válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Ejecuta la modificacion en la base de datos
    private void actualizarStockEnBD(int idProducto, int nuevoStock){
        String sql = "UPDATE productos SET stock = ? WHERE id = ?";
        try(Connection conn = ConexionBD.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, idProducto);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Stock actualizado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error al actualizar el stock: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}