/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

/**
 * Clase que contiene métodos estáticos para generar las páginas HTML utilizadas
 * en los juegos interactivos. Cada método genera una página HTML diferente
 * según el juego y el estado de la solicitud.
 *
 * @author Antonio Naranjo Castillo
 */
public class Paginas {

    /**
     * Generates the HTML for the login page.
     * @param message A message to display (e.g., error messages).
     * @return The HTML for the login page.
     */
    public static String getLoginFormHtml(String message) {
        return "<html><body>"
                + "<h1>Inicio de Sesión</h1>"
                + "<form method='POST' action='/login' style='display: flex; flex-direction: column; width: 300px;'>"
                + "<label for='email'>Email:</label>"
                + "<input type='email' id='email' name='email' required pattern='^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$' title='Introduce un email válido.'>"
                + "<label for='password'>Contraseña:</label>"
                + "<input type='password' id='password' name='password' required minlength='6' pattern='^[a-zA-Z0-9]{6,}$' title='La contraseña debe tener al menos 6 caracteres alfanuméricos.'>"
                + "<input type='submit' value='Iniciar Sesión' style='margin-top: 10px;'>"
                + "</form>"
                + "<p><a href='/register'>¿No tienes cuenta? Regístrate aquí.</a></p>"
                + (message != null && !message.isEmpty() ? "<p style='color:red;'>" + message + "</p>" : "")
                + "</body></html>";
    }

    /**
     * Generates the HTML for the registration page.
     * @param message A message to display (e.g., error messages).
     * @return The HTML for the registration page.
     */
    public static String getRegisterFormHtml(String message) {
        return "<html><body>"
                + "<h1>Registro de Usuario</h1>"
                + "<form method='POST' action='/register' style='display: flex; flex-direction: column; width: 300px;'>"
                + "<label for='email'>Email:</label>"
                + "<input type='email' id='email' name='email' required pattern='^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$' title='Introduce un email válido.'>"
                + "<label for='password'>Contraseña:</label>"
                + "<input type='password' id='password' name='password' required minlength='6' pattern='^[a-zA-Z0-9]{6,}$' title='La contraseña debe tener al menos 6 caracteres alfanuméricos.'>"
                + "<input type='submit' value='Registrarse' style='margin-top: 10px;'>"
                + "</form>"
                + "<p><a href='/login'>¿Ya tienes cuenta? Inicia Sesión.</a></p>"
                + (message != null && !message.isEmpty() ? "<p style='color:red;'>" + message + "</p>" : "")
                + "</body></html>";
    }

    /**
     * Genera el HTML del menú principal con enlaces a los tres juegos
     * disponibles y un botón de cierre de sesión.
     *
     * @return El código HTML del menú de juegos.
     */
    public static String getMenuHtml() {
        return "<html><body>"
                + "<h1>Menú de Juegos</h1>"
                + "<ul>"
                + "<li><a href='/adivina'>Adivina el Número</a></li>"
                + "<li><a href='/dados'>Lanza Dados</a></li>"
                + "<li><a href='/ppt'>Piedra, Papel o Tijeras</a></li>"
                + "</ul>"
                + "<form method='POST' action='/logout'>"
                + "<input type='submit' value='Cerrar Sesión'>"
                + "</form>"
                + "</body></html>";
    }

    /**
     * Genera el HTML para el juego "Adivina el Número". Muestra un formulario
     * para ingresar un número, el número de intentos y un mensaje de estado.
     *
     * @param mensaje El mensaje a mostrar en la página (ej. resultado de la
     * adivinanza).
     * @param intentos El número de intentos realizados.
     * @param desactivarBoton Si es verdadero, deshabilita el botón de enviar.
     * @return El código HTML para el juego "Adivina el Número".
     */
    public static String getAdivinarNumeroHtml(String mensaje, int intentos, boolean desactivarBoton) {
        return "<html>"
                + "<body>"
                + "<h1>¡Adivina el Número!</h1>"
                + "<form method='POST' action='/adivina' style='display: flex; align-items: center;'>"
                + "<label for='adivina'>Introduce un número del 1 al 100:</label>"
                + "<input type='number' id='adivina' name='adivina' min='1' max='100' required style='margin-left: 10px;' " + (desactivarBoton ? "disabled" : "") + ">"
                + "<input type='submit' value='Enviar' style='margin-left: 10px;' " + (desactivarBoton ? "disabled" : "") + ">"
                + "</form>"
                + "<p>Intentos: " + intentos + "/10</p>"
                + "<p>" + mensaje + "</p>"
                + "<form method='POST' action='/logout' style='margin-top: 20px;'>"
                + "<input type='submit' value='Cerrar Sesión'>"
                + "</form>"
                + "</body>"
                + "</html>";
    }

