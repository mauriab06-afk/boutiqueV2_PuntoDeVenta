package vista;

//Interfaz de el catalogo de ṕroductos

import modelo.*;
import controlador.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaCatalogo extends JDialog {
    private final ProductoDAO productoDAO; //Operaciones en la base de datos
    private final Runnable alCerrarCallback; //Refrescar componentes de la ventana padre

    //Tabla de productos
    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;

    //Leer nuevos datos
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtPrecio;
    private JTextField txtCategoria;

    //Botones para manejar datos
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;

    //Constructor
    public VentanaCatalogo(Frame parent, Runnable alCerrarCallback){
        super(parent, "Gestión de Catálogo de Productos", true);
        this.productoDAO = new ProductoDAO();
        this.alCerrarCallback = alCerrarCallback;

        configurarVentana();
        inicializarComponentes();
        cargarTablaProductos();
    }

    //Configurar ventana (Tamaño, posicion)
    private void configurarVentana(){
        setSize(1000, 600);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        setResizable(true);
    }

    //Iniciar componentes (Formulario, tabla)
    private void inicializarComponentes(){
        //Formulario (recibimos datos)
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Producto"));
        panelFormulario.setPreferredSize(new Dimension(340, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField();
        txtId.setEditable(false);
        txtNombre = new JTextField();
        txtPrecio = new JTextField();
        txtCategoria = new JTextField();

        int row = 0;
        agregarCampoFormulario(panelFormulario, gbc, "ID:", txtId, row++);
        agregarCampoFormulario(panelFormulario, gbc, "Nombre:", txtNombre, row++);
        agregarCampoFormulario(panelFormulario, gbc, "Precio ($):", txtPrecio, row++);
        agregarCampoFormulario(panelFormulario, gbc, "Categoría:", txtCategoria, row++);

        //Botones para manejar datos
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        
        JPanel subPanelSuper = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnGuardar = new JButton("Guardar");
        btnNuevo = new JButton("Limpiar / Nuevo");

        btnGuardar.setBackground(new Color(70, 130, 180));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFont(new Font("Arial", Font.BOLD, 13));

        //Manejar eventos
        btnGuardar.addActionListener(e -> guardarOActualizarProducto());
        btnNuevo.addActionListener(e -> limpiarFormulario());

        subPanelSuper.add(btnGuardar);
        subPanelSuper.add(btnNuevo);

        btnEliminar = new JButton("Eliminar Producto");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminar.setFocusPainted(false);
        btnEliminar.addActionListener(e -> eliminarProducto());

        panelBotones.add(subPanelSuper);
        panelBotones.add(btnEliminar);

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        panelFormulario.add(panelBotones, gbc);

        add(panelFormulario, BorderLayout.WEST);

        //Tabla de productos
        String[] columnas = {"ID", "Nombre", "Precio", "Categoría"};
        modeloTablaProductos = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        tablaProductos = new JTable(modeloTablaProductos);
        tablaProductos.setFont(new Font("Arial", Font.PLAIN, 14));
        tablaProductos.setRowHeight(26);
        tablaProductos.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        //Anchos para las columnas
        tablaProductos.getColumnModel().getColumn(0).setPreferredWidth(50);  
        tablaProductos.getColumnModel().getColumn(1).setPreferredWidth(300); 
        tablaProductos.getColumnModel().getColumn(2).setPreferredWidth(90);  
        tablaProductos.getColumnModel().getColumn(3).setPreferredWidth(140); 

        //Seleccion de fila (Producto)
        tablaProductos.getSelectionModel().addListSelectionListener(e -> {
            if(!e.getValueIsAdjusting() && tablaProductos.getSelectedRow() != -1){
                cargarProductoSeleccionado(tablaProductos.getSelectedRow());
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaProductos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Catálogo de Productos"));

        add(scrollTabla, BorderLayout.CENTER);
    }


    private void agregarCampoFormulario(JPanel panel, GridBagConstraints gbc, String etiqueta, JComponent campo, int row){
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        panel.add(new JLabel(etiqueta), gbc);
        gbc.gridx = 1; gbc.gridy = row;
        panel.add(campo, gbc);
    }

    //Cargar productos a la tabla
    private void cargarTablaProductos(){
        modeloTablaProductos.setRowCount(0);
        List<Producto> productos = productoDAO.obtenerActivos();
        for(Producto p : productos){
            Object[] fila = {
                p.getId(),
                p.getNombre(),
                "$" + p.getPrecio(),
                p.getCategoria()
            };
            modeloTablaProductos.addRow(fila);
        }
    }

    //Copiar info de fila selccionada a campos de texto
    private void cargarProductoSeleccionado(int filaIndex){ 
        txtId.setText(tablaProductos.getValueAt(filaIndex, 0).toString());
        txtNombre.setText(tablaProductos.getValueAt(filaIndex, 1).toString());
        txtPrecio.setText(tablaProductos.getValueAt(filaIndex, 2).toString().replace("$", ""));
        txtCategoria.setText(tablaProductos.getValueAt(filaIndex, 3).toString());
    }

    //Editar o agregar un producto nuevo
    private void guardarOActualizarProducto(){
        try{
            String nombre = txtNombre.getText().trim();
            String precioText = txtPrecio.getText().trim();
            String categoria = txtCategoria.getText().trim();

            if(nombre.isEmpty() || precioText.isEmpty() || categoria.isEmpty()){
                JOptionPane.showMessageDialog(this, "Por favor completa todos los campos.", "Atención", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double precio = Double.parseDouble(precioText);

            //Si id no tiene texto, es un nuevo producto
            if(txtId.getText().isEmpty()){
                Producto nuevo = new Producto(nombre, precio, categoria);
                nuevo.setActivo(true);
                if(productoDAO.guardar(nuevo)){
                    JOptionPane.showMessageDialog(this, "Producto guardado con éxito.");
                }
            }else{ //Si lo tiene, es un producto nuevo
                int id = Integer.parseInt(txtId.getText());
                Producto existente = new Producto(id, nombre, precio, categoria, true);
                if (productoDAO.actualizar(existente)) {
                    JOptionPane.showMessageDialog(this, "Producto actualizado con éxito.");
                }
            }

            limpiarFormulario();
            cargarTablaProductos();

            //Actualizar ventanas
            if(alCerrarCallback != null){
                alCerrarCallback.run();
            }

        }catch(NumberFormatException e){
            JOptionPane.showMessageDialog(this, "Ingrese un precio numérico válido.", "Error de validación", JOptionPane.ERROR_MESSAGE);
        }catch(Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Se desactiva el producto (Elimina)
    private void eliminarProducto(){
        if(txtId.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Por favor, selecciona un producto de la tabla para eliminar.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(txtId.getText());
        String nombre = txtNombre.getText();

        Object[] opciones = {"Sí", "No"};
        int confirmacion = JOptionPane.showOptionDialog(
                this,
                "¿Desea eliminar el producto '" + nombre + "' del catálogo?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if(confirmacion == 0){
            //Se cambia false
            Producto producto = new Producto(id, nombre, Double.parseDouble(txtPrecio.getText()), txtCategoria.getText(), false);
            boolean exito = productoDAO.actualizar(producto);

            if(exito){
                JOptionPane.showMessageDialog(this, "Producto eliminado con éxito.", "Eliminado", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario();
                cargarTablaProductos();

                if(alCerrarCallback != null){
                    alCerrarCallback.run();
                }
            }else{
                JOptionPane.showMessageDialog(this, "Ocurrió un error al intentar eliminar el producto.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Limpia el formulario
    private void limpiarFormulario(){
        txtId.setText("");
        txtNombre.setText("");
        txtPrecio.setText("");
        txtCategoria.setText("");
        tablaProductos.clearSelection();
    }
}