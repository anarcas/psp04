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
 * 
 */
public class Paginas {
    // TODO code application logic here

    /**
     * Genera el HTML para la página de inicio de sesión independiente.
     *
     * @param message Un mensaje para mostrar (ej. mensajes de error).
     * @return El HTML para la página de inicio de sesión.
     */
    public static String getLoginFormHtml(String message) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Inicio de Sesión</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
        html.append(".container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; text-align: center; }");
        html.append("h1 { color: #333; margin-bottom: 25px; }");
        html.append("form { display: flex; flex-direction: column; gap: 15px; }");
        html.append("label { text-align: left; margin-bottom: -10px; font-weight: bold; color: #555; }");
        html.append("input[type='email'], input[type='password'] { width: calc(100% - 20px); padding: 10px; margin-bottom: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }");
        html.append("input[type='submit'] { background-color: #4CAF50; color: white; padding: 12px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 10px; }");
        html.append("input[type='submit']:hover { background-color: #45a049; }");
        html.append(".message { color: red; margin-top: 15px; font-weight: bold; }");
        html.append(".register-link { margin-top: 20px; font-size: 14px; }");
        html.append(".register-link a { color: #007bff; text-decoration: none; }");
        html.append(".register-link a:hover { text-decoration: underline; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<h1>Inicio de Sesión</h1>");

        if (message != null && !message.isEmpty()) {
            html.append("<p class='message'>").append(message).append("</p>");
        }

        html.append("<form method='POST' action='/login'>");
        html.append("<label for='email'>Email:</label>");
        html.append("<input type='email' id='email' name='email' required pattern='^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}$' title='Introduce un email válido.'>");
        html.append("<label for='password'>Contraseña:</label>");
        html.append("<input type='password' id='password' name='password' required minlength='6' pattern='^[a-zA-Z0-9]{6,}$' title='La contraseña debe tener al menos 6 caracteres alfanuméricos.'>");
        html.append("<input type='submit' value='Iniciar Sesión'>");
        html.append("</form>");
        html.append("<p class='register-link'>¿No tienes cuenta? <a href='/register'>Regístrate aquí</a></p>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Genera el HTML para la página de registro independiente. Muestra un
     * formulario para que el usuario introduzca su email y contraseña.
     *
     * @param message Un mensaje para mostrar.
     * @return El HTML para la página de registro.
     */
    public static String getRegisterFormHtml(String message) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Registro de Usuario</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
        html.append(".container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; text-align: center; }");
        html.append("h1 { color: #333; margin-bottom: 25px; }");
        html.append("form { display: flex; flex-direction: column; gap: 15px; }");
        html.append("label { text-align: left; margin-bottom: -10px; font-weight: bold; color: #555; }");
        html.append("input[type='email'], input[type='password'] { width: calc(100% - 20px); padding: 10px; margin-bottom: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }");
        html.append("input[type='submit'] { background-color: #007bff; color: white; padding: 12px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 10px; }");
        html.append("input[type='submit']:hover { background-color: #0056b3; }");
        html.append(".message { color: red; margin-top: 15px; font-weight: bold; }");
        html.append(".login-link { margin-top: 20px; font-size: 14px; }");
        html.append(".login-link a { color: #007bff; text-decoration: none; }");
        html.append(".login-link a:hover { text-decoration: underline; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='container'>");
        html.append("<h1>Registro de Usuario</h1>");

        // Muestra el mensaje si existe y no está vacío.
        if (message != null && !message.isEmpty()) {
            html.append("<p class='message'>").append(message).append("</p>");
        }

        html.append("<form method='POST' action='/register'>");
        html.append("<label for='email'>Email:</label>");
        html.append("<input type='email' id='email' name='email' required pattern='^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}$' title='Introduce un email válido.'>");
        html.append("<label for='password'>Contraseña:</label>");
        // Se ha ajustado el patrón de la contraseña para reflejar lo que se espera (alfanuméricos de 6+ caracteres)
        html.append("<input type='password' id='password' name='password' required minlength='6' pattern='^[a-zA-Z0-9]{6,}$' title='La contraseña debe tener al menos 6 caracteres alfanuméricos.'>");
        html.append("<input type='submit' value='Registrarse'>");
        html.append("</form>");
        html.append("<p class='login-link'>¿Ya tienes cuenta? <a href='/login'>Inicia Sesión aquí</a></p>");
        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    /**
     * Método que genera el HTML para una página que contiene tanto el
     * formulario de inicio de sesión como el formulario de registro.
     *
     * @param loginMessage Un mensaje opcional para mostrar en el formulario de
     * inicio de sesión.
     * @param registerMessage Un mensaje opcional para mostrar en el formulario
     * de registro.
     * @return El HTML completo de la página combinada.
     */
    public static String getLoginAndRegisterFormsHtml(String loginMessage, String registerMessage) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Acceso / Registro</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; display: flex; justify-content: center; align-items: center; min-height: 100vh; margin: 0; }");
        html.append(".outer-container { display: flex; flex-direction: column; gap: 30px; }"); // Contenedor para ambos formularios
        html.append(".form-container { background-color: white; padding: 30px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); width: 100%; max-width: 400px; text-align: center; }"); // Contenedor para cada formulario
        html.append("h1 { color: #333; margin-bottom: 25px; }");
        html.append("form { display: flex; flex-direction: column; gap: 15px; }");
        html.append("label { text-align: left; margin-bottom: -10px; font-weight: bold; color: #555; }");
        html.append("input[type='email'], input[type='password'] { width: calc(100% - 20px); padding: 10px; margin-bottom: 5px; border: 1px solid #ddd; border-radius: 4px; box-sizing: border-box; }");
        html.append(".login-button { background-color: #4CAF50; color: white; padding: 12px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 10px; }");
        html.append(".login-button:hover { background-color: #45a049; }");
        html.append(".register-button { background-color: #007bff; color: white; padding: 12px 20px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 10px; }");
        html.append(".register-button:hover { background-color: #0056b3; }");
        html.append(".message { color: red; margin-top: 15px; font-weight: bold; }");
        html.append(".separator { margin-top: 30px; margin-bottom: 30px; width: 100%; border-top: 1px solid #eee; }"); // Separador
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='outer-container'>"); // Contenedor principal para ambos formularios

