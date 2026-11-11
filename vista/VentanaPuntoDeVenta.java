package vista;

//Interfaz principal de punto de venta

import controlador.GestionBackup;
import controlador.PuntoDeVentaControlador;
import modelo.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaPuntoDeVenta extends JFrame {
    private final PuntoDeVentaControlador controlador;
    private final Usuario usuarioActivo; //Usuario actual

    private JTabbedPane tabCategorias;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotal;
    private boolean actualizandoTabla = false;

    //Constructor
    public VentanaPuntoDeVenta(Usuario usuarioActivo){
        this.usuarioActivo = (usuarioActivo != null)
            ? usuarioActivo
            : new Usuario(1, "admin", "admin123", "ADMINISTRADOR", true);

        this.controlador = new PuntoDeVentaControlador(this);

        GestionBackup.verificarYRestaurarSiFalta();
        ConexionBD.inicializarBaseDatos();
        GestionBackup.crearCopiaAutomatica();

        configurarVentana();
        inicializarComponentes();
        cargarCategoriasYProductos();
    }

    /*
    public VentanaPuntoDeVenta(){
        this(new Usuario(1, "admin", "admin123", "ADMINISTRADOR", true));
    }
    */

    //Configuracion de la ventana
    private void configurarVentana(){
        setTitle("La Boutique del Maíz y el Nopal - Punto de Venta");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    private void inicializarComponentes(){
        //Panel superior
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(new Color(45, 45, 45));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        String infoUsuario = " 👤 " + usuarioActivo.getUsuario() + " [" + usuarioActivo.getRol() + "]";
        JLabel lblTitulo = new JLabel("🌽 La Boutique del Maíz y el Nopal 🌵" + infoUsuario);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);

        JPanel panelBotonesSup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotonesSup.setOpaque(false);

        //Botones de navegacion (Botones a otras pantallas)
        JButton btnInventario = crearBotonNavegacion("📦 Inventario", new Color(40, 167, 69));
        btnInventario.addActionListener(e -> {
            VentanaInventario vInv = new VentanaInventario(this);
            vInv.setVisible(true);
            refrescarProductos();
        });

        JButton btnCatalogo = crearBotonNavegacion("📋 Catálogo", new Color(0, 123, 255));
        btnCatalogo.addActionListener(e -> {
            VentanaCatalogo vCat = new VentanaCatalogo(this, this::refrescarProductos);
            vCat.setVisible(true);
            refrescarProductos();
        });

        JButton btnReportes = crearBotonNavegacion("📊 Reportes", new Color(108, 117, 125));
        btnReportes.addActionListener(e -> {
            VentanaReportes vRep = new VentanaReportes(this);
            vRep.setVisible(true);
        });

        JButton btnUsuarios = crearBotonNavegacion("👥 Usuarios", new Color(111, 66, 193));
        btnUsuarios.addActionListener(e -> {
            VentanaUsuarios vUsr = new VentanaUsuarios(this);
            vUsr.setVisible(true);
        });

        JButton btnBackup = crearBotonNavegacion("💾 Backup Manual", new Color(255, 193, 7));
        btnBackup.setForeground(Color.BLACK);
        btnBackup.addActionListener(e -> GestionBackup.crearCopiaManual(this));

        //Dependiendo el rol son las ventanas disponibles
        if(!"ADMINISTRADOR".equalsIgnoreCase(usuarioActivo.getRol())){
            btnCatalogo.setEnabled(false);
            btnReportes.setEnabled(false);
            btnUsuarios.setEnabled(false);
            btnBackup.setEnabled(false);

            btnCatalogo.setToolTipText("Requiere permisos de Administrador");
            btnReportes.setToolTipText("Requiere permisos de Administrador");
            btnUsuarios.setToolTipText("Requiere permisos de Administrador");
            btnBackup.setToolTipText("Requiere permisos de Administrador");
        }

        panelBotonesSup.add(btnInventario);
        panelBotonesSup.add(btnCatalogo);
        panelBotonesSup.add(btnReportes);
        panelBotonesSup.add(btnUsuarios);
        panelBotonesSup.add(btnBackup);

        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        panelSuperior.add(panelBotonesSup, BorderLayout.EAST);

        add(panelSuperior, BorderLayout.NORTH);

        //Catalogo de productos
        tabCategorias = new JTabbedPane();
        tabCategorias.setFont(new Font("Arial", Font.BOLD, 13));

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder("Catálogo de Productos"));
        panelIzquierdo.add(tabCategorias, BorderLayout.CENTER);

        //Informacion de carrito
        JPanel panelDerecho = new JPanel(new BorderLayout(10, 10));
        panelDerecho.setBorder(BorderFactory.createTitledBorder("Comanda / Carrito de Compra (Edición Directa)"));
        panelDerecho.setPreferredSize(new Dimension(450, 0));

        String[] columnas = {"Producto", "Cant.", "Precio U.", "Subtotal"};

        modeloCarrito = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return column == 1; //Únicamente la columna de cantidad es editable
            }
        };

        //Escuchador de edición en la cantidad
        modeloCarrito.addTableModelListener(e -> {
            if (actualizandoTabla) return;
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 1){
                int fila = e.getFirstRow();
                Object val = modeloCarrito.getValueAt(fila, 1);
                try{
                    int nuevaCant = Integer.parseInt(val.toString().trim());
                    controlador.actualizarCantidadItem(fila, nuevaCant);
                }catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(this, "Ingrese una cantidad entera válida.", "Error", JOptionPane.ERROR_MESSAGE);
                    actualizarTablaCarrito();
                }
            }
        });

        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaCarrito.setRowHeight(26);
        tablaCarrito.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        panelDerecho.add(new JScrollPane(tablaCarrito), BorderLayout.CENTER);

        //Boton de quitar producto del carrito
        JPanel panelControlesCarrito = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        JButton btnEliminar = new JButton("🗑️ Quitar Producto");
        btnEliminar.setFont(new Font("Arial", Font.BOLD, 13));
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.addActionListener(e -> controlador.eliminarItemSeleccionado(tablaCarrito.getSelectedRow()));

        panelControlesCarrito.add(btnEliminar);

        //Total del carrito y boton de compra
        JPanel panelTotalCobro = new JPanel(new BorderLayout(10, 10));
        panelTotalCobro.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotal = new JLabel("Total: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setForeground(new Color(34, 139, 34));

        JButton btnCobrar = new JButton("💳 REGISTRAR PAGO / COBRAR");
        btnCobrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnCobrar.setBackground(new Color(40, 167, 69));
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setPreferredSize(new Dimension(0, 45));
        btnCobrar.addActionListener(e -> controlador.procesarCobro());

        panelTotalCobro.add(lblTotal, BorderLayout.NORTH);
        panelTotalCobro.add(panelControlesCarrito, BorderLayout.CENTER);
        panelTotalCobro.add(btnCobrar, BorderLayout.SOUTH);

        panelDerecho.add(panelTotalCobro, BorderLayout.SOUTH);

        //Divisor
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelIzquierdo, panelDerecho);
        splitPane.setResizeWeight(0.6);
        add(splitPane, BorderLayout.CENTER);
    }

    //Creacion de boton
    private JButton crearBotonNavegacion(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Arial", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    //Pestañas de categorias de productos
    public void cargarCategoriasYProductos(){
        tabCategorias.removeAll();
        List<String> categorias = controlador.obtenerCategorias();

        for(String cat : categorias){ //Crear categoria
            JPanel panelGrid = new JPanel(new GridLayout(0, 2, 10, 10));
            panelGrid.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            List<Producto> productos = controlador.obtenerProductosPorCategoria(cat);
            for(Producto p : productos){ //En cada categoria añadir los productos correspondientes
                JButton btnProducto = new JButton(
                    "<html><center><b>" + p.getNombre() + "</b><br><font color='green'>$" +
                    String.format("%.2f", p.getPrecio()) + "</font><br><font color='gray'>Stock: " +
                    p.getStock() + "</font></center></html>"
                );
                btnProducto.setFont(new Font("Arial", Font.PLAIN, 12));
                btnProducto.setPreferredSize(new Dimension(140, 70));

                if(p.getStock() <= 0){
                    btnProducto.setEnabled(false);
                    btnProducto.setToolTipText("Producto agotado");
                }else{
                    btnProducto.addActionListener(e -> controlador.agregarAlCarrito(p));
                }
                panelGrid.add(btnProducto);
            }

            JScrollPane scrollPanel = new JScrollPane(panelGrid);
            scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
            tabCategorias.addTab(cat, scrollPanel);
        }
    }

    //Revisa denuevo la base de taos y actualiza el contenido
    public void refrescarProductos(){
        cargarCategoriasYProductos();
        actualizarTablaCarrito();
    }


    //Conforme se añadan productos que se actualice
    public void actualizarTablaCarrito(){
        actualizandoTabla = true;
        modeloCarrito.setRowCount(0);
        double total = 0.0;
        for (ItemCarrito item : controlador.getCarrito()){
            double subtotal = item.calcularSubtotal();
            total += subtotal;
            modeloCarrito.addRow(new Object[]{
                item.getProducto().getNombre(),
                item.getCantidad(),
                "$" + String.format("%.2f", item.getPrecioUnitario()),
                "$" + String.format("%.2f", subtotal)
            });
        }
        lblTotal.setText(String.format("Total: $%.2f", total));
        actualizandoTabla = false;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
}