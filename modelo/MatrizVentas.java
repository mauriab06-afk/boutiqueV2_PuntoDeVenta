package modelo;

//Modelo que organiza las unidades vendidas por producto y dia en una matriz

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatrizVentas{
    //Tipos de operacion permitidos sobre las semanas
    public enum Operacion{ PERIODO, SUMA, RESTA }

    //Representa cada producto con su identificador y nombre para las filas
    public record ProductoFila(int id, String nombre){}

    private final List<ProductoFila> productos;
    private final long[][] unidades;
    private final int dias;

    //Constructor que acomoda las ventas diarias en filas por producto y columnas por dia
    public MatrizVentas(List<VentaDAO.VentaDiaria> ventas, LocalDate inicio,
                        int dias, Operacion operacion){
        this.dias = dias;

        //Asigna un indice de fila unico a cada producto encontrado
        Map<ProductoFila, Integer> filas = new LinkedHashMap<>();
        for(VentaDAO.VentaDiaria venta : ventas){
            ProductoFila producto = new ProductoFila(venta.idProducto(), venta.producto());
            filas.putIfAbsent(producto, filas.size());
        }
        productos = List.copyOf(filas.keySet());

        //Separa las unidades del periodo actual y de la semana anterior
        long[][] actual = new long[productos.size()][dias];
        long[][] anterior = new long[productos.size()][dias];
        for(VentaDAO.VentaDiaria venta : ventas){
            int fila = filas.get(new ProductoFila(venta.idProducto(), venta.producto()));
            int columna = (int) ChronoUnit.DAYS.between(inicio, venta.fecha());
            if(columna < 0){
                anterior[fila][columna + dias] += venta.unidades();
            }else{
                actual[fila][columna] += venta.unidades();
            }
        }

        //Aplica suma o resta entre semanas si se eligio comparativa
        unidades = actual;
        if(operacion != Operacion.PERIODO){
            for(int fila = 0; fila < productos.size(); fila++){
                for(int dia = 0; dia < dias; dia++){
                    unidades[fila][dia] = switch(operacion){
                        case SUMA -> actual[fila][dia] + anterior[fila][dia];
                        case RESTA -> actual[fila][dia] - anterior[fila][dia];
                        case PERIODO -> actual[fila][dia];
                    };
                }
            }
        }
    }

    //Devuelve la lista ordenada de productos en las filas
    public List<ProductoFila> productos(){
        return productos;
    }

    //Devuelve las unidades de un producto en un dia especifico
    public long unidades(int fila, int dia){
        return unidades[fila][dia];
    }

    //Suma las unidades de todos los dias para obtener el total por cada producto
    public long[] calcularTotales(){
        long[] unos = new long[dias];
        Arrays.fill(unos, 1);
        long[] totales = new long[productos.size()];
        for(int fila = 0; fila < productos.size(); fila++){
            for(int dia = 0; dia < dias; dia++){
                totales[fila] += unidades[fila][dia] * unos[dia];
            }
        }
        return totales;
    }
}