        // --- Cabecera del formulario ---
        html.append("<div class='form-container'>");
        html.append("<h1>Bienvenido</h1>");
        html.append("</div>"); // Cierra form-container de la cabecera

        // --- Formulario de Inicio de Sesión ---
        html.append("<div class='form-container'>");
        html.append("<h3>Inicio de Sesión</h3>");
        if (loginMessage != null && !loginMessage.isEmpty()) {
            html.append("<p class='message'>").append(loginMessage).append("</p>");
        }
        html.append("<form method='POST' action='/login'>");
        html.append("<label for='login-email'>Email:</label>"); // ID único para evitar conflictos
        html.append("<input type='email' id='login-email' name='email' required pattern='^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}$' title='Introduce un email válido.'>");
        html.append("<label for='login-password'>Contraseña:</label>"); // ID único
        html.append("<input type='password' id='login-password' name='password' required minlength='6' pattern='^[a-zA-Z0-9]{6,}$' title='La contraseña debe tener al menos 6 caracteres alfanuméricos.'>");
        html.append("<input type='submit' value='Iniciar Sesión' class='login-button'>");
        html.append("</form>");
        html.append("</div>"); // Cierra form-container de login

        // --- Formulario de Registro ---
        html.append("<div class='form-container'>");
        html.append("<h3>Registro de Usuario</h3>");
        if (registerMessage != null && !registerMessage.isEmpty()) {
            html.append("<p class='message'>").append(registerMessage).append("</p>");
        }
        html.append("<form method='POST' action='/register'>");
        html.append("<label for='register-email'>Email:</label>"); // ID único
        html.append("<input type='email' id='register-email' name='email' required pattern='^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,6}$' title='Introduce un email válido.'>");
        html.append("<label for='register-password'>Contraseña:</label>"); // ID único
        html.append("<input type='password' id='register-password' name='password' required minlength='6' pattern='^[a-zA-Z0-9]{6,}$' title='La contraseña debe tener al menos 6 caracteres alfanuméricos.'>");
        html.append("<input type='submit' value='Registrarse' class='register-button'>");
        html.append("</form>");
        html.append("</div>"); // Cierra form-container de register