    /**
     * Genera el HTML para el juego "Lanza Dados". Muestra el formulario para
     * lanzar los dados, las puntuaciones y los resultados de las rondas.
     *
     * @param mensaje El mensaje a mostrar en la página (ej. resultado del
     * lanzamiento).
     * @param puntuacionUsuario La puntuación del usuario.
     * @param puntuacionServidor La puntuación del servidor.
     * @param rondasJugadas El número de rondas jugadas.
     * @param tiradaUsuario El resultado de la tirada del usuario.
     * @param tiradaServidor El resultado de la tirada del servidor.
     * @param desactivarBoton Si es verdadero, deshabilita el botón de lanzar
     * dados.
     * @return El código HTML para el juego "Lanza Dados".
     */
    public static String getLanzarDadosHtml(String mensaje, int puntuacionUsuario, int puntuacionServidor, int rondasJugadas, int tiradaUsuario, int tiradaServidor, boolean desactivarBoton) {
        return "<html>"
                + "<body>"
                + "<h1>¡Lanza Dados!</h1>"
                + "<form method='POST' action='/dados'>"
                + "<input type='submit' value='Lanzar Dados' " + (desactivarBoton ? "disabled" : "") + ">"
                + "</form>"
                + "<p>Pulsa el botón para lanzar los dados</p>"
                + "<p>Rondas jugadas: " + rondasJugadas + "/5</p>"
                + "<table style='width: 50%; border-collapse: collapse; margin: 25px 0; font-size: 18px; text-align: center;'>"
                + "<thead>"
                + "<tr>"
                + "<th></th>"
                + "<th>Puntuación</th>"
                + "<th>Tirada</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + "<tr>"
                + "<td>Usuario</td>"
                + "<td>" + puntuacionUsuario + "</td>"
                + "<td>" + tiradaUsuario + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td>Servidor</td>"
                + "<td>" + puntuacionServidor + "</td>"
                + "<td>" + tiradaServidor + "</td>"
                + "</tr>"
                + "</tbody>"
                + "</table>"
                + "<p>" + mensaje + "</p>"
                + "<form method='POST' action='/logout' style='margin-top: 20px;'>"
                + "<input type='submit' value='Cerrar Sesión'>"
                + "</form>"
                + "</body>"
                + "</html>";
    }

    /**
     * Genera el HTML para el juego "Piedra, Papel o Tijeras". Muestra las
     * opciones de jugada para el usuario, el puntaje y el resultado de la
     * ronda.
     *
     * @param mensaje El mensaje a mostrar en la página (ej. resultado de la
     * ronda).
     * @param eleccionUsuario La elección del usuario (Piedra, Papel o Tijeras).
     * @param eleccionServidor La elección del servidor (Piedra, Papel o Tijeras).
     * @param victoriasUsuario La cantidad de rondas ganadas por el usuario.
     * @param victoriasServidor La cantidad de rondas ganadas por el servidor.
     * @param rondasJugadas El número de rondas jugadas.
     * @param desactivarBoton Si es verdadero, deshabilita los botones de jugada.
     * @return El código HTML para el juego "Piedra, Papel o Tijeras".
     */
    public static String getPiedraPapelTijerasHtml(String mensaje, String eleccionUsuario, String eleccionServidor, int victoriasUsuario, int victoriasServidor, int rondasJugadas, boolean desactivarBoton) {
        return "<html>"
                + "<body>"
                + "<h1>¡Piedra, Papel o Tijeras!</h1>"
                + "<form method='POST' action='/ppt'>"
                + "<input type='submit' name='eleccion' value='Piedra' " + (desactivarBoton ? "disabled" : "") + ">"
                + "<input type='submit' name='eleccion' value='Papel' " + (desactivarBoton ? "disabled" : "") + ">"
                + "<input type='submit' name='eleccion' value='Tijeras' " + (desactivarBoton ? "disabled" : "") + ">"
                + "</form>"
                + "<p>Rondas jugadas: " + rondasJugadas + "/5</p>"
                + "<p>Puntuación: Usuario " + victoriasUsuario + " - Servidor " + victoriasServidor + "</p>"
                + (eleccionUsuario != null && !eleccionUsuario.isEmpty() ? "<p>Tú elegiste: " + eleccionUsuario + "</p>" : "")
                + (eleccionServidor != null && !eleccionServidor.isEmpty() ? "<p>El servidor eligió: " + eleccionServidor + "</p>" : "")
                + "<p>" + mensaje + "</p>"
                + "<form method='POST' action='/logout' style='margin-top: 20px;'>"
                + "<input type='submit' value='Cerrar Sesión'>"
                + "</form>"
                + "</body>"
                + "</html>";
    }
}