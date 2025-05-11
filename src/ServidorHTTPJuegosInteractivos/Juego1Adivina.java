/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import java.util.Random;

/**
 * Clase que maneja la lógica del juego 1 "Adivina el Número".
 * El usuario debe adivinar un número aleatorio entre 1 y 100 en un máximo de 10 intentos.
 *
 * @author Antonio Naranjo Castillo
 */
public class Juego1Adivina {

    public static final int NUM_MAX_INTENTOS = 10; // Made public for ServidorHTTP access
    public static int numeroSolucion = new Random().nextInt(100) + 1; // public static for access
    public static int intentos = 0; // public static for access

    /**
     * Maneja la lógica del juego cuando el usuario realiza una suposición.
     * Compara la suposición del usuario con el número generado y devuelve un mensaje.
     *
     * @param numeroPropuestoStr El número que el usuario propone, como String.
     * @return El HTML generado con el mensaje de respuesta al usuario.
     */
    public static String adivinarNumero(String numeroPropuestoStr) {
        int numeroPropuesto;
        try {
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


        intentos++;
        String mensaje;
        boolean desactivaBoton = false;

        if (numeroPropuesto == numeroSolucion) {
            mensaje = "CORRECTO. Has adivinado el número <b>" + numeroPropuesto + "</b> en " + intentos + " intentos. ";
            desactivaBoton = true;
        } else if (intentos >= NUM_MAX_INTENTOS) {
            int numeroCorrecto = numeroSolucion;
            mensaje = "Has agotado todos los intentos. El número era <b>" + numeroCorrecto + "</b>. ";
            desactivaBoton = true;
        } else {
            mensaje = numeroPropuesto < numeroSolucion ? "El número es mayor." : "El número es menor.";
        }

        if (desactivaBoton) {
            // Reset for next game if user plays again
            resetJuego();
            mensaje += "<a href='/adivina'>Jugar otra vez.</a>";
        }

        return Paginas.getAdivinarNumeroHtml(mensaje, intentos, desactivaBoton);
    }

    /**
     * Resets the game state for "Adivina el Número".
     */
    public static void resetJuego() {
        numeroSolucion = new Random().nextInt(100) + 1;
        intentos = 0;
        // Any other state for "desactivaBoton" if it's tracked outside a single call.
        // In the current Paginas.getAdivinarNumeroHtml, desactivaBoton is determined by conditions.
    
    }
}