/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import java.util.Arrays;
import java.util.Random;

/**
 * Clase que maneja la lógica del juego 3 "Piedra, Papel o Tijeras".
 * El usuario y el servidor eligen una opción en cada ronda, y el que gane más rondas al final será el ganador.
 * El juego consta de 5 rondas.
 *
 * @author Antonio Naranjo Castillo
 */
public class Juego3PPT {

    private static final String[] eleccion = {"Piedra", "Papel", "Tijeras"};
    private static final Random random = new Random();
    private static int victoriasUsuario = 0;
    private static int victoriasServidor = 0;
    private static int rondasJugadas = 0;
    private static final int TOTAL_RONDAS = 5;

    /**
     * Maneja la lógica del juego "Piedra, Papel o Tijeras" cuando el usuario hace una elección.
     *
     * @param eleccionUsuario La opción elegida por el usuario (Piedra, Papel o Tijeras).
     * @return El HTML con el resultado de la ronda y el estado actualizado del juego.
     */
    public static String piedraPapelTijeras(String eleccionUsuario) {
        // Server-side input validation for eleccionUsuario
        if (!Arrays.asList(eleccion).contains(eleccionUsuario)) {
            ErrorLogger.logError("PPT", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                 "El valor introducido no es una opción válida (Piedra, Papel o Tijeras).", eleccionUsuario);
            return Paginas.getPiedraPapelTijerasHtml("Opción no válida. Por favor, elige Piedra, Papel o Tijeras.", null, null, victoriasUsuario, victoriasServidor, rondasJugadas, false);
        }

        String eleccionServidor = eleccion[random.nextInt(eleccion.length)];
        String resultado;

        if (eleccionUsuario.equals(eleccionServidor)) {
            resultado = "Empate en esta ronda. Vuelve a elegir.";
            // Do not increment rondasJugadas on a tie
        } else if ((eleccionUsuario.equals("Piedra") && eleccionServidor.equals("Tijeras")) ||
                   (eleccionUsuario.equals("Papel") && eleccionServidor.equals("Piedra")) ||
                   (eleccionUsuario.equals("Tijeras") && eleccionServidor.equals("Papel"))) {
            victoriasUsuario++;
            rondasJugadas++; // Increment only if there's a winner
            resultado = "Ganaste esta ronda.";
        } else {
            victoriasServidor++;
            rondasJugadas++; // Increment only if there's a winner
            resultado = "Perdiste esta ronda.";
        }

        boolean desactivarBoton = rondasJugadas >= TOTAL_RONDAS;
        if (desactivarBoton) {
            String resultadoFinal = victoriasUsuario > victoriasServidor ? "Ganaste el juego." : victoriasUsuario < victoriasServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            resultado += " " + resultadoFinal + " <a href='/ppt?reset=true'>Jugar otra vez.</a>";
            resetJuego(); // Reset for next game if user plays again
        }

        return Paginas.getPiedraPapelTijerasHtml(resultado, eleccionUsuario, eleccionServidor, victoriasUsuario, victoriasServidor, rondasJugadas, desactivarBoton);
    }

    /**
     * Resets the game state for "Piedra, Papel o Tijeras".
     */
    public static void resetJuego() {
        victoriasUsuario = 0;
        victoriasServidor = 0;
        rondasJugadas = 0;
    }
}