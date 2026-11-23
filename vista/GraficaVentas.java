package vista;

//Panel grafico que dibuja barras horizontales de ventas por producto

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.JPanel;
import modelo.MatrizVentas.ProductoFila;

final class GraficaVentas extends JPanel{
    //Colores y medidas de las barras
    private static final int ALTO_FILA = 34;
    private static final Color VERDE_BARRA = new Color(34, 153, 72);
    private static final Color ROJO_BARRA = new Color(220, 53, 69);
    private static final Color TEXTO_OSCURO = new Color(45, 50, 48);
    private static final Color LINEA_GUIA = new Color(225, 230, 226);

    private record Barra(String nombre, long unidades){}

    private final List<Barra> barras = new ArrayList<>();
    private double minimo;
    private double maximo;

    //Constructor principal
    GraficaVentas(){
        setBackground(Color.WHITE);
        setFont(new Font("SansSerif", Font.PLAIN, 13));
    }

    //Recibe los productos y totales para ordenarlos y dibujar las barras
    void mostrar(List<ProductoFila> productos, long[] totales){
        barras.clear();
        minimo = 0;
        maximo = 0;
        for(int fila = 0; fila < totales.length; fila++){
            barras.add(new Barra(productos.get(fila).nombre(), totales[fila]));
            minimo = Math.min(minimo, totales[fila]);
            maximo = Math.max(maximo, totales[fila]);
        }
        barras.sort(Comparator.comparingLong(Barra::unidades).reversed());
        setPreferredSize(new Dimension(420, Math.max(130, barras.size() * ALTO_FILA + 24)));
        revalidate();
        repaint();
    }

    //Dibuja las etiquetas, ejes y barras redondeadas en pantalla
    @Override
    protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        try{
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(TEXTO_OSCURO);

            if(barras.isEmpty()){
                g.setFont(new Font("SansSerif", Font.ITALIC, 13));
                g.setColor(new Color(130, 135, 132));
                g.drawString("Sin datos para graficar en este periodo.", 16, 30);
                return;
            }

            int inicio = Math.min(210, getWidth() / 3);
            FontMetrics tipografia = g.getFontMetrics();
            int margen = tipografia.stringWidth(Long.toString(Long.MIN_VALUE)) + 14;
            int ancho = Math.max(1, getWidth() - inicio - margen * 2);
            double rango = Math.max(1, maximo - minimo);
            int cero = inicio + margen + (int) Math.round(-minimo / rango * ancho);

            //Linea vertical de referencia en cero
            g.setColor(LINEA_GUIA);
            g.drawLine(cero, 8, cero, barras.size() * ALTO_FILA + 10);

            Rectangle zonaVisible = g.getClipBounds();
            int primera = Math.max(0, (zonaVisible.y - 12) / ALTO_FILA);
            int ultima = Math.min(barras.size(), (zonaVisible.y + zonaVisible.height) / ALTO_FILA + 1);

            for(int fila = primera; fila < ultima; fila++){
                Barra barra = barras.get(fila);
                int y = 12 + fila * ALTO_FILA;
                int extremo = inicio + margen + (int) Math.round((barra.unidades() - minimo) / rango * ancho);
                boolean negativa = barra.unidades() < 0;

                //Linea separadora sutil entre filas
                g.setColor(new Color(244, 246, 244));
                g.drawLine(10, y + ALTO_FILA - 2, getWidth() - 10, y + ALTO_FILA - 2);

                //Barra redondeada verde o roja
                g.setColor(negativa ? ROJO_BARRA : VERDE_BARRA);
                g.fillRoundRect(Math.min(cero, extremo), y + 5, Math.max(4, Math.abs(extremo - cero)), 20, 10, 10);

                //Nombre del producto a la izquierda
                g.setFont(new Font("SansSerif", Font.PLAIN, 13));
                g.setColor(TEXTO_OSCURO);
                Graphics etiqueta = g.create(12, y, Math.max(0, inicio - 18), ALTO_FILA);
                etiqueta.drawString(barra.nombre(), 0, 20);
                etiqueta.dispose();

                //Valor numerico en negrita junto a la barra
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                g.setColor(negativa ? ROJO_BARRA : new Color(26, 108, 52));
                String valor = Long.toString(barra.unidades());
                int x = negativa ? extremo - tipografia.stringWidth(valor) - 6 : extremo + 6;
                g.drawString(valor, x, y + 20);
            }
        }finally{
            g.dispose();
        }
    }
}