        html.append("</div>"); // Cierra outer-container
        html.append("</body>");
        html.append("</html>");

        return html.toString();

    }

    /**
     * Genera el HTML del menú principal con enlaces a los tres juegos
     * disponibles y un botón de cierre de sesión.
     *
     * @return El código HTML del menú de juegos.
     */
    public static String getMenuHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Menú de Juegos</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #e0f2f7; /* Un azul claro para el fondo */");
        html.append("    display: flex;");
        html.append("    justify-content: center;");
        html.append("    align-items: center;");
        html.append("    min-height: 100vh;");
        html.append("    margin: 0;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".menu-container {");
        html.append("    background-color: #ffffff; /* Fondo blanco para la tarjeta */");
        html.append("    padding: 40px;");
        html.append("    border-radius: 12px; /* Esquinas más redondeadas */");
        html.append("    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15); /* Sombra más pronunciada */");
        html.append("    text-align: center;");
        html.append("    width: 90%;");
        html.append("    max-width: 450px; /* Ancho máximo para la tarjeta */");
        html.append("    box-sizing: border-box;");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #007bff; /* Azul primario para el título */");
        html.append("    margin-bottom: 35px; /* Más espacio debajo del título */");
        html.append("    font-size: 2.5em; /* Tamaño de fuente más grande */");
        html.append("    font-weight: 600; /* Un poco más de peso */");
        html.append("}");
        html.append("ul {");
        html.append("    list-style: none; /* Eliminar viñetas */");
        html.append("    padding: 0;");
        html.append("    margin: 0 0 30px 0; /* Espacio debajo de la lista */");
        html.append("}");
        html.append("li {");
        html.append("    margin-bottom: 18px; /* Espacio entre los elementos de la lista */");
        html.append("}");
        html.append(".game-button {");
        html.append("    display: block; /* Para que el enlace ocupe todo el ancho y se comporte como un botón */");
        html.append("    background-color: #007bff; /* Azul para los botones de juego */");
        html.append("    color: white;");
        html.append("    padding: 15px 25px;");
        html.append("    border: none;");
        html.append("    border-radius: 8px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1.3em; /* Tamaño de fuente más grande para los botones */");
        html.append("    text-decoration: none; /* Sin subrayado */");
        html.append("    transition: background-color 0.3s ease, transform 0.2s ease; /* Transición suave al pasar el ratón */");
        html.append("    width: 100%;");
        html.append("    box-sizing: border-box; /* Incluir padding y border en el ancho total */");
        html.append("}");
        html.append(".game-button:hover {");
        html.append("    background-color: #0056b3; /* Un azul más oscuro al pasar el ratón */");
        html.append("    transform: translateY(-3px); /* Pequeño efecto de elevación */");
        html.append("}");
        html.append(".logout-button {");
        html.append("    background-color: #dc3545; /* Rojo para el botón de cerrar sesión */");
        html.append("    color: white;");
        html.append("    padding: 12px 25px;");
        html.append("    border: none;");
        html.append("    border-radius: 6px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1.1em;");
        html.append("    text-decoration: none;");
        html.append("    transition: background-color 0.3s ease, transform 0.2s ease;");
        html.append("    margin-top: 25px; /* Espacio superior */");
        html.append("}");
        html.append(".logout-button:hover {");
        html.append("    background-color: #c82333; /* Un rojo más oscuro al pasar el ratón */");
        html.append("    transform: translateY(-2px);");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='menu-container'>"); // Contenedor principal para el menú
        html.append("<h1>Menú de Juegos</h1>");
        html.append("<ul>");
        html.append("<li><a href='/adivina' class='game-button'>Adivina el Número</a></li>");
        html.append("<li><a href='/dados' class='game-button'>Lanza Dados</a></li>");
        html.append("<li><a href='/ppt' class='game-button'>Piedra, Papel o Tijeras</a></li>");
        html.append("</ul>");
        html.append("<form method='POST' action='/logout'>");
        html.append("<input type='submit' value='Cerrar Sesión' class='logout-button'>");
        html.append("</form>");
        html.append("</div>"); // Cierra menu-container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
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
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Adivina el Número</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #e0f2f7; /* Azul claro de fondo */");
        html.append("    display: flex;");
        html.append("    justify-content: center;");
        html.append("    align-items: center;");
        html.append("    min-height: 100vh;");
        html.append("    margin: 0;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".game-container {");
        html.append("    background-color: #ffffff; /* Fondo blanco para la tarjeta */");
        html.append("    padding: 40px;");
        html.append("    border-radius: 12px; /* Esquinas redondeadas */");
        html.append("    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15); /* Sombra para efecto de elevación */");
        html.append("    text-align: center;");
        html.append("    width: 90%;");
        html.append("    max-width: 500px; /* Ancho máximo para la tarjeta */");
        html.append("    box-sizing: border-box;");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #007bff; /* Azul para el título */");
        html.append("    margin-bottom: 30px;");
        html.append("    font-size: 2.2em;");
        html.append("}");
        html.append("p {");
        html.append("    font-size: 1.1em;");
        html.append("    line-height: 1.6;");
        html.append("    margin-bottom: 15px;");
        html.append("}");
        html.append("form {");
        html.append("    display: flex;");
        html.append("    flex-direction: column; /* Apilar elementos del formulario */");
        html.append("    align-items: center; /* Centrar horizontalmente */");
        html.append("    gap: 15px; /* Espacio entre elementos del formulario */");
        html.append("    margin-bottom: 25px;");
        html.append("}");
        html.append("label {");
        html.append("    font-size: 1.1em;");
        html.append("    font-weight: bold;");
        html.append("    color: #555;");
        html.append("    margin-bottom: -5px; /* Ajuste para acercar al input */");
        html.append("}");
        html.append("input[type='number'] {");
        html.append("    width: 80%; /* Ancho del input */");
        html.append("    padding: 12px;");
        html.append("    border: 1px solid #ced4da;");
        html.append("    border-radius: 8px;");
        html.append("    font-size: 1.1em;");
        html.append("    text-align: center;");
        html.append("    box-sizing: border-box;");
        html.append("}");
        html.append(".submit-button {");
        html.append("    background-color: #28a745; /* Verde para el botón de enviar */");
        html.append("    color: white;");
        html.append("    padding: 12px 25px;");
        html.append("    border: none;");
        html.append("    border-radius: 6px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1.1em;");
        html.append("    transition: background-color 0.3s ease, transform 0.2s ease;");
        html.append("    width: auto; /* Ancho automático para el botón */");
        html.append("}");
        html.append(".submit-button:hover {");
        html.append("    background-color: #218838;");
        html.append("    transform: translateY(-2px);");
        html.append("}");
        html.append(".submit-button:disabled {");
        html.append("    background-color: #cccccc;");
        html.append("    cursor: not-allowed;");
        html.append("    transform: none;");
        html.append("}");
        html.append(".message {");
        html.append("    font-weight: bold;");
        html.append("    margin-top: 20px;");
        html.append("    font-size: 1.2em;");
        html.append("    color: #dc3545; /* Rojo para mensajes de error/informativos */");
        html.append("}");
        html.append(".success-message {"); // Clase para mensajes de éxito
        html.append("    color: #28a745; /* Verde para mensajes de éxito */");
        html.append("}");
        html.append(".nav-link-button {"); // Estilo para enlaces que parecen botones
        html.append("    display: inline-block;");
        html.append("    background-color: #007bff; /* Azul para el botón de volver */");
        html.append("    color: white;");
        html.append("    padding: 10px 20px;");
        html.append("    border-radius: 5px;");
        html.append("    text-decoration: none;");
        html.append("    font-size: 1em;");
        html.append("    margin-top: 25px;");
        html.append("    transition: background-color 0.3s ease;");
        html.append("}");
        html.append(".nav-link-button:hover {");
        html.append("    background-color: #0056b3;");
        html.append("}");
        html.append(".logout-form {");
        html.append("    margin-top: 20px;");
        html.append("}");
        html.append(".logout-button {");
        html.append("    background-color: #6c757d; /* Gris para el botón de cerrar sesión */");
        html.append("    color: white;");
        html.append("    padding: 10px 20px;");
        html.append("    border: none;");
        html.append("    border-radius: 5px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1em;");
        html.append("    transition: background-color 0.3s ease;");
        html.append("}");
        html.append(".logout-button:hover {");
        html.append("    background-color: #5a6268;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='game-container'>"); // Contenedor principal
        html.append("<h1>¡Adivina el Número!</h1>");
        html.append("<p>Introduce un número del 1 al 100:</p>");
        html.append("<form method='POST' action='/adivina'>");
        html.append("<label for='numeroPropuesto'>Tu número:</label>"); // ID 'adivina' cambiado a 'numeroPropuesto' para claridad con 'name'
        html.append("<input type='number' id='adivina' name='adivina' min='1' max='100' required").append(desactivarBoton ? "disabled" : "").append(">");
        html.append("<input type='submit' value='Enviar' class='submit-button' ").append(desactivarBoton ? "disabled" : "").append(">");
        html.append("</form>");
        html.append("<p>Intentos: ").append( intentos).append( "/10</p>");
        if (mensaje != null && !mensaje.isEmpty()) {
            // Determina si el mensaje es de éxito (ej. "CORRECTO") para aplicar el estilo de color verde
            if (mensaje.toUpperCase().contains("CORRECTO")) {
                html.append("<p class='message success-message'>").append( mensaje).append( "</p>");
            } else {
                html.append("<p class='message'>").append( mensaje).append( "</p>");
            }
        }
        html.append("<a href='/' class='nav-link-button'>Volver al menú principal</a>");
        html.append("<form method='POST' action='/logout' class='logout-form'>");
        html.append("<input type='submit' value='Cerrar Sesión' class='logout-button'>");
        html.append("</form>");
        html.append("</div>"); // Cierra game-container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
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
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Lanza Dados</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #e0f2f7; /* Azul claro de fondo */");
        html.append("    display: flex;");
        html.append("    justify-content: center;");
        html.append("    align-items: center;");
        html.append("    min-height: 100vh;");
        html.append("    margin: 0;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".game-container {");
        html.append("    background-color: #ffffff; /* Fondo blanco para la tarjeta */");
        html.append("    padding: 40px;");
        html.append("    border-radius: 12px; /* Esquinas redondeadas */");
        html.append("    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15); /* Sombra para efecto de elevación */");
        html.append("    text-align: center;");
        html.append("    width: 90%;");
        html.append("    max-width: 600px; /* Ancho máximo para la tarjeta, un poco más para la tabla */");
        html.append("    box-sizing: border-box;");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #007bff; /* Azul para el título */");
        html.append("    margin-bottom: 30px;");
        html.append("    font-size: 2.2em;");
        html.append("}");
        html.append("p {");
        html.append("    font-size: 1.1em;");
        html.append("    line-height: 1.6;");
        html.append("    margin-bottom: 15px;");
        html.append("}");
        html.append("form {");
        html.append("    display: flex;");
        html.append("    justify-content: center; /* Centrar el botón de lanzar */");
        html.append("    margin-bottom: 25px;");
        html.append("}");
        html.append(".submit-button {");
        html.append("    background-color: #28a745; /* Verde para el botón de lanzar */");
        html.append("    color: white;");
        html.append("    padding: 15px 30px; /* Más padding para que sea más grande */");
        html.append("    border: none;");
        html.append("    border-radius: 8px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1.3em; /* Letra más grande */");
        html.append("    transition: background-color 0.3s ease, transform 0.2s ease;");
        html.append("}");
        html.append(".submit-button:hover {");
        html.append("    background-color: #218838;");
        html.append("    transform: translateY(-3px);");
        html.append("}");
        html.append(".submit-button:disabled {");
        html.append("    background-color: #cccccc;");
        html.append("    cursor: not-allowed;");
        html.append("    transform: none;");
        html.append("}");
        html.append("table {");
        html.append("    width: 80%; /* Ancho de la tabla */");
        html.append("    border-collapse: separate; /* Para que border-radius funcione en la tabla */");
        html.append("    border-spacing: 0;");
        html.append("    margin: 25px auto; /* Centrar la tabla */");
        html.append("    font-size: 1.1em;");
        html.append("    text-align: center;");
        html.append("    border-radius: 10px; /* Bordes redondeados para la tabla */");
        html.append("    overflow: hidden; /* Asegura que los bordes redondeados se apliquen bien */");
        html.append("    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1); /* Sombra para la tabla */");
        html.append("}");
        html.append("th, td {");
        html.append("    padding: 15px;");
        html.append("    border-bottom: 1px solid #e0e0e0; /* Separadores de fila */");
        html.append("}");
        html.append("th {");
        html.append("    background-color: #007bff; /* Fondo azul para encabezados */");
        html.append("    color: white;");
        html.append("    font-weight: bold;");
        html.append("}");
        html.append("tr:nth-child(even) {");
        html.append("    background-color: #f9f9f9; /* Rayas de cebra */");
        html.append("}");
        html.append("tr:hover {");
        html.append("    background-color: #e9e9e9; /* Efecto hover en filas */");
        html.append("}");
        html.append(".message {");
        html.append("    font-weight: bold;");
        html.append("    margin-top: 20px;");
        html.append("    font-size: 1.2em;");
        html.append("    color: #dc3545; /* Rojo para mensajes de error/informativos */");
        html.append("}");
        html.append(".success-message {"); // Clase para mensajes de éxito
        html.append("    color: #28a745; /* Verde para mensajes de éxito */");
        html.append("}");
        html.append(".nav-link-button {");
        html.append("    display: inline-block;");
        html.append("    background-color: #007bff; /* Azul para el botón de volver */");
        html.append("    color: white;");
        html.append("    padding: 10px 20px;");
        html.append("    border-radius: 5px;");
        html.append("    text-decoration: none;");
        html.append("    font-size: 1em;");
        html.append("    margin-top: 25px;");
        html.append("    transition: background-color 0.3s ease;");
        html.append("}");
        html.append(".nav-link-button:hover {");
        html.append("    background-color: #0056b3;");
        html.append("}");
        html.append(".logout-form {");
        html.append("    margin-top: 20px;");
        html.append("}");
        html.append(".logout-button {");
        html.append("    background-color: #6c757d; /* Gris para el botón de cerrar sesión */");
        html.append("    color: white;");
        html.append("    padding: 10px 20px;");
        html.append("    border: none;");
        html.append("    border-radius: 5px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1em;");
        html.append("    transition: background-color 0.3s ease;");
        html.append("}");
        html.append(".logout-button:hover {");
        html.append("    background-color: #5a6268;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='game-container'>"); // Contenedor principal
        html.append("<h1>¡Lanza Dados!</h1>");
        html.append("<p>Pulsa el botón para lanzar los dados.</p>");
        html.append("<form method='POST' action='/dados'>");
        html.append("<input type='submit' value='Lanzar Dados' class='submit-button' ").append(desactivarBoton ? "disabled" : "").append(">");
        html.append("</form>");

        // Mostrar las tiradas de la ronda actual solo si ya ha habido alguna tirada
        if (tiradaUsuario > 0 || tiradaServidor > 0) {
            html.append("<p>Tu última tirada: <strong>").append(tiradaUsuario).append("</strong></p>");
            html.append("<p>Última tirada del servidor: <strong>").append(tiradaServidor).append("</strong></p>");
        }

        html.append("<h2>Resultados</h2>"); // Un subtítulo para la tabla
        html.append("<table>");
        html.append("<thead>");
        html.append("<tr>");
        html.append("<th></th>");
        html.append("<th>Puntuación</th>");
        html.append("<th>Última Tirada</th>"); // Cambiado de 'Tirada' a 'Última Tirada' para claridad
        html.append("</tr>");
        html.append("</thead>");
        html.append("<tbody>");
        html.append("<tr>");
        html.append("<td>Usuario</td>");
        html.append("<td>").append(puntuacionUsuario).append("</td>");
        html.append("<td>").append(tiradaUsuario > 0 ? tiradaUsuario : "-").append("</td>"); // Muestra '-' si no hay tirada aún
        html.append("</tr>");
        html.append("<tr>");
        html.append("<td>Servidor</td>");
        html.append("<td>").append(puntuacionServidor).append("</td>");
        html.append("<td>").append(tiradaServidor > 0 ? tiradaServidor : "-").append("</td>"); // Muestra '-' si no hay tirada aún
        html.append("</tr>");
        html.append("</tbody>");
        html.append("</table>");
        html.append("<p>Rondas jugadas: ").append(rondasJugadas).append("/5</p>");
        if (mensaje != null && !mensaje.isEmpty()) {
            // Aplicar estilo de mensaje de éxito o normal
            if (mensaje.contains("Ganaste el juego.") || mensaje.contains("El juego ha terminado en empate.")) {
                html.append("<p class='message success-message'>").append(mensaje).append("</p>");
            } else if (mensaje.contains("Perdiste el juego.")) {
                html.append("<p class='message'>").append(mensaje).append("</p>");
            } else {
                html.append("<p>").append(mensaje).append("</p>"); // Mensajes intermedios sin color especial
            }
        }
        html.append("<a href='/' class='nav-link-button'>Volver al menú principal</a>");
        html.append("<form method='POST' action='/logout' class='logout-form'>");
        html.append("<input type='submit' value='Cerrar Sesión' class='logout-button'>");
        html.append("</form>");
        html.append("</div>"); // Cierra game-container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
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
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<title>Piedra, Papel o Tijeras</title>");
        html.append("<style>");
        html.append("body {");
        html.append("    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;");
        html.append("    background-color: #e0f2f7; /* Azul claro de fondo */");
        html.append("    display: flex;");
        html.append("    justify-content: center;");
        html.append("    align-items: center;");
        html.append("    min-height: 100vh;");
        html.append("    margin: 0;");
        html.append("    color: #333;");
        html.append("}");
        html.append(".game-container {");
        html.append("    background-color: #ffffff; /* Fondo blanco para la tarjeta */");
        html.append("    padding: 40px;");
        html.append("    border-radius: 12px; /* Esquinas redondeadas */");
        html.append("    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15); /* Sombra para efecto de elevación */");
        html.append("    text-align: center;");
        html.append("    width: 90%;");
        html.append("    max-width: 550px; /* Ancho máximo para la tarjeta */");
        html.append("    box-sizing: border-box;");
        html.append("}");
        html.append("h1 {");
        html.append("    color: #007bff; /* Azul para el título */");
        html.append("    margin-bottom: 30px;");
        html.append("    font-size: 2.2em;");
        html.append("}");
        html.append("p {");
        html.append("    font-size: 1.1em;");
        html.append("    line-height: 1.6;");
        html.append("    margin-bottom: 15px;");
        html.append("}");
        html.append(".choice-form {"); // Estilo para el formulario de botones de elección
        html.append("    display: flex;");
        html.append("    justify-content: center; /* Centrar los botones */");
        html.append("    gap: 15px; /* Espacio entre los botones */");
        html.append("    margin-bottom: 25px;");
        html.append("}");
        html.append(".choice-button {");
        html.append("    background-color: #007bff; /* Azul para los botones de elección */");
        html.append("    color: white;");
        html.append("    padding: 15px 25px;");
        html.append("    border: none;");
        html.append("    border-radius: 8px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1.2em;");
        html.append("    transition: background-color 0.3s ease, transform 0.2s ease;");
        html.append("}");
        html.append(".choice-button:hover {");
        html.append("    background-color: #0056b3;");
        html.append("    transform: translateY(-3px);");
        html.append("}");
        html.append(".choice-button:disabled {");
        html.append("    background-color: #cccccc;");
        html.append("    cursor: not-allowed;");
        html.append("    transform: none;");
        html.append("}");
        html.append(".score-table {"); // Estilo específico para la tabla de puntuación
        html.append("    width: 70%; /* Ancho de la tabla */");
        html.append("    border-collapse: separate;");
        html.append("    border-spacing: 0;");
        html.append("    margin: 25px auto;");
        html.append("    font-size: 1.1em;");
        html.append("    text-align: center;");
        html.append("    border-radius: 10px;");
        html.append("    overflow: hidden;");
        html.append("    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);");
        html.append("}");
        html.append(".score-table th, .score-table td {");
        html.append("    padding: 15px;");
        html.append("    border-bottom: 1px solid #e0e0e0;");
        html.append("}");
        html.append(".score-table th {");
        html.append("    background-color: #007bff;");
        html.append("    color: white;");
        html.append("    font-weight: bold;");
        html.append("}");
        html.append(".score-table tr:nth-child(even) {");
        html.append("    background-color: #f9f9f9;");
        html.append("}");
        html.append(".score-table tr:hover {");
        html.append("    background-color: #e9e9e9;");
        html.append("}");
        html.append(".message {");
        html.append("    font-weight: bold;");
        html.append("    margin-top: 20px;");
        html.append("    font-size: 1.2em;");
        html.append("    color: #dc3545; /* Rojo para mensajes de error/informativos */");
        html.append("}");
        html.append(".success-message {"); // Clase para mensajes de éxito
        html.append("    color: #28a745; /* Verde para mensajes de éxito */");
        html.append("}");
        html.append(".nav-link-button {");
        html.append("    display: inline-block;");
        html.append("    background-color: #007bff; /* Azul para el botón de volver */");
        html.append("    color: white;");
        html.append("    padding: 10px 20px;");
        html.append("    border-radius: 5px;");
        html.append("    text-decoration: none;");
        html.append("    font-size: 1em;");
        html.append("    margin-top: 25px;");
        html.append("    transition: background-color 0.3s ease;");
        html.append("}");
        html.append(".nav-link-button:hover {");
        html.append("    background-color: #0056b3;");
        html.append("}");
        html.append(".logout-form {");
        html.append("    margin-top: 20px;");
        html.append("}");
        html.append(".logout-button {");
        html.append("    background-color: #6c757d; /* Gris para el botón de cerrar sesión */");
        html.append("    color: white;");
        html.append("    padding: 10px 20px;");
        html.append("    border: none;");
        html.append("    border-radius: 5px;");
        html.append("    cursor: pointer;");
        html.append("    font-size: 1em;");
        html.append("    transition: background-color 0.3s ease;");
        html.append("}");
        html.append(".logout-button:hover {");
        html.append("    background-color: #5a6268;");
        html.append("}");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");
        html.append("<div class='game-container'>"); // Contenedor principal
        html.append("<h1>¡Piedra, Papel o Tijeras!</h1>");
        html.append("<p>Elige tu jugada:</p>");
        html.append("<form method='POST' action='/ppt' class='choice-form'>"); // Formulario con flex para los botones
        html.append("<input type='submit' name='eleccion' value='Piedra' class='choice-button' ").append(desactivarBoton ? "disabled" : "").append( ">");
        html.append("<input type='submit' name='eleccion' value='Papel' class='choice-button' ").append(desactivarBoton ? "disabled" : "").append( ">");
        html.append("<input type='submit' name='eleccion' value='Tijeras' class='choice-button' ").append(desactivarBoton ? "disabled" : "").append( ">");
        html.append("</form>");

        if (eleccionUsuario != null && !eleccionUsuario.isEmpty()) {
            html.append("<p>Tú elegiste: <strong>").append( eleccionUsuario).append( "</strong></p>");
        }
        if (eleccionServidor != null && !eleccionServidor.isEmpty()) {
            html.append("<p>El servidor eligió: <strong>").append( eleccionServidor).append( "</strong></p>");
        }

        html.append("<h2>Puntuación</h2>"); // Subtítulo para la tabla de puntuación
        html.append("<table class='score-table'>");
        html.append("<thead>");
        html.append("<tr><th></th><th>Victorias</th></tr>");
        html.append("</thead>");
        html.append("<tbody>");
        html.append("<tr><td>Usuario</td><td>").append( victoriasUsuario).append( "</td></tr>");
        html.append("<tr><td>Servidor</td><td>").append( victoriasServidor).append( "</td></tr>");
        html.append("</tbody>");
        html.append("</table>");

        html.append("<p>Rondas jugadas: ").append(rondasJugadas).append( "/5</p>");
        if (mensaje != null && !mensaje.isEmpty()) {
            // Aplicar estilo de mensaje de éxito o normal
            if (mensaje.contains("Ganaste el juego.") || mensaje.contains("El juego ha terminado en empate.")) {
                html.append("<p class='message success-message'>").append( mensaje).append( "</p>");
            } else if (mensaje.contains("Perdiste el juego.")) {
                html.append("<p class='message'>").append( mensaje).append( "</p>");
            } else {
                html.append("<p>").append( mensaje).append( "</p>"); // Mensajes intermedios sin color especial
            }
        }
        html.append("<a href='/' class='nav-link-button'>Volver al menú principal</a>");
        html.append("<form method='POST' action='/logout' class='logout-form'>");
        html.append("<input type='submit' value='Cerrar Sesión' class='logout-button'>");
        html.append("</form>");
        html.append("</div>"); // Cierra game-container
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }
}