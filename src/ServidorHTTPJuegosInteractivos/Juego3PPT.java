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
 * 
 */
public class Juego3PPT {

    /**
     * Array constante que contiene las opciones válidas del juego: "Piedra", "Papel", "Tijeras".
     * Es inmutable y se utiliza para validar la entrada del usuario y las elecciones del servidor.
     */
    private static final String[] eleccion = {"Piedra", "Papel", "Tijeras"};
    /**
     * Objeto de la clase Random utilizado para generar aleatoriamente la jugada del servidor.
     * Es estático para garantizar una única instancia y un comportamiento predecible.
     */
    private static final Random random = new Random();
    /**
     * Almacena el número de victorias acumuladas por el usuario en la partida actual.
     * Se reinicia al inicio de cada nueva partida.
     */
    public static int victoriasUsuario = 0; 
    /**
     * Almacena el número de victorias acumuladas por el servidor en la partida actual.
     * Se reinicia al inicio de cada nueva partida.
     */
    public static int victoriasServidor = 0; 
    /**
     * Lleva la cuenta de cuántas rondas con un ganador (no empates) se han jugado en la partida actual.
     * Se incrementa solo cuando hay una victoria para el usuario o el servidor.
     */
    public static int rondasJugadas = 0; 
    /**
     * Define el número total de rondas con ganador que deben jugarse para finalizar una partida.
     * Es una constante final para indicar que su valor no cambiará.
     */
    public static final int TOTAL_RONDAS = 5; 
    /**
     * Almacena la última jugada realizada por el usuario ("Piedra", "Papel" o "Tijeras").
     * Se utiliza para mostrar la jugada previa en la interfaz.
     */
    private static String ultimaJugadaUsuario = "";
    /**
     * Almacena la última jugada generada por el servidor ("Piedra", "Papel" o "Tijeras").
     * Se utiliza para mostrar la jugada previa en la interfaz.
     */
    private static String ultimaJugadaServidor = "";

    
    /**
     * Devuelve el HTML de la página de "Piedra, Papel o Tijeras" sin jugar una ronda.
     * Este método se usa cuando se carga la página por primera vez (GET request)
     * o se vuelve a ella después de un reset.
     *
     * @return El HTML de la página con el estado actual o inicial del juego.
     */
    public static String estadoPPT() {
        String mensaje;
        boolean desactivarBotones;

        // Si el juego ha terminado, mostrar el mensaje final y el enlace "Jugar otra vez".
        if (rondasJugadas >= TOTAL_RONDAS) {
            String resultadoFinal = victoriasUsuario > victoriasServidor ? "Ganaste el juego." : victoriasUsuario < victoriasServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            // Añadir el mensaje de la última ronda para darle contexto al mensaje final y el enlace "Jugar otra vez" con la clase para que se vea como botón.
            mensaje = "Tú elegiste: " + ultimaJugadaUsuario + "<br>El servidor eligió: " + ultimaJugadaServidor + "<br><br>" +
                      "Rondas jugadas: " + rondasJugadas + "/" + TOTAL_RONDAS + "<br>" +
                      resultadoFinal + " <a href='/ppt?reset=true'>Jugar otra vez.</a>";
            desactivarBotones = true; // Desactivar los botones de jugada al finalizar el juego
        } else {
            // Si el juego no ha terminado (es el inicio o una ronda intermedia), mostrar un mensaje inicial o el resultado de la última ronda (si se jugó una).
            if (rondasJugadas == 0 && ultimaJugadaUsuario.isEmpty()) { // Inicio del juego
                mensaje = "Elige tu jugada:";
            } else { // Después de una ronda, pero el juego no ha terminado
                mensaje = "Tú elegiste: " + ultimaJugadaUsuario + "<br>El servidor eligió: " + ultimaJugadaServidor + "<br><br>" +
                          "Siguiente ronda. Elige tu jugada:";
            }
            desactivarBotones = false; // Los botones deben estar activos
        }
        // Llamada a getPiedraPapelTijerasHtml con los parámetros correctos.
        return Paginas.getPiedraPapelTijerasHtml(mensaje, ultimaJugadaUsuario, ultimaJugadaServidor, victoriasUsuario, victoriasServidor, rondasJugadas, desactivarBotones);
    }
    
