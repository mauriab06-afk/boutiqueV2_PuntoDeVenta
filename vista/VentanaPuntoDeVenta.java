package vista;

//Interfaz principal moderna del punto de venta

import controlador.PuntoDeVentaControlador;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import modelo.ItemCarrito;
import modelo.Producto;
import modelo.Usuario;

public class VentanaPuntoDeVenta extends JFrame{
    //Controlador y usuario que inicio sesion
    private final PuntoDeVentaControlador controlador;
    private final Usuario usuarioActivo;

    //Paleta de colores del diseno
    private final Color VERDE_HEADER = new Color(26, 108, 52);
    private final Color VERDE_BOTON_SUP = new Color(46, 128, 72);
    private final Color VERDE_PRIMARIO = new Color(34, 153, 72);
    private final Color FONDO_CREMA = new Color(247, 245, 238);
    private final Color PILDORA_STOCK_BG = new Color(214, 240, 221);
    private final Color PILDORA_STOCK_TXT = new Color(24, 118, 54);
    private final Color ROJO_ALERTA = new Color(220, 53, 69);

    //Componentes dinamicos de catalogo y carrito
    private JPanel panelPildorasCategorias;
    private JPanel panelGridProductos;
    private JTextField txtBuscarProducto;
    private JPanel panelListaCarrito;
    private JLabel lblTotal;
    private JLabel lblReloj;

    private String categoriaActiva = "";
    private int indiceItemSeleccionado = -1;

    //Constructor principal
    public VentanaPuntoDeVenta(Usuario usuarioActivo){
        this.usuarioActivo = (usuarioActivo != null)
            ? usuarioActivo
            : new Usuario(1, "admin", "admin123", "ADMINISTRADOR", true);

        this.controlador = new PuntoDeVentaControlador(this);
        this.controlador.inicializarSistema();

        configurarVentana();
        inicializarComponentes();
        cargarCategoriasYProductos();
        iniciarReloj();
    }

    //Configura tamano, titulo y fondo general
    private void configurarVentana(){
        setTitle("La Boutique del Maíz y el Nopal - Punto de Venta");
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(FONDO_CREMA);
    }

