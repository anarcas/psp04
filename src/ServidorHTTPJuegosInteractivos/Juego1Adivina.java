/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import java.util.Random;

/**
 * Clase que maneja la lógica del juego "Adivina el Número".
 * El usuario debe adivinar un número aleatorio entre 1 y 100 en un máximo de 10 intentos.
 * El estado del juego (número a adivinar e intentos) se mantiene estáticamente entre solicitudes.
 *
 * @author Antonio Naranjo Castillo
 * 
 */
public class Juego1Adivina {

    /**
     * Define el número máximo de intentos permitidos para adivinar el número.
     * Es una constante final para indicar que su valor no cambiará.
     */
    public static final int NUM_MAX_INTENTOS = 10; 
    /**
     * Almacena el número aleatorio que el usuario debe adivinar.
     * Se genera una vez al cargar la clase (entre 1 y 100, ambos inclusive).
     * Su valor se mantiene a lo largo de las interacciones del juego hasta que se reinicia.
     */
    public static int numeroSolucion = new Random().nextInt(100) + 1; 
    /**
     * Lleva la cuenta de los intentos que ha realizado el usuario en la partida actual.
     * Se incrementa con cada intento y se reinicia al inicio de una nueva partida.
     */
    public static int intentos = 0; 

    /**
     * Maneja la lógica del juego cuando el usuario realiza una suposición.
     * Compara la suposición del usuario con el número generado y devuelve un
     * mensaje.
     *
     * @param numeroPropuestoStr El número que el usuario propone, como String.
     * @return El HTML generado con el mensaje de respuesta al usuario.
     */
    public static String adivinarNumero(String numeroPropuestoStr) {
        int numeroPropuesto;
        String mensaje;
        boolean desactivaBoton = false;

        // Bloque de validación de entrada del usuario
        try {
            // Validar que el número esté dentro del rango permitido [1-100].
            numeroPropuesto = Integer.parseInt(numeroPropuestoStr);
            if (numeroPropuesto < 1 || numeroPropuesto > 100) {
                ErrorLogger.logError("Adivina", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                        "El valor introducido no está dentro del rango [1-100].", numeroPropuestoStr);
                return Paginas.getAdivinarNumeroHtml("Por favor, introduce un número entre 1 y 100.", intentos, false);
            }
        } catch (NumberFormatException e) {
            ErrorLogger.logError("Adivina", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                    "El valor introducido no es un número válido.", numeroPropuestoStr);
            return Paginas.getAdivinarNumeroHtml("Por favor, introduce un número válido.", intentos, false);
        }
        
        // Incrementa el contador de intentos después de una suposición válida.
        intentos++;

        // Lógica principal del juego: comparación de la suposición con el número solución.
        if (numeroPropuesto == numeroSolucion) {
            mensaje = "CORRECTO. Has adivinado el número <b>" + numeroPropuesto + "</b> en " + intentos + " intentos. ";
            desactivaBoton = true;
        } else if (intentos >= NUM_MAX_INTENTOS) {
            // Lógica para cuando se agotan los intentos
            int numeroCorrecto = numeroSolucion;
            mensaje = "Has agotado todos los intentos. El número era <b>" + numeroCorrecto + "</b>. ";
            desactivaBoton = true;
        } else {
            // Pistas si el número no es el correcto y aún quedan intentos
            mensaje = numeroPropuesto < numeroSolucion ? "El número es mayor." : "El número es menor.";
        }

        // Primero, se genera el HTML con el estado ACTUAL de 'intentos' antes de resetearlo
        String htmlOutput = Paginas.getAdivinarNumeroHtml(mensaje, intentos, desactivaBoton);

        // Genera el HTML de la página.
        // Si el juego ha terminado (ganado o perdido), se añade el enlace "Jugar otra vez".
        if (desactivaBoton) {
            mensaje += "<a href='/adivina'>Jugar otra vez.</a>";
            htmlOutput = Paginas.getAdivinarNumeroHtml(mensaje, intentos, desactivaBoton);

            resetJuego();
        }

        return htmlOutput; // Retorna el HTML con el mensaje y el estado actual del juego.
    }

    /**
     * Resetea el estado del juego "Adivina el Número" para iniciar una nueva partida.
     * Genera un nuevo número a adivinar y reinicia el contador de intentos.
     */
    public static void resetJuego() {
        numeroSolucion = new Random().nextInt(100) + 1; // Genera un nuevo número aleatorio.
        intentos = 0; // Reinicia el contador de intentos.
    }
}
