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
 * 
 */
public class Juego2Dados {

    /**
     * Objeto de la clase Random utilizado para generar números aleatorios (tiradas de dado).
     * Es estático para asegurar que se use la misma instancia en todo el juego.
     */
    private static final Random random = new Random();
    /**
     * Almacena la puntuación total acumulada por el usuario a lo largo de las rondas.
     * Se reinicia al inicio de cada nueva partida.
     */
    private static int puntuacionUsuario = 0;
    /**
     * Almacena la puntuación total acumulada por el servidor a lo largo de las rondas.
     * Se reinicia al inicio de cada nueva partida.
     */
    private static int puntuacionServidor = 0;
    /**
     * Lleva la cuenta de cuántas rondas se han jugado en la partida actual.
     * Se incrementa después de cada ronda completada.
     */
    public static int rondasJugadas = 0;
    /**
     * Define el número total de rondas que componen una partida completa del juego.
     * Es una constante final para indicar que su valor no cambiará durante la ejecución.
     */
    public static final int TOTAL_RONDAS = 5;
    /**
     * Almacena el resultado de la última tirada de dado realizada por el usuario.
     * Se utiliza para mostrar la última jugada en la interfaz.
     */
    private static int ultimaTiradaUsuario = 0;
    /**
     * Almacena el resultado de la última tirada de dado realizada por el servidor.
     * Se utiliza para mostrar la última jugada en la interfaz.
     */
    private static int ultimaTiradaServidor = 0;

    /**
     * Devuelve el HTML de la página de "Lanza Dados" sin lanzar los dados.
     *
     * @return El HTML de la página con el estado actual del juego.
     */
    public static String estadoLanzarDados() {
          // Si el juego ha terminado, se prepara el mensaje final
        if (rondasJugadas >= TOTAL_RONDAS) {
            String resultadoFinal = puntuacionUsuario > puntuacionServidor ? "Ganaste el juego." : puntuacionUsuario < puntuacionServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            // Añade el enlace para jugar otra vez al mensaje final
            String mensajeFinal = resultadoFinal + " <a href='/dados?reset=true'>Jugar otra vez.</a>";
            // El botón debe estar desactivado porque el juego ha terminado
            return Paginas.getLanzarDadosHtml(mensajeFinal, puntuacionUsuario, puntuacionServidor, rondasJugadas, ultimaTiradaUsuario, ultimaTiradaServidor, true);
        } else {
            // Si el juego no ha terminado (o se acaba de resetear), el botón debe estar activo
            return Paginas.getLanzarDadosHtml("Pulsa el botón para lanzar los dados.", puntuacionUsuario, puntuacionServidor, rondasJugadas, ultimaTiradaUsuario, ultimaTiradaServidor, false);
        }
    }

    /**
     * Maneja la lógica cuando el usuario decide lanzar los dados.
     * Se actualiza la puntuación del usuario y del servidor dependiendo de los resultados de los dados.
     *
     * @return El HTML de la página con el resultado de la ronda y el estado actualizado.
     */
    public static String lanzarDados() {
        
        // Genera un número aleatorio entre 1 y 6 para el usuario y el servidor.
        int tiradaActualUsuario = random.nextInt(6) + 1;
        int tiradaActualServidor = random.nextInt(6) + 1;

        // Actualiza las últimas tiradas para poder mostrarlas en la página.
        ultimaTiradaUsuario = tiradaActualUsuario;
        ultimaTiradaServidor = tiradaActualServidor;

        // Variable auxiliar para almacenar el mensaje de la ronda.
        String mensajeRonda;
        
        // Actualiza la puntuación según el resultado de los dados.
        if (tiradaActualUsuario > tiradaActualServidor) {
            puntuacionUsuario++;
            rondasJugadas++; // Incrementa rondas si alguien gana la ronda
            mensajeRonda = "Ganaste esta ronda.";
        } else if (tiradaActualUsuario < tiradaActualServidor) {
            puntuacionServidor++;
            rondasJugadas++; // Incrementa rondas si alguien gana la ronda
            mensajeRonda = "Perdiste esta ronda.";
        } else {
            mensajeRonda = "Empate en esta ronda. Vuelve a lanzar los dados.";
            // No se incrementa rondasJugadas en caso de empate, la ronda se repite.
        }
        
        // Se determina si el botón debe desactivarse.
        boolean desactivaBoton = false;
        String mensajeFinal = mensajeRonda;
       
        if (rondasJugadas >= TOTAL_RONDAS) {
            desactivaBoton = true; // Desactivar el botón porque el juego ha terminado
            String resultadoFinal = puntuacionUsuario > puntuacionServidor ? "Ganaste el juego." : puntuacionUsuario < puntuacionServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            
            mensajeFinal = mensajeRonda + " " + resultadoFinal + " <a href='/dados?reset=true'>Jugar otra vez.</a>";
      
        }

        // Devuelve la página HTML con el estado actual del juego
        return Paginas.getLanzarDadosHtml(mensajeFinal, puntuacionUsuario, puntuacionServidor, rondasJugadas, ultimaTiradaUsuario, ultimaTiradaServidor, desactivaBoton);
 
    }

    /**
     * Restablece el estado del juego "Piedra, papel, tijeras" a sus valores iniciales.
     * Reiniciar todas las variables estáticas de la clase Juego2Dados.
     */
    public static void resetJuego() {
        puntuacionUsuario = 0;
        puntuacionServidor = 0;
        rondasJugadas = 0;
        ultimaTiradaUsuario = 0;
        ultimaTiradaServidor = 0;
    }
}