    //Asegura que la ventana abra en pantalla completa en Linux
    @Override
    public void setVisible(boolean b){
        super.setVisible(b);
        if(b){
            SwingUtilities.invokeLater(() -> setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH));
        }
    }

    //Arma la barra superior verde y el area dividida inferior
    private void inicializarComponentes(){
        //Barra superior verde bosque
        JPanel panelSuperior = new JPanel(new BorderLayout(15, 0));
        panelSuperior.setBackground(VERDE_HEADER);
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 22));

        //Lado izquierdo del header con logo circular y nombre del negocio
        JPanel panelMarca = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panelMarca.setOpaque(false);

        JPanel medallonLogo = new JPanel(new BorderLayout()){
            @Override
            protected void paintComponent(Graphics g){
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int s = Math.min(getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.fill(new Ellipse2D.Double(0, 0, s, s));
            }
        };
        medallonLogo.setOpaque(false);
        medallonLogo.setPreferredSize(new Dimension(74, 74));
        medallonLogo.setBorder(BorderFactory.createEmptyBorder(7, 7, 7, 7));

        JLabel lblLogo = new JLabel("", SwingConstants.CENTER);
        cargarImagenLogo(lblLogo, 58);
        medallonLogo.add(lblLogo, BorderLayout.CENTER);

        JLabel lblTitulo = new JLabel("<html><div style='line-height:1.05;'>La Boutique del<br>Maíz y el Nopal</div></html>");
        lblTitulo.setFont(new Font("Serif", Font.BOLD | Font.ITALIC, 26));
        lblTitulo.setForeground(Color.WHITE);

        panelMarca.add(medallonLogo);
        panelMarca.add(lblTitulo);

        //Lado derecho del header con info de usuario arriba y botones abajo
        JPanel panelDerechoHeader = new JPanel(new BorderLayout(0, 8));
        panelDerechoHeader.setOpaque(false);

        JPanel panelInfoUsuario = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        panelInfoUsuario.setOpaque(false);

        JLabel lblUsuario = new JLabel("● " + usuarioActivo.getUsuario() + " [" + usuarioActivo.getRol() + "]");
        lblUsuario.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblUsuario.setForeground(Color.WHITE);

        lblReloj = new JLabel("");
        lblReloj.setFont(new Font("SansSerif", Font.PLAIN, 13));
        lblReloj.setForeground(new Color(210, 240, 220));

        panelInfoUsuario.add(lblUsuario);
        panelInfoUsuario.add(lblReloj);

        //Fila de botones de navegacion superiores con simbolos compatibles
        JPanel panelBotonesSup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelBotonesSup.setOpaque(false);

        JButton btnInventario = crearBotonHeader("⊞", "Inventario");
        btnInventario.addActionListener(e -> controlador.abrirInventario());

        JButton btnCatalogo = crearBotonHeader("☰", "Catálogo");
        btnCatalogo.addActionListener(e -> controlador.abrirCatalogo());

        JButton btnReportes = crearBotonHeader("▤", "Reportes");
        btnReportes.addActionListener(e -> controlador.abrirReportes());

        JButton btnMatriz = crearBotonHeader("↗", "Matriz");
        btnMatriz.addActionListener(e -> controlador.abrirMatrizVentas());

        JButton btnUsuarios = crearBotonHeader("☺", "Usuarios");
        btnUsuarios.addActionListener(e -> controlador.abrirUsuarios());

        JButton btnBackup = crearBotonHeader("⤓", "Backup");
        btnBackup.addActionListener(e -> controlador.ejecutarBackupManual());

        JButton btnManual = crearBotonHeader("?", "Manual");
        btnManual.addActionListener(e -> controlador.abrirGuiaUsuario());

        JButton btnSalir = crearBotonHeader("⏻", "Salir");
        btnSalir.setBackground(new Color(185, 45, 58));
        btnSalir.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
        });

        //Restriccion de botones segun rol
        if(!"ADMINISTRADOR".equalsIgnoreCase(usuarioActivo.getRol())){
            btnCatalogo.setEnabled(false);
            btnReportes.setEnabled(false);
            btnUsuarios.setEnabled(false);
            btnBackup.setEnabled(false);
        }

        panelBotonesSup.add(btnInventario);
        panelBotonesSup.add(btnCatalogo);
        panelBotonesSup.add(btnReportes);
        panelBotonesSup.add(btnMatriz);
        panelBotonesSup.add(btnUsuarios);
        panelBotonesSup.add(btnBackup);
        panelBotonesSup.add(btnManual);
        panelBotonesSup.add(btnSalir);

        panelDerechoHeader.add(panelInfoUsuario, BorderLayout.NORTH);
        panelDerechoHeader.add(panelBotonesSup, BorderLayout.SOUTH);

        panelSuperior.add(panelMarca, BorderLayout.WEST);
        panelSuperior.add(panelDerechoHeader, BorderLayout.EAST);
        add(panelSuperior, BorderLayout.NORTH);

        //Cuerpo principal dividido en catalogo (izquierda) y comanda (derecha)
        JPanel panelCuerpo = new JPanel(new BorderLayout(20, 0));
        panelCuerpo.setBackground(FONDO_CREMA);
        panelCuerpo.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

        //Seccion izquierda: catalogo de productos
        JPanel panelIzquierdo = new JPanel(new BorderLayout(0, 12));
        panelIzquierdo.setOpaque(false);

        JPanel panelCabeceraCatalogo = new JPanel();
        panelCabeceraCatalogo.setOpaque(false);
        panelCabeceraCatalogo.setLayout(new BoxLayout(panelCabeceraCatalogo, BoxLayout.Y_AXIS));

        JLabel lblCatTitulo = new JLabel("Catálogo de Productos");
        lblCatTitulo.setFont(new Font("SansSerif", Font.BOLD, 19));
        lblCatTitulo.setForeground(new Color(40, 45, 42));
        lblCatTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Contenedor horizontal con scroll para pildoras de categoria
        panelPildorasCategorias = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelPildorasCategorias.setOpaque(false);

        JScrollPane scrollPildoras = new JScrollPane(panelPildorasCategorias);
        scrollPildoras.setBorder(null);
        scrollPildoras.setOpaque(false);
        scrollPildoras.getViewport().setOpaque(false);
        scrollPildoras.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPildoras.setPreferredSize(new Dimension(0, 44));
        scrollPildoras.setAlignmentX(Component.LEFT_ALIGNMENT);

        //Barra de busqueda de productos
        txtBuscarProducto = new JTextField();
        txtBuscarProducto.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtBuscarProducto.setPreferredSize(new Dimension(0, 38));
        txtBuscarProducto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtBuscarProducto.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtBuscarProducto.putClientProperty("JTextField.placeholderText", "⌕  Buscar producto...");
        txtBuscarProducto.putClientProperty("FlatLaf.style", "arc: 18");
        txtBuscarProducto.getDocument().addDocumentListener(new javax.swing.event.DocumentListener(){
            public void insertUpdate(javax.swing.event.DocumentEvent e){ filtrarYMostrarProductos(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e){ filtrarYMostrarProductos(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e){ filtrarYMostrarProductos(); }
        });

        panelCabeceraCatalogo.add(lblCatTitulo);
        panelCabeceraCatalogo.add(Box.createVerticalStrut(8));
        panelCabeceraCatalogo.add(scrollPildoras);
        panelCabeceraCatalogo.add(Box.createVerticalStrut(8));
        panelCabeceraCatalogo.add(txtBuscarProducto);

        //Cuadricula de tarjetas de productos
        panelGridProductos = new JPanel(new GridLayout(0, 3, 14, 14));
        panelGridProductos.setBackground(FONDO_CREMA);
        panelGridProductos.setBorder(BorderFactory.createEmptyBorder(4, 2, 10, 8));

        JPanel contenedorGridArriba = new JPanel(new BorderLayout());
        contenedorGridArriba.setBackground(FONDO_CREMA);
        contenedorGridArriba.add(panelGridProductos, BorderLayout.NORTH);

        JScrollPane scrollProductos = new JScrollPane(contenedorGridArriba);
        scrollProductos.setBorder(null);
        scrollProductos.getViewport().setBackground(FONDO_CREMA);
        scrollProductos.getVerticalScrollBar().setUnitIncrement(16);

        panelIzquierdo.add(panelCabeceraCatalogo, BorderLayout.NORTH);
        panelIzquierdo.add(scrollProductos, BorderLayout.CENTER);

        //Seccion derecha: comanda y cobro con mas ancho para nombres largos
        JPanel panelDerecho = new JPanel(new BorderLayout(0, 12));
        panelDerecho.setOpaque(false);
        panelDerecho.setPreferredSize(new Dimension(500, 0));

        //Tarjeta blanca para la lista del carrito
        JPanel tarjetaComanda = new JPanel(new BorderLayout(0, 10));
        tarjetaComanda.setBackground(Color.WHITE);
        tarjetaComanda.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        tarjetaComanda.putClientProperty("FlatLaf.style", "arc: 22");

        JPanel encabezadoComanda = new JPanel(new BorderLayout());
        encabezadoComanda.setOpaque(false);

        JLabel lblComanda = new JLabel("Comanda / Carrito de Compra");
        lblComanda.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblComanda.setForeground(new Color(35, 40, 38));

        JPanel subEncabezado = new JPanel(new BorderLayout());
        subEncabezado.setOpaque(false);
        subEncabezado.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 232, 230)),
            BorderFactory.createEmptyBorder(10, 4, 6, 4)
        ));
        JLabel lblColProd = new JLabel("Producto");
        lblColProd.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblColProd.setForeground(new Color(90, 95, 92));

        JLabel lblColCant = new JLabel("Cantidad             ");
        lblColCant.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblColCant.setForeground(new Color(90, 95, 92));

        subEncabezado.add(lblColProd, BorderLayout.WEST);
        subEncabezado.add(lblColCant, BorderLayout.EAST);

        encabezadoComanda.add(lblComanda, BorderLayout.NORTH);
        encabezadoComanda.add(subEncabezado, BorderLayout.SOUTH);

        panelListaCarrito = new JPanel();
        panelListaCarrito.setLayout(new BoxLayout(panelListaCarrito, BoxLayout.Y_AXIS));
        panelListaCarrito.setBackground(Color.WHITE);

        JScrollPane scrollCarrito = new JScrollPane(panelListaCarrito);
        scrollCarrito.setBorder(null);
        scrollCarrito.getViewport().setBackground(Color.WHITE);
        scrollCarrito.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollCarrito.getVerticalScrollBar().setUnitIncrement(14);

        tarjetaComanda.add(encabezadoComanda, BorderLayout.NORTH);
        tarjetaComanda.add(scrollCarrito, BorderLayout.CENTER);

        //Zona inferior de total y botones de accion
        JPanel panelCobroInferior = new JPanel();
        panelCobroInferior.setOpaque(false);
        panelCobroInferior.setLayout(new BoxLayout(panelCobroInferior, BoxLayout.Y_AXIS));

        lblTotal = new JLabel("TOTAL: $0.00", SwingConstants.RIGHT);
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTotal.setForeground(VERDE_HEADER);
        lblTotal.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JPanel filaQuitar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        filaQuitar.setOpaque(false);
        filaQuitar.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnQuitarSeleccionado = new JButton("✕ Quitar Producto");
        btnQuitarSeleccionado.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnQuitarSeleccionado.setBackground(ROJO_ALERTA);
        btnQuitarSeleccionado.setForeground(Color.WHITE);
        btnQuitarSeleccionado.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuitarSeleccionado.putClientProperty("FlatLaf.style", "arc: 14");
        btnQuitarSeleccionado.addActionListener(e -> {
            controlador.eliminarItemSeleccionado(indiceItemSeleccionado);
            indiceItemSeleccionado = -1;
        });
        filaQuitar.add(btnQuitarSeleccionado);

        //Botones finales de cobrar y vaciar carrito con simbolos compatibles
        JPanel filaBotonesFinales = new JPanel(new BorderLayout(10, 0));
        filaBotonesFinales.setOpaque(false);
        filaBotonesFinales.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        filaBotonesFinales.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JButton btnCobrar = new JButton("✓  REGISTRAR PAGO / COBRAR");
        btnCobrar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCobrar.setBackground(VERDE_PRIMARIO);
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setPreferredSize(new Dimension(0, 54));
        btnCobrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCobrar.putClientProperty("FlatLaf.style", "arc: 22");
        btnCobrar.addActionListener(e -> controlador.procesarCobro());

        JButton btnVaciar = new JButton("<html><center>✕<br>Vaciar<br>Carrito</center></html>");
        btnVaciar.setFont(new Font("SansSerif", Font.BOLD, 11));
        btnVaciar.setBackground(ROJO_ALERTA);
        btnVaciar.setForeground(Color.WHITE);
        btnVaciar.setPreferredSize(new Dimension(92, 54));
        btnVaciar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVaciar.putClientProperty("FlatLaf.style", "arc: 22");
        btnVaciar.addActionListener(e -> {
            controlador.getCarrito().clear();
            indiceItemSeleccionado = -1;
            actualizarTablaCarrito();
        });

        filaBotonesFinales.add(btnCobrar, BorderLayout.CENTER);
        filaBotonesFinales.add(btnVaciar, BorderLayout.EAST);

        panelCobroInferior.add(lblTotal);
        panelCobroInferior.add(Box.createVerticalStrut(4));
        panelCobroInferior.add(filaQuitar);
        panelCobroInferior.add(Box.createVerticalStrut(8));
        panelCobroInferior.add(filaBotonesFinales);

        panelDerecho.add(tarjetaComanda, BorderLayout.CENTER);
        panelDerecho.add(panelCobroInferior, BorderLayout.SOUTH);

        panelCuerpo.add(panelIzquierdo, BorderLayout.CENTER);
        panelCuerpo.add(panelDerecho, BorderLayout.EAST);
        add(panelCuerpo, BorderLayout.CENTER);
    }

    //Crea los botones superiores con simbolo arriba y texto abajo bien centrado
    private JButton crearBotonHeader(String simbolo, String texto){
        JButton btn = new JButton("<html><div style='text-align:center;'><span style='font-size:15px;'>" + simbolo + "</span><br>" + texto + "</div></html>");
        btn.setFont(new Font("SansSerif", Font.BOLD, 11));
        btn.setBackground(VERDE_BOTON_SUP);
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(94, 54));
        btn.setMargin(new Insets(4, 6, 4, 6));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc: 14");
        return btn;
    }
    
    //Carga las categorias en forma de pildoras y muestra sus productos
    public void cargarCategoriasYProductos(){
        List<String> categorias = controlador.obtenerCategorias();
        if(!categorias.isEmpty() && (categoriaActiva.isEmpty() || !categorias.contains(categoriaActiva))){
            categoriaActiva = categorias.get(0);
        }
        dibujarPildorasCategorias(categorias);
        filtrarYMostrarProductos();
    }

    //Pinta las pildoras de categorias marcando en verde la seleccionada
    private void dibujarPildorasCategorias(List<String> categorias){
        panelPildorasCategorias.removeAll();
        for(String cat : categorias){
            boolean activa = cat.equals(categoriaActiva);
            JButton btnCat = new JButton(cat.toUpperCase());
            btnCat.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnCat.setFocusPainted(false);
            btnCat.setBorderPainted(false);
            btnCat.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnCat.putClientProperty("FlatLaf.style", "arc: 20");

            if(activa){
                btnCat.setBackground(VERDE_HEADER);
                btnCat.setForeground(Color.WHITE);
            }else{
                btnCat.setBackground(Color.WHITE);
                btnCat.setForeground(new Color(60, 65, 62));
            }

            btnCat.addActionListener(e -> {
                categoriaActiva = cat;
                dibujarPildorasCategorias(categorias);
                filtrarYMostrarProductos();
            });
            panelPildorasCategorias.add(btnCat);
        }
        panelPildorasCategorias.revalidate();
        panelPildorasCategorias.repaint();
    }

    //Filtra los productos de la categoria activa segun el texto buscado
    private void filtrarYMostrarProductos(){
        panelGridProductos.removeAll();
        if(categoriaActiva.isEmpty()) return;

        String filtro = txtBuscarProducto != null ? txtBuscarProducto.getText().trim().toLowerCase() : "";
        List<Producto> productos = controlador.obtenerProductosPorCategoria(categoriaActiva);

        for(Producto p : productos){
            if(!filtro.isEmpty() && !p.getNombre().toLowerCase().contains(filtro)){
                continue;
            }
            panelGridProductos.add(crearTarjetaProducto(p));
        }

        panelGridProductos.revalidate();
        panelGridProductos.repaint();
    }

    //Construye cada tarjeta blanca limpia de producto sin imagen
    private JPanel crearTarjetaProducto(Producto p){
        boolean agotado = p.getStock() <= 0;

        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(18, 14, 18, 14));
        tarjeta.putClientProperty("FlatLaf.style", "arc: 20");

        //Nombre del producto centrado
        JLabel lblNombre = new JLabel("<html><center>" + p.getNombre().toUpperCase() + "</center></html>", SwingConstants.CENTER);
        lblNombre.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNombre.setForeground(agotado ? new Color(150, 150, 150) : new Color(35, 38, 36));
        lblNombre.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Precio en verde grande
        JLabel lblPrecio = new JLabel(String.format("$%.2f", p.getPrecio()), SwingConstants.CENTER);
        lblPrecio.setFont(new Font("SansSerif", Font.BOLD, 20));
        lblPrecio.setForeground(agotado ? new Color(160, 160, 160) : VERDE_PRIMARIO);
        lblPrecio.setAlignmentX(Component.CENTER_ALIGNMENT);

        //Pildora de stock disponible o etiqueta de agotado
        JLabel lblStock = new JLabel(agotado ? "AGOTADO" : "Stock: " + p.getStock(), SwingConstants.CENTER);
        lblStock.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblStock.setOpaque(true);
        lblStock.setBorder(BorderFactory.createEmptyBorder(4, 14, 4, 14));
        lblStock.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblStock.putClientProperty("FlatLaf.style", "arc: 14");

        if(agotado){
            lblStock.setBackground(ROJO_ALERTA);
            lblStock.setForeground(Color.WHITE);
        }else{
            lblStock.setBackground(PILDORA_STOCK_BG);
            lblStock.setForeground(PILDORA_STOCK_TXT);
        }

        tarjeta.add(Box.createVerticalGlue());
        tarjeta.add(lblNombre);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblPrecio);
        tarjeta.add(Box.createVerticalStrut(10));
        tarjeta.add(lblStock);
        tarjeta.add(Box.createVerticalGlue());

        //Clic en toda la tarjeta para agregar al carrito
        if(!agotado){
            tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tarjeta.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e){
                    controlador.agregarAlCarrito(p);
                }
                @Override
                public void mouseEntered(MouseEvent e){
                    tarjeta.setBackground(new Color(244, 252, 246));
                }
                @Override
                public void mouseExited(MouseEvent e){
                    tarjeta.setBackground(Color.WHITE);
                }
            });
        }

        return tarjeta;
    }

    //Vuelve a cargar categorias, productos y carrito
    public void refrescarProductos(){
        cargarCategoriasYProductos();
        actualizarTablaCarrito();
    }

    //Dibuja cada renglon del carrito asegurando espacio para nombres largos y botones
    public void actualizarTablaCarrito(){
        panelListaCarrito.removeAll();
        List<ItemCarrito> items = controlador.getCarrito();
        double total = 0.0;

        for(int i = 0; i < items.size(); i++){
            final int indice = i;
            ItemCarrito item = items.get(i);
            double subtotal = item.calcularSubtotal();
            total += subtotal;

            JPanel fila = new JPanel(new BorderLayout(10, 0));
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
            fila.setPreferredSize(new Dimension(440, 68));
            fila.setBackground(indice == indiceItemSeleccionado ? new Color(235, 248, 239) : Color.WHITE);
            fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(238, 240, 238)),
                BorderFactory.createEmptyBorder(8, 6, 8, 6)
            ));

            //Permite seleccionar la fila con clic
            fila.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e){
                    indiceItemSeleccionado = indice;
                    actualizarTablaCarrito();
                }
            });

            //Nombre con ajuste de linea y subtotal a la izquierda
            JLabel lblInfoProd = new JLabel("<html><div style='width:190px;'><b>" + item.getProducto().getNombre().toUpperCase() +
                "</b><br><span style='color:#229948;font-size:11px;'>$" + String.format("%.2f", subtotal) + "</span></div></html>");
            lblInfoProd.setFont(new Font("SansSerif", Font.PLAIN, 12));

            //Controles de cantidad (+, xN, -) y boton de quitar siempre visibles a la derecha
            JPanel panelControles = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 10));
            panelControles.setOpaque(false);

            JButton btnMas = crearBotonCircularCantidad("+");
            btnMas.addActionListener(e -> controlador.actualizarCantidadItem(indice, item.getCantidad() + 1));

            JLabel lblCant = new JLabel("x" + item.getCantidad(), SwingConstants.CENTER);
            lblCant.setFont(new Font("SansSerif", Font.BOLD, 13));
            lblCant.setPreferredSize(new Dimension(32, 24));

            JButton btnMenos = crearBotonCircularCantidad("-");
            btnMenos.addActionListener(e -> controlador.actualizarCantidadItem(indice, item.getCantidad() - 1));

            JButton btnBorrarFila = new JButton("✕");
            btnBorrarFila.setFont(new Font("SansSerif", Font.BOLD, 12));
            btnBorrarFila.setBackground(ROJO_ALERTA);
            btnBorrarFila.setForeground(Color.WHITE);
            btnBorrarFila.setPreferredSize(new Dimension(30, 30));
            btnBorrarFila.setMargin(new Insets(0, 0, 0, 0));
            btnBorrarFila.setFocusPainted(false);
            btnBorrarFila.setBorderPainted(false);
            btnBorrarFila.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnBorrarFila.putClientProperty("FlatLaf.style", "arc: 10");
            btnBorrarFila.addActionListener(e -> {
                if(indiceItemSeleccionado == indice) indiceItemSeleccionado = -1;
                controlador.eliminarItemSeleccionado(indice);
            });

            panelControles.add(btnMas);
            panelControles.add(lblCant);
            panelControles.add(btnMenos);
            panelControles.add(Box.createHorizontalStrut(4));
            panelControles.add(btnBorrarFila);

            fila.add(lblInfoProd, BorderLayout.CENTER);
            fila.add(panelControles, BorderLayout.EAST);
            panelListaCarrito.add(fila);
        }

        lblTotal.setText(String.format("TOTAL: $%.2f", total));
        panelListaCarrito.revalidate();
        panelListaCarrito.repaint();
    }

    //Crea los botones redondos pequenos de + y -
    private JButton crearBotonCircularCantidad(String simbolo){
        JButton btn = new JButton(simbolo);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setBackground(new Color(238, 240, 238));
        btn.setForeground(new Color(45, 50, 48));
        btn.setPreferredSize(new Dimension(28, 28));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.putClientProperty("FlatLaf.style", "arc: 999");
        return btn;
    }

    //Escala el logo manteniendo su proporcion original
    private void cargarImagenLogo(JLabel destino, int tamanoMax){
        try{
            BufferedImage imgOriginal = ImageIO.read(new File("img/logo.jpg"));
            double escala = Math.min((double) tamanoMax / imgOriginal.getWidth(), (double) tamanoMax / imgOriginal.getHeight());
            int nuevoW = (int) (imgOriginal.getWidth() * escala);
            int nuevoH = (int) (imgOriginal.getHeight() * escala);
            Image imgEscalada = imgOriginal.getScaledInstance(nuevoW, nuevoH, Image.SCALE_SMOOTH);
            destino.setIcon(new ImageIcon(imgEscalada));
        }catch(Exception e){
            destino.setText("POS");
        }
    }

    //Actualiza la fecha y hora de la esquina superior cada 15 segundos
    private void iniciarReloj(){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("MMM dd HH:mm");
        lblReloj.setText(LocalDateTime.now().format(formato));
        Timer timer = new Timer(15000, e -> lblReloj.setText(LocalDateTime.now().format(formato)));
        timer.start();
    }

    //Muestra mensajes enviados desde el controlador
    public void mostrarMensaje(String mensaje, String titulo, int tipoMensaje){
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipoMensaje);
    }

    //Punto de entrada por si se ejecuta directo esta ventana
    /*
    public static void main(String[] args){
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> new VentanaLogin().setVisible(true));
    }
    */
}