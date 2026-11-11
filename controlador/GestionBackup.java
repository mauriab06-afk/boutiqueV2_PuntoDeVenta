package controlador;

//Clase encargada de la gestion de las las backups para evitar perdidas de datos

import modelo.*;
import vista.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class GestionBackup{
    //NOmbre del archivo por defecto
    private static final String ARCHIVO_BD = "boutique.db";

    //Verifica si la base de datos no existe, busca en la carpeta de backups y la restaura
    public static void verificarYRestaurarSiFalta(){
        File origen = new File(ARCHIVO_BD);
        if(origen.exists()){return;} //Si existe la base principal

        File carpetaBackups = new File("backups");
        if(!carpetaBackups.exists() || !carpetaBackups.isDirectory()){return;} //Si no existe la carpeta

        //Listamos los archivos de las backups
        File[] respaldos = carpetaBackups.listFiles((dir, name) -> name.endsWith(".db"));

        if(respaldos == null || respaldos.length == 0){return;} //Si tiene 0 archivos la carpeta

        Arrays.sort(respaldos, Comparator.comparingLong(File::lastModified).reversed()); //Ordena la lista (Ultimo modificado)
        File ultimoRespaldo = respaldos[0]; //Tomamos el primero

        //Se formatea la fecha a un string
        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(ultimoRespaldo.lastModified()));
        Object[] opciones = {"Sí", "No"};

        //Ventana emergente de recuperacion
        int opcion = JOptionPane.showOptionDialog(
                null,
                "No se encontró la base de datos principal ('boutique.db').\n\n" +
                "Se detectó un respaldo guardado el " + fechaFormateada + ".\n" +
                "¿Desea restaurar la información desde este respaldo?",
                "Restauración Automática de Base de Datos",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if(opcion == 0){ //Presiono "si"
            try{
                Files.copy(ultimoRespaldo.toPath(), origen.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(null, "¡Base de datos restaurada con éxito desde:\n" + ultimoRespaldo.getName(), "Restauración Completada", JOptionPane.INFORMATION_MESSAGE);
            }catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar restaurar el respaldo: " + e.getMessage(), "Error de Restauración", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Generea automaticamente una backup
    public static void crearCopiaAutomatica(){
        File origen = new File(ARCHIVO_BD);
        if(!origen.exists())return;

        //Si no existe la carpeta la crea
        File carpetaBackups = new File("backups");
        if(!carpetaBackups.exists()){carpetaBackups.mkdir();}

        //Formato de fecha actual y de nuevo archivo
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File destino = new File(carpetaBackups, "boutique_auto_" + fechaActual + ".db");

        try{
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            System.err.println("Error en el backup automático: " + e.getMessage());
        }
    }

    //Generar backup manual
    public static void crearCopiaManual(JFrame parent){
        File origen = new File(ARCHIVO_BD);

        if(!origen.exists()){ //Revisar si existe la base de datos principal
            JOptionPane.showMessageDialog(parent, "No se encontró la base de datos principal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Si no existe la carpeta la crea
        File carpetaBackups = new File("backups");
        if(!carpetaBackups.exists()){carpetaBackups.mkdir();}

        //Formato de fecha actual y de nuevo archivo
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File destino = new File(carpetaBackups, "boutique_manual_" + timeStamp + ".db");

        try{
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            JOptionPane.showMessageDialog(
                    parent,
                    "¡Copia de seguridad guardada con éxito en la carpeta 'backups'!\n\nArchivo: " + destino.getName(),
                    "Backup Exitoso",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }catch(IOException e){
            JOptionPane.showMessageDialog(parent, "Error al crear la copia de seguridad: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}