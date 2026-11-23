package controlador;

//Clase encargada de gestionar las copias de seguridad de la base de datos

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GestionBackup{
    //Nombre del archivo principal de la base de datos
    private static final String ARCHIVO_BD = "boutique.db";

    //Verifica si falta la base de datos principal y ofrece restaurar el respaldo mas reciente
    public static void verificarYRestaurarSiFalta(){
        File origen = new File(ARCHIVO_BD);
        if(origen.exists()){
            return;
        }

        File carpetaBackups = new File("backups");
        if(!carpetaBackups.exists() || !carpetaBackups.isDirectory()){
            return;
        }

        //Busca todos los archivos de respaldo disponibles
        File[] respaldos = carpetaBackups.listFiles((dir, name) -> name.endsWith(".db"));
        if(respaldos == null || respaldos.length == 0){
            return;
        }

        //Ordena los respaldos del mas reciente al mas antiguo
        Arrays.sort(respaldos, Comparator.comparingLong(File::lastModified).reversed());
        File ultimoRespaldo = respaldos[0];

        String fechaFormateada = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(ultimoRespaldo.lastModified()));
        Object[] opciones = {"Sí", "No"};

        //Muestra el cuadro de confirmacion para restaurar
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

        if(opcion == 0){
            try{
                Files.copy(ultimoRespaldo.toPath(), origen.toPath(), StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(null, "¡Base de datos restaurada con éxito desde:\n" + ultimoRespaldo.getName(), "Restauración Completada", JOptionPane.INFORMATION_MESSAGE);
            }catch(IOException e){
                JOptionPane.showMessageDialog(null, "Ocurrió un error al intentar restaurar el respaldo: " + e.getMessage(), "Error de Restauración", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    //Genera una copia de seguridad automatica diaria
    public static void crearCopiaAutomatica(){
        File origen = new File(ARCHIVO_BD);
        if(!origen.exists()){
            return;
        }

        File carpetaBackups = new File("backups");
        if(!carpetaBackups.exists()){
            carpetaBackups.mkdir();
        }

        String fechaActual = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        File destino = new File(carpetaBackups, "boutique_auto_" + fechaActual + ".db");

        try{
            Files.copy(origen.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }catch(IOException e){
            System.err.println("Error en el backup automático: " + e.getMessage());
        }
    }

    //Genera una copia de seguridad manual solicitada por el usuario
    public static void crearCopiaManual(JFrame parent){
        File origen = new File(ARCHIVO_BD);
        if(!origen.exists()){
            JOptionPane.showMessageDialog(parent, "No se encontró la base de datos principal.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File carpetaBackups = new File("backups");
        if(!carpetaBackups.exists()){
            carpetaBackups.mkdir();
        }

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