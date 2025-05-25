/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package ServidorHTTPJuegosInteractivos;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase de utilidad para registrar errores de la aplicación en un archivo de log.
 * Facilita la depuración y el seguimiento de problemas que ocurren durante la ejecución de los juegos.
 * 
 * @author Antonio Naranjo Castillo
 * 
 */
public class ErrorLogger {
    // TODO code application logic here
    
    private static final String LOG_FILE = "logErrores.txt"; // Nombre del archivo donde se registrarán todos los errores.
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Formateador para la fecha y hora.
    
    private static final Logger logger = Logger.getLogger(ErrorLogger.class.getName()); // Obtener una instancia del Logger para esta clase.

    /**
     * Registra un mensaje de error en el archivo `logErrores.txt`.
     * Cada entrada de log incluye la fecha y hora, el nombre del juego, el número de línea
     * donde se produjo el error, un mensaje descriptivo y el valor que lo causó.
     *
     * @param gameName El nombre del juego donde ocurrió el error (ej., "Adivina", "PPT", "Dados").
     * @param lineNumber El número de línea en el código donde se detectó el error (útil para depuración).
     * Para obtenerlo, se usa `Thread.currentThread().getStackTrace()[1].getLineNumber()`.
     * @param errorMessage El mensaje específico que describe la naturaleza del error.
     * @param receivedValue El valor de entrada o dato que provocó el error (puede ser nulo si no aplica).
     */
    public static void logError(String gameName, int lineNumber, String errorMessage, String receivedValue) {
        // Se intenta obtener la fecha y hora siempre que exista una escritura exitosa.
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) { // Append to file
            String timestamp = LocalDateTime.now().format(formatter);
            writer.println(timestamp + " - Error juego " + gameName + " en la linea " + lineNumber + ": " + errorMessage + " Valor recibido: " + receivedValue + ".");
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Error escribiendo log en el archivo: ", e.getMessage()));
        }
    }
}