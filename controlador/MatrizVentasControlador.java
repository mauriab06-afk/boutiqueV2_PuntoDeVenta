package controlador;

//Controlador que procesa los calculos y datos de la matriz de ventas

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.stream.IntStream;
import modelo.MatrizVentas;
import modelo.MatrizVentas.Operacion;
import modelo.VentaDAO;
import vista.PanelMatrizVentas;

public class MatrizVentasControlador{
    //Etiquetas de los dias de la semana y formato corto de fecha
    private static final String[] DIAS = {"Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"};
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM");

    private final PanelMatrizVentas vista;
    private final VentaDAO ventaDAO;

    //Constructor que conecta el panel de la matriz con el acceso a ventas
    public MatrizVentasControlador(PanelMatrizVentas vista, VentaDAO ventaDAO){
        this.vista = vista;
        this.ventaDAO = ventaDAO;
        this.vista.agregarListenerActualizar(e -> actualizar());
    }

    //Lee la fecha y periodo elegidos para consultar y calcular la matriz
    public void actualizar(){
        try{
            //Convierte el texto de fecha y detecta el tipo de calculo seleccionado
            LocalDate referencia = LocalDate.parse(vista.getFechaTexto());
            int indicePeriodo = vista.getIndicePeriodo();
            boolean mensual = indicePeriodo == 1;

            Operacion operacion = switch(indicePeriodo){
                case 2 -> Operacion.RESTA;
                case 3 -> Operacion.SUMA;
                default -> Operacion.PERIODO;
            };

            //Calcula el dia inicial, cantidad de dias y fecha limite
            LocalDate inicio = mensual
                    ? referencia.withDayOfMonth(1)
                    : referencia.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            int dias = mensual ? referencia.lengthOfMonth() : 7;
            LocalDate fin = inicio.plusDays(dias - 1);
            LocalDate desde = (operacion == Operacion.PERIODO) ? inicio : inicio.minusWeeks(1);

            //Consulta las unidades en base de datos y construye la matriz
            MatrizVentas matriz = new MatrizVentas(
                    ventaDAO.obtenerUnidadesPorDia(desde, fin), inicio, dias, operacion
            );

            //Prepara el texto descriptivo del rango de fechas analizado
            String descripcion = switch(operacion){
                case RESTA -> " − " + desde + " al " + inicio.minusDays(1);
                case SUMA -> " + " + desde + " al " + inicio.minusDays(1);
                case PERIODO -> "";
            };
            String textoRango = "Unidades vendidas · " + inicio + " al " + fin + descripcion;

            construirYEnviarResultados(matriz, inicio, dias, mensual, operacion, textoRango);

        }catch(DateTimeParseException e){
            vista.mostrarError("Ingresa una fecha válida en formato AAAA-MM-DD.");
        }catch(SQLException e){
            vista.mostrarError("No se pudieron consultar las ventas.");
            System.err.println(e.getMessage());
        }
    }

    //Arma los encabezados, filas de la tabla y el resumen del top 3
    private void construirYEnviarResultados(MatrizVentas matriz, LocalDate inicio, int dias,
                                            boolean mensual, Operacion operacion, String textoRango){
        //Crea el arreglo de encabezados segun sea semana o mes
        String[] columnas = new String[dias + 2];
        columnas[0] = "Producto";
        for(int dia = 0; dia < dias; dia++){
            columnas[dia + 1] = mensual ? inicio.plusDays(dia).format(FECHA_CORTA) : DIAS[dia];
        }
        columnas[dias + 1] = "Total";

        //Calcula totales por producto y llena la matriz de celdas
        long[] totales = matriz.calcularTotales();
        int totalProductos = matriz.productos().size();
        Object[][] filas = new Object[totalProductos][columnas.length];

        for(int fila = 0; fila < totalProductos; fila++){
            filas[fila][0] = matriz.productos().get(fila).nombre();
            for(int dia = 0; dia < dias; dia++){
                filas[fila][dia + 1] = matriz.unidades(fila, dia);
            }
            filas[fila][dias + 1] = totales[fila];
        }

        //Genera el mensaje resumen inferior o los tres productos mas vendidos
        String textoResultado;
        if(totales.length == 0){
            textoResultado = "No hay ventas registradas en el periodo.";
        }else if(operacion == Operacion.RESTA){
            textoResultado = "Valores positivos = aumentos. Valores negativos = disminuciones.";
        }else{
            String top = IntStream.range(0, totales.length).boxed()
                    .filter(fila -> totales[fila] > 0)
                    .sorted(Comparator.comparingLong((Integer fila) -> totales[fila]).reversed())
                    .limit(3)
                    .map(fila -> matriz.productos().get(fila).nombre() + " (" + totales[fila] + ")")
                    .reduce((primero, siguiente) -> primero + " · " + siguiente)
                    .orElse("Sin unidades vendidas");
            textoResultado = "Top 3 por unidades · " + top;
        }

        //Envia toda la informacion procesada a la vista
        vista.mostrarDatos(textoRango, textoResultado, columnas, filas, matriz.productos(), totales);
    }
}