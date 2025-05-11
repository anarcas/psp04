/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import java.util.Random;

/**
 * Clase que maneja la lógica del juego 2 "Lanza Dados".
 * El usuario y el servidor lanzan un dado en cada ronda y se lleva la puntuación el que saque el número más alto.
 * El juego consta de 5 rondas, y al final se determina el ganador.
 *
 * @author Antonio Naranjo Castillo
 */
public class Juego2Dados {

    private static final Random random = new Random();
    private static int puntuacionUsuario = 0;
    private static int puntuacionServidor = 0;
    private static int rondasJugadas = 0;
    private static final int TOTAL_RONDAS = 5;

    /**
     * Devuelve el HTML de la página de "Lanza Dados" sin lanzar los dados.
     *
     * @return El HTML de la página con el estado actual del juego.
     */
    public static String estadoLanzarDados() {
        // Devuelve la página con el mensaje inicial y el estado actual del juego.
        return Paginas.getLanzarDadosHtml("Pulsa el botón para lanzar los dados.", puntuacionUsuario, puntuacionServidor, rondasJugadas, 0, 0, rondasJugadas >= TOTAL_RONDAS);
    }

    /**
     * Maneja la lógica cuando el usuario decide lanzar los dados.
     * Se actualiza la puntuación del usuario y del servidor dependiendo de los resultados de los dados.
     *
     * @return El HTML de la página con el resultado de la ronda y el estado actualizado.
     */
    public static String lanzarDados() {
        if (rondasJugadas >= TOTAL_RONDAS) {
            // Determina el mensaje final del juego según el resultado de las puntuaciones.
            String resultadoFinal = puntuacionUsuario > puntuacionServidor ? "Ganaste el juego." : puntuacionUsuario < puntuacionServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            String mensaje = resultadoFinal + " <a href='/dados?reset=true'>Jugar otra vez.</a>";
            // Devuelve la página con el resultado final del juego.
            return Paginas.getLanzarDadosHtml(mensaje, puntuacionUsuario, puntuacionServidor, rondasJugadas, 0, 0, true);
        }

        // Genera un número aleatorio entre 1 y 6 para el usuario y el servidor.
        int tiradaUsuario = random.nextInt(6) + 1;
        int tiradaServidor = random.nextInt(6) + 1;

        String mensajeRonda;

        // Actualiza la puntuación según el resultado de los dados.
        if (tiradaUsuario > tiradaServidor) {
            puntuacionUsuario++;
            rondasJugadas++; // Incrementa solo si hay ganador
            mensajeRonda = "Ganaste esta ronda.";
        } else if (tiradaUsuario < tiradaServidor) {
            puntuacionServidor++;
            rondasJugadas++; // Incrementa solo si hay ganador
            mensajeRonda = "Perdiste esta ronda.";
        } else {
            mensajeRonda = "Empate en esta ronda. Vuelve a lanzar los dados.";
            // No se incrementa rondasJugadas en caso de empate, la ronda se repite.
        }

        boolean desactivaBoton = false;
        String mensajeFinal = mensajeRonda;
        if (rondasJugadas >= TOTAL_RONDAS) {
            desactivaBoton = true;
            String resultadoFinal = puntuacionUsuario > puntuacionServidor ? "Ganaste el juego." : puntuacionUsuario < puntuacionServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            mensajeFinal = mensajeRonda + " " + resultadoFinal + " <a href='/dados?reset=true'>Jugar otra vez.</a>";
        }

        return Paginas.getLanzarDadosHtml(mensajeFinal, puntuacionUsuario, puntuacionServidor, rondasJugadas, tiradaUsuario, tiradaServidor, desactivaBoton);
    }

    /**
     * Resets the game state for "Lanza Dados".
     */
    public static void resetJuego() {
        puntuacionUsuario = 0;
        puntuacionServidor = 0;
        rondasJugadas = 0;
    }
}