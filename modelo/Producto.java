package modelo;

//Clase encargada de representar un producto dentro del catalogo

public class Producto{
    private int id;
    private String nombre;
    private double precio;
    private String categoria;
    private boolean activo = true; //Disponible (true) / No disponible (false)
    private int stock = 0;

    public Producto(){}

    //Constructor para crear un nuevo producto en el catalogo
    public Producto(String nombre, double precio, String categoria){
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.activo = true;
        this.stock = 0;
    }

    //Constructor para modificar los productos desde catalogo
    public Producto(int id, String nombre, double precio, String categoria, boolean activo){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.activo = activo;
        this.stock = 0;
    }

    //COnstructor usado al leer desde la base de datos
    public Producto(int id, String nombre, double precio, String categoria, boolean activo, int stock){
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
        this.activo = activo;
        this.stock = stock;
    }


    //Getters y setters
    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}

    public double getPrecio(){return precio;}
    public void setPrecio(double precio){this.precio = precio;}

    public String getCategoria(){return categoria;}
    public void setCategoria(String categoria){this.categoria = categoria;}

    public boolean isActivo(){return activo;}
    public void setActivo(boolean activo){this.activo = activo;}

    public int getStock(){return stock;}
    public void setStock(int stock){this.stock = stock;}
}