    /**
     * Maneja la lógica de una ronda del juego "Piedra, Papel o Tijeras" cuando el usuario realiza una elección.
     *      *
     * @param eleccionUsuario La opción elegida por el usuario ("Piedra", "Papel" o "Tijeras").
     * @return El HTML generado con el mensaje de resultado de la ronda, el estado actualizado de las puntuaciones
     * y las rondas jugadas, y un enlace para jugar de nuevo si la partida ha finalizado.
     */
    public static String piedraPapelTijeras(String eleccionUsuario) {
        // Validación en el servidor de la entrada del usuario.
        if (!Arrays.asList(eleccion).contains(eleccionUsuario)) {
            ErrorLogger.logError("PPT", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                 "El valor introducido no es una opción válida (Piedra, Papel o Tijeras).", eleccionUsuario);
            return Paginas.getPiedraPapelTijerasHtml("Opción no válida. Por favor, elige Piedra, Papel o Tijeras.", null, null, victoriasUsuario, victoriasServidor, rondasJugadas, false);
        }

        // Almacena la última jugada del usuario en una variable estática para mantener el estado.
        ultimaJugadaUsuario = eleccionUsuario;
        // Almacena la elección aleatoria del servidor (Piedra, Papel o Tijeras).
        String eleccionServidor = eleccion[random.nextInt(eleccion.length)];
        // Almacena la última jugada del servidor en una variable estática para mantener el estado.
        ultimaJugadaServidor = eleccionServidor; // Guarda la jugada del servidor.
        // Variable para almacenar el mensaje del resultado de la ronda actual.
        String resultadoRondaTexto;
       
        // Lógica para determinar el resultado de la ronda
        if (eleccionUsuario.equals(eleccionServidor)) {
            resultadoRondaTexto = "Empate en esta ronda. Vuelve a elegir.";
            // No se incrementa el contador de rondas jugadas en caso de empate, la ronda se repite.
        } else if ((eleccionUsuario.equals("Piedra") && eleccionServidor.equals("Tijeras")) ||
                   (eleccionUsuario.equals("Papel") && eleccionServidor.equals("Piedra")) ||
                   (eleccionUsuario.equals("Tijeras") && eleccionServidor.equals("Papel"))) {
            victoriasUsuario++;
            rondasJugadas++;  // Incrementa el contador de rondas jugadas cuando hay un ganador.
            resultadoRondaTexto = "Ganaste esta ronda.";
        } else {
            victoriasServidor++;
            rondasJugadas++; // Incrementa el contador de rondas jugadas cuando hay un ganador.
            resultadoRondaTexto = "Perdiste esta ronda.";
        }

        // Determina si los botones de jugada deben desactivarse (si se han jugado todas las rondas).
        boolean desactivarBoton = rondasJugadas >= TOTAL_RONDAS;
        // Variable para almacenar el mensaje final que se mostrará en el HTML.
        String mensajeCompleto;
        
        // Si el juego ha terminado (se han alcanzado el total de rondas).
        if (desactivarBoton) {
            // Se almacena el resultado final de la partida completa.
            String resultadoFinalJuego = victoriasUsuario > victoriasServidor ? "Ganaste el juego." : victoriasUsuario < victoriasServidor ? "Perdiste el juego." : "El juego ha terminado en empate.";
            mensajeCompleto = resultadoRondaTexto + " " + resultadoFinalJuego + " <a href='/ppt?reset=true'>Jugar otra vez.</a>";
        } else {
            // Si el juego no ha terminado, el mensaje es solo el resultado de la ronda actual
            mensajeCompleto = resultadoRondaTexto;
        }

        // Devuelve el HTML de la página del juego con el mensaje completo
        return Paginas.getPiedraPapelTijerasHtml(mensajeCompleto, ultimaJugadaUsuario, ultimaJugadaServidor, victoriasUsuario, victoriasServidor, rondasJugadas, desactivarBoton);
    }

    /**
     * Resets the game state for "Piedra, Papel o Tijeras".
     */
    public static void resetJuego() {
        victoriasUsuario = 0;
        victoriasServidor = 0;
        rondasJugadas = 0;
        ultimaJugadaUsuario = "";
        ultimaJugadaServidor = "";
    }
}