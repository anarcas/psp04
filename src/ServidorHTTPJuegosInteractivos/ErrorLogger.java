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

/**
 * Utility class for logging errors to a file.
 */
public class ErrorLogger {
    // TODO code application logic here
    
    private static final String LOG_FILE = "logErrores.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Logs an error message to the logErrores.txt file.
     * @param gameName The name of the game where the error occurred (e.g., "Adivina", "PPT").
     * @param lineNumber The line number in the code where the error was caught (for debugging).
     * @param errorMessage The specific error message.
     * @param receivedValue The value that caused the error.
     */
    public static void logError(String gameName, int lineNumber, String errorMessage, String receivedValue) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) { // Append to file
            String timestamp = LocalDateTime.now().format(formatter);
            writer.println(timestamp + " - Error juego " + gameName + " en la linea " + lineNumber + ": " + errorMessage + " Valor recibido: " + receivedValue + ".");
        } catch (IOException e) {
            System.err.println("Error writing to log file: " + e.getMessage());
        }
    }
}