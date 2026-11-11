package modelo;

//Clase para gestionar los elementos de una lista de ItemCarrito

import java.util.ArrayList;
import java.util.List;

public class Carrito {
    private final List<ItemCarrito> items; //Lista de productos del carrito

    //Constructor
    public Carrito(){this.items = new ArrayList<>();}

    //Agregar un producto
    public void agregarProducto(Producto producto, int cantidad){
        for(ItemCarrito item : items){ //Verifica si esta en la lista
            if(item.getProducto().getId() == producto.getId()){ //AGrega +1 si lo esta
                item.setCantidad(item.getCantidad() + cantidad);
                return;
            }
        }
        items.add(new ItemCarrito(producto, cantidad));
    }

    //Remover el item por su posicion
    public void removerItem(int index){
        if(index >= 0 && index < items.size())items.remove(index);
    }

    //Calcula el precio total del carrito
    public double calcularTotal(){
        double total = 0;
        for(ItemCarrito item : items)total += item.calcularSubtotal();
        return total;
    }

    //Limpiar la lista
    public void limpiar(){items.clear();}

    public List<ItemCarrito> getItems(){return items;}
}