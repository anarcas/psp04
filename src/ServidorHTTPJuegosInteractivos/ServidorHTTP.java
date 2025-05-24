/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import org.mindrot.jbcrypt.BCrypt; // Import BCrypt library
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase principal del servidor HTTP que maneja las solicitudes de los clientes
 * y responde con el contenido HTML correspondiente a los juegos interactivos.
 * Actúa como el punto de entrada para las conexiones de los clientes.
 * Cada conexión entrante es manejada por un hilo separado, permitiendo la concurrencia.
 * El servidor procesa las solicitudes HTTP, gestiona la autenticación y las sesiones,
 * y delega la lógica de los juegos y la generación de HTML a otras clases.
 *
 * @author Antonio Naranjo Castillo
 */
public class ServidorHTTP {
    
    // Obtener una instancia del Logger para esta clase
    private static final Logger logger = Logger.getLogger(ServidorHTTP.class.getName());
    
    /**
     * Método principal que lanza el servidor HTTP y maneja las excepciones IO.
     * 
     * @param args Los argumentos de la línea de comandos.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public static void main(String[] args) throws IOException {
        // Se configura el entorno SSL/TLS estableciendo las propiedades del sistema para el KeyStore.
        System.setProperty("javax.net.ssl.keyStore", "AlmacenSSL"); // Ruta del archivo almacén SLL (directorio raiz del proyecto) que contiene los datos pares clave-certificado necesarios para la autenticación de los usuarios.
        System.setProperty("javax.net.ssl.keyStorePassword", "123456"); // Definición de la contraseña de acceso.

        SSLServerSocket serverSocket = null; // Se inicia el servidor encriptado mediante protocolo SSL. 
        try {
            /*
             * Se emplea el método estático getDefault() de la superclase
             * SocketFactory, realizando un casting explícito para almacenar su
             * resultado en un objeto tipo SSLServerSocketFactory, es decir, se
             * inicia un servidor seguro con autenticación de claves y
             * validación de certificados.
             */
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            /*
             * Se crea un servidor seguro a partir del objeto fábrica
             * anterioremente iniciado, escuchando las conexiones o peticiones
             * de los usuarios en el puerto 8066.
             */
            serverSocket = (SSLServerSocket) ssf.createServerSocket(8066);
            // Se muestran mensajes en en la pantalla de la consola.
//            System.out.println("Servidor HTTPS iniciado en el puerto 8066");
//            System.out.println("Visita https://localhost:8066");

            logger.log(Level.INFO, "Servidor HTTPS iniciado en el puerto 8066\nVisita https://localhost:8066 \n");
            
            // Mientras exista una aceptación de conexión al servidor se inicia un hilo servidor para atender al cliente.
            while (true) {
                Socket cliente = serverSocket.accept(); // Aceptar la conexión del cliente
                logger.log(Level.INFO, String.format("Cliente conectado desde: %s", cliente.getInetAddress().getHostAddress()));

                // Manejar la petición del cliente en un nuevo hilo
                Thread hiloservidor = new HiloServidor(cliente);
                hiloservidor.start();
            }
            
        } catch (IOException e) {
//            System.err.println("Error al iniciar el servidor SSL: " + e.getMessage());
//            e.printStackTrace();
//            System.err.println("Asegúrese de que el fichero 'AlmacenSSL' existe en la raíz del proyecto, es un keystore válido y la contraseña es correcta.");

            // Logear el error crítico al iniciar el servidor usando el logger
            logger.log(Level.SEVERE, String.format("Error crítico al iniciar el servidor SLL: %s. %s.", e.getMessage(), e));
            // Si el servidor no puede iniciar, se cierra el programa finaliando sin exito de conexión.
            System.exit(1);
        } finally {
            // Se cierra el servidor
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    logger.log(Level.INFO, "Servidor cerrado.");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, String.format("Error al cerrar el ServerSocket: %s. %s.", e.getMessage(), e));
                }
            }
        }

    }

    /**
     * Clase interna que representa un hilo para manejar las solicitudes de un
     * cliente.
     */
    static class HiloServidor extends Thread {
        // Registrador de mensajes de información, advertencias y errores del servidor.
        private static final Logger logger = Logger.getLogger(HiloServidor.class.getName());
        // Conexión de red establecida con un cliente. Comunicación entre cliente y servidor.
        private final Socket cliente;
        // Validador del formato del correo electrónico de los usuarios.
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        // Validador del formato de la contraseña de los usuarios.
        private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9]{6,}$");

        /**
         * Constructor que inicializa el hilo con el socket del cliente.
         *
         * @param cliente El socket que representa la conexión del cliente.
         */
        public HiloServidor(Socket cliente) {
            this.cliente = cliente;
        }

        /**
         * Método que se ejecuta cuando se inicia el hilo. Maneja la solicitud
         * HTTP y genera una respuesta.
         */
        @Override
        public void run() {
            try (
                    // Se define el flujo de entrada.
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8));
                    // Se define el flujo de salida.
                    PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true)) {
                    // Se recoge la primera petición cuando se establezcan los flujos de entrada y salida.
                    String primeraLineaPeticion = entrada.readLine();
                    
                    // Se gestiona una primera línea de solicitud nula.
                    if (primeraLineaPeticion == null) {
                        logger.log(Level.SEVERE, "Petición HTTP nula, cierre prematuro del servidor.");
                    } else {
                    // En caso de no ser nula se imprime por pantalla .
                    System.out.printf("Línea de Petición: %s.%n", primeraLineaPeticion);
                    // Se descompone la solicitud HTTP delimitada por un espacio y las partes se almacenan en una lista.
                    String[] partesSolicitudHTTP = primeraLineaPeticion.split(" ");
                    // Se gestiona una solicitud mal formada en caso que el numero de partes sea inferior a dos.
                    if (partesSolicitudHTTP.length < 2) {
                        // Línea de solicitud mal formada.
                        logger.log(Level.WARNING, String.format("Línea de solicitud mal formada: %s.", primeraLineaPeticion));
                        // Se envía respuesta del servidor al cliente.
                        salida.printf("HTTP/1.1 400 Solicitud incorrecta%nContent-Type: text/plain%n%nSolicitud incorrecta");
                    } else {
                        
                        // Se declaran las variables necesarias, y se inician las necesarias.
                        String metodo = partesSolicitudHTTP[0];
                        String ruta = partesSolicitudHTTP[1];
                        int contentLength = 0;
                        String cookieHeader = null;
                        String linea;

                        /*
                         * Bucle para leer cada línea de las cabeceras HTTP de
                         * la petición entrante. Continúa mientras haya líneas y
                         * no se encuentre una línea vacía (que marca el fin de
                         * las cabeceras).
                         */
                        while ((linea = entrada.readLine()) != null && !linea.isEmpty()) {
                            System.out.printf("Línea de Encabezado: %s.%n", linea);
                            if (linea.toLowerCase().startsWith("content-length: ")) {
                                try {
                                    contentLength = Integer.parseInt(linea.substring(16).trim());
                                } catch (NumberFormatException e) {
                                    logger.log(Level.SEVERE, String.format("Error al leer Content-Length: %s %s",  e.getMessage(), e));
                                }
                            } else if (linea.toLowerCase().startsWith("cookie: ")) {
                                cookieHeader = linea.substring(8).trim();
                            }
                        }
                        System.out.println("Fin de encabezados.");

                        // Se inicializa un StringBuilder para construir el cuerpo de la petición.
                        StringBuilder cuerpo = new StringBuilder();
                        // Se verifica si la petición es de tipo POST y si tiene un cuerpo (Content-Length > 0).
                        if (metodo.equals("POST") && contentLength > 0) {
                            // Se crea un búfer de caracteres del tamaño del cuerpo para leer los datos.
                            char[] buffer = new char[contentLength];
                            int bytesLeidos = 0; // Contador de bytes leídos.
                            // Bucle para leer bytes del cuerpo de la petición hasta alcanzar contentLength.
                            while (bytesLeidos < contentLength) {
                                // Bytes del flujo de entrada al búfer.
                                int read = entrada.read(buffer, bytesLeidos, contentLength - bytesLeidos);
                                if (read == -1) { // Se devuelve -1 si se alcanzó el fin del flujo.
                                    break; // Salida del bucle While si se llega al fin del flujo antes de leer todo el cuerpo.
                                }
                                bytesLeidos += read; // Actualiza el contador de bytes leídos.
                            }
                            // Se añade los caracteres leídos del búfer al StringBuilder.
                            cuerpo.append(buffer, 0, bytesLeidos);
                        }
                        // Se imprime el cuerpo completo de la petición en la consola del servidor para depuración.
                        System.out.printf("Cuerpo de la Petición: %s.%n", cuerpo.toString());

                        // Variable que extrae el ID de sesión de la cabecera 'Cookie' de la petición.
                        String idSesion = extractSessionIdFromCookie(cookieHeader);
                        // Valida el ID de sesión para obtener el email del usuario; null si la sesión no es válida.
                        String emailUsuario = SessionManager.validateSession(idSesion);
                        
                        // Inicialización de variables para la respuesta del servidor
                        String respuestaHtml = ""; // Almacenará el contenido HTML a enviar al cliente.
                        String establecerCookieCabecera = null; // Se usará para enviar una nueva cookie.
                        String ubicacionRedireccion = null; // Para indicar una redirección HTTP a otra URL.

                        // --- Bloque principal para manejar las diferentes rutas de autenticación y juegos ---
                        // Maneja las rutas relacionadas con el login, registro y logout.
                        if (ruta.equals("/login")) {
                            if (metodo.equals("GET")) {
                                // Si es una petición GET a /login, devuelve el formulario de inicio de sesión.
                                respuestaHtml = Paginas.getLoginFormHtml(null);
                            } else if (metodo.equals("POST")) {
                                // Si es una petición POST a /login, procesa los datos del formulario.
                                String[] datosFormulario = cuerpo.toString().split("&"); // Divide el cuerpo en pares clave=valor.
                                String email = null;
                                String password = null;
                                // Itera sobre los pares de datos del formulario para extraer email y password.
                                for (String parametro : datosFormulario) {
                                    String[] parClaveValor = parametro.split("=", 2); // Divide cada par en clave y valor.  
                                    if (parClaveValor.length == 2) {
                                        String clave = parClaveValor[0];
                                        // Decodifica el valor para manejar caracteres especiales de URL
                                        String valor = java.net.URLDecoder.decode(parClaveValor[1], StandardCharsets.UTF_8.name());
                                        if (clave.equals("email")) {
                                            email = valor;
                                        } else if (clave.equals("password")) {
                                            password = valor;
                                        }
                                    }
                                }
                                
                                // --- Validaciones de entrada del formulario de login ---
                                if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                                    // Error: campos vacíos. Muestra el formulario con un mensaje.
                                    respuestaHtml = Paginas.getLoginFormHtml("Email o contraseña no pueden estar vacíos.");
                                } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                                    respuestaHtml = Paginas.getLoginFormHtml("El formato del email no es válido.");
                                    ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                            "Formato de email inválido en login.", email);
                                } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                                    // Error: formato de email inválido. Muestra el formulario con un mensaje y registra el error.
                                    respuestaHtml = Paginas.getLoginFormHtml("La contraseña debe tener al menos 6 caracteres alfanuméricos.");
                                    ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                            "Formato de contraseña inválido en login.", "***********"); // Oculta la contraseña en el log
                                } else {
                                    // --- Autenticación del usuario ---
                                    if (autenticacionUsuario(email, password)) {
                                        // Autenticación exitosa: crea una nueva sesión y establece la cookie.
                                        String newSessionId = SessionManager.createSession(email);
                                        establecerCookieCabecera = "session_id=" + newSessionId + "; Path=/; HttpOnly; Secure; SameSite=Lax";
                                        ubicacionRedireccion = "/menu"; // Redirige al usuario al menú principal.
                                    } else {
                                        // Autenticación fallida: muestra el formulario de login con un mensaje de error.
                                        respuestaHtml = Paginas.getLoginFormHtml("Email o contraseña incorrectos.");
                                    }
                                }
                            }
                        } else if (ruta.equals("/register")) {
                            // Bloque similar al de login, pero para el registro de nuevos usuarios.
                            if (metodo.equals("GET")) {
                                // Si es una petición GET a /register, devuelve el formulario de registro.
                                respuestaHtml = Paginas.getRegisterFormHtml(null);
                            } else if (metodo.equals("POST")) {
                                // Si es una petición POST a /register, procesa los datos del formulario.
                                String[] formData = cuerpo.toString().split("&");
                                String email = null;
                                String password = null;
                                // Extrae email y password del cuerpo de la petición.
                                for (String param : formData) {
                                    String[] pair = param.split("=", 2);
                                    if (pair.length == 2) {
                                        String key = pair[0];
                                        String value = java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
                                        if (key.equals("email")) {
                                            email = value;
                                        } else if (key.equals("password")) {
                                            password = value;
                                        }
                                    }
                                }

                                // --- Validaciones de entrada del formulario de registro ---
                                if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                                    // Error: campos vacíos.
                                    respuestaHtml = Paginas.getRegisterFormHtml("Email o contraseña no pueden estar vacíos.");
                                } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                                    // Error: formato de email inválido.
                                    respuestaHtml = Paginas.getRegisterFormHtml("El formato del email no es válido.");
                                    ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                            "Formato de email inválido en registro.", email);
                                } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                                    // Error: formato de contraseña inválido.
                                    respuestaHtml = Paginas.getRegisterFormHtml("La contraseña debe tener al menos 6 caracteres alfanuméricos.");
                                    ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                            "Formato de contraseña inválido en registro.", "***********"); // Oculta la contraseña
                                } else {
                                    // --- Registro del usuario ---
                                    // Hashea la contraseña antes de guardarla para seguridad.
                                    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                                    // Intenta añadir el nuevo usuario a la base de datos/archivo.
                                    if (FileManager.addUser(email, hashedPassword)) {
                                        // Registro exitoso: crea una sesión para el nuevo usuario y redirige.
                                        String newSessionId = SessionManager.createSession(email);
                                        establecerCookieCabecera = "session_id=" + newSessionId + "; Path=/; HttpOnly; Secure; SameSite=Lax";
                                        ubicacionRedireccion = "/menu"; // Redirect to game menu
                                    } else {
                                        // Fallo en el registro si el email ya existe.
                                        respuestaHtml = Paginas.getRegisterFormHtml("El email ya está registrado.");
                                    }
                                }
                            }
                        } else if (ruta.equals("/logout") && metodo.equals("POST")) { 
                            // Maneja la petición de cierre de sesión (logout). Se espera un POST para mayor seguridad.
                            if (idSesion != null) {
                                // Invalida la sesión activa.
                                SessionManager.invalidateSession(idSesion);
                                // Establece una cookie para expirar la sesión en el navegador, borrándola.
                                establecerCookieCabecera = "session_id=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; Secure; SameSite=Lax";
                            }
                            ubicacionRedireccion = "/login"; // Redirige al usuario a la página de login
                        } else {
                            // --- Manejo de rutas protegidas y lógica de juegos ---
                            // Si la ruta no es /login o /register, se asume que requiere autenticación.
                            if (emailUsuario == null && !ruta.equals("/login") && !ruta.equals("/register")) {
                                // Si no hay una sesión válida, redirige al login.
                                ubicacionRedireccion = "/login";
                            } else {
                                // Si hay sesión válida o es una de las rutas públicas, gestiona la solicitud del juego o contenido.
                                // Pasa el método, la ruta y el cuerpo de la petición a la función que gestiona los juegos.
                                respuestaHtml = gestionarSolicitud(metodo, ruta, cuerpo.toString());
                            }
                        }

                        // Bloque para enviar la respuesta HTTP al cliente.
                        if (ubicacionRedireccion != null) {
                            // --- Manejo de Redirecciones (HTTP 302 Found) ---
                            /*
                             * Si la variable 'ubicacionRedireccion' tiene un
                             * valor, significa que el servidor quiere redirigir
                             * al cliente a otra URL después de un login/logout exitoso.
                             */
                            salida.println("HTTP/1.1 302 Found");
                            salida.println("Location: " + ubicacionRedireccion);
                            if (establecerCookieCabecera != null) {
                                // Si hay una cookie de sesión para establecer, como una nueva sesión o limpiar una existente, entonces se incluye.
                                salida.printf("Set-Cookie: %s%n", establecerCookieCabecera);
                            }
                            // Indica al cliente que la conexión se cerrará después de esta respuesta.
                            salida.printf("Connection: close%n");
                            // Línea en blanco que separa las cabeceras del cuerpo de la respuesta (vacío en una redirección).
                            salida.println();
                        } else {
                            // --- Manejo de Respuestas con Contenido HTML o Errores (sin redirección) ---
                            // Si no hay una redirección pendiente, el servidor se prepara para enviar contenido HTML.
                            if (respuestaHtml == null || respuestaHtml.isEmpty()) {
                                // Este bloque se ejecuta si no se ha generado ningún HTML para la respuesta.
                                // Se intenta manejar casos específicos de rutas vacías o no manejadas.
                                if (emailUsuario != null && (ruta.equals("/") || ruta.isEmpty())) {
                                    // Si el usuario está logeado y solicita la raíz ("/" o ""):
                                    // Se asume que debe ser redirigido al menú de juegos.
                                    salida.println("HTTP/1.1 302 Found"); // Envía un código de redirección.
                                    salida.println("Location: /menu"); // Redirige al menú.
                                    if (establecerCookieCabecera != null) {
                                        salida.println("Set-Cookie: " + establecerCookieCabecera); // Incluye la cookie si aplica.
                                    }
                                    salida.println("Connection: close");
                                    salida.println();
                                } else if (emailUsuario == null && (ruta.equals("/") || ruta.isEmpty())) { // If not logged in and at root, redirect to login
                                    // Si el usuario NO está logeado y solicita la raíz ("/" o ""):
                                    // Se asume que debe ser redirigido a la página de login.
                                    salida.println("HTTP/1.1 302 Found"); // Envía un código de redirección.
                                    salida.println("Location: /login"); // Redirige al login.
                                    if (establecerCookieCabecera != null) {
                                        salida.println("Set-Cookie: " + establecerCookieCabecera); // Incluye la cookie si aplica.
                                    }
                                    salida.println("Connection: close");
                                    salida.println();
                                } else { 
                                    // Si no se generó HTML y no se aplicó ninguna redirección específica,
                                    // se envía una respuesta de "404 Not Found" (Recurso no encontrado).
                                    salida.println("HTTP/1.1 404 Not Found");
                                    salida.println("Content-Type: text/html; charset=UTF-8");
                                    salida.println("Connection: close");
                                    salida.println();
                                    // Envía un cuerpo HTML simple para el error 404.
                                    salida.println("<html><body><h1>404 Not Found</h1><p>The requested resource was not found.</p></body></html>");
                                }
                            } else {
                                // --- Manejo de Respuestas Exitosas con Contenido HTML (HTTP 200 OK) ---
                                // Este bloque se ejecuta cuando se ha generado contenido HTML para la respuesta.
                                salida.println("HTTP/1.1 200 OK"); // Envía el código de estado HTTP 200 (Éxito).
                                salida.println("Content-Type: text/html; charset=UTF-8"); // Indica que el contenido es HTML y su codificación.
                                if (establecerCookieCabecera != null) {
                                    salida.printf("Set-Cookie: %s%n", establecerCookieCabecera); // Incluye la cookie si aplica (ej. mantener sesión).
                                }
                                // Calcula y envía la longitud del contenido HTML para que el cliente sepa cuánto leer.
                                salida.printf("Content-Length: %s", respuestaHtml.getBytes(StandardCharsets.UTF_8).length);
                                salida.println("Connection: close"); // Indica que la conexión se cerrará.
                                salida.println(); // Línea en blanco que separa las cabeceras del cuerpo.
                                salida.println(respuestaHtml); // Envía el contenido HTML generado.
                            }
                        }
                    }
                }
            } catch (IOException e) {
                // --- Manejo de Errores de Entrada/Salida (IOException) ---
                logger.log(Level.SEVERE, "Error handling client request", e);
            } finally {
                // Se asegura que los recursos críticos (como el socket del cliente) se cierren.
                try {
                    if (cliente != null && !cliente.isClosed()) {
                        // Si el socket del cliente existe y no está ya cerrado, se procede a cerrarlo.
                        // Esto libera el puerto y los recursos de red asociados a esta conexión.
                        cliente.close();
                    }
                } catch (IOException e) {
                    // Si ocurre un error al intentar cerrar el socket del cliente, se registra en el log
                    logger.log(Level.SEVERE, "Error closing client socket", e);
                }
            }
        }

        /**
         * Authenticates a user against stored credentials.
         * @param email The email to authenticate.
         * @param password The plain-text password.
         * @return true if authentication is successful, false otherwise.
         */
        private boolean autenticacionUsuario(String email, String password) {
            java.util.Map<String, String> users = FileManager.readUsers();
            String hashedPassword = users.get(email);
            if (hashedPassword != null) {
                try {
                    return BCrypt.checkpw(password, hashedPassword);
                } catch (IllegalArgumentException e) {
                    // Log if password hash is invalid for some reason (e.g. not a BCrypt hash)
                    logger.log(Level.WARNING, String.format("Error checking password for user %s: %s", email, e.getMessage()));
                    return false;
                }
            }
            return false;
        }

        /**
         * Extracts the session ID from the "Cookie" HTTP header.
         * @param cookieHeader The valor of the "Cookie" header.
         * @return The session ID if found, null otherwise.
         */
        private String extractSessionIdFromCookie(String cookieHeader) {
            if (cookieHeader == null || cookieHeader.isEmpty()) {
                return null;
            }
            String[] cookies = cookieHeader.split(";\\s*"); // Split by semicolon and optional whitespace
            for (String cookie : cookies) {
                if (cookie.startsWith("session_id=")) {
                    return cookie.substring("session_id=".length());
                }
            }
            return null;
        }


        /**
         * Maneja la solicitud HTTP y genera la respuesta adecuada según la ruta
         * y los parámetros.
         *
         * @param metodo El método HTTP (GET, POST).
         * @param ruta La ruta de la solicitud HTTP (ej. /adivina, /dados).
         * @param cuerpo La parte del cuerpo de la solicitud, utilizada para POST.
         * @return El HTML de la página a devolver al cliente.
         */
        public String gestionarSolicitud(String metodo, String ruta, String cuerpo) {
            String respuesta = "";
            // Extraer query parameters de la ruta para GET requests (e.g. /dados?reset=true)
            String pathPart = ruta;
            String queryPart = "";
            if (ruta.contains("?")) {
                pathPart = ruta.substring(0, ruta.indexOf('?'));
                queryPart = ruta.substring(ruta.indexOf('?') + 1);
            }


            if (pathPart.equals("/menu")) {
                respuesta = Paginas.getMenuHtml();
            } else if (pathPart.equals("/adivina")) {
                if (metodo.equals("GET")) {
                    // Reset game if requested (e.g., via link "Jugar otra vez")
                    if (queryPart.contains("reset=true")) {
                        Juego1Adivina.resetJuego();
                    }
                    // Always show initial state or reset state on GET
                    respuesta = Paginas.getAdivinarNumeroHtml("Intenta adivinar el número.", Juego1Adivina.intentos, Juego1Adivina.intentos >= Juego1Adivina.NUM_MAX_INTENTOS || (Juego1Adivina.numeroSolucion == -1)); // numeroSolucion == -1 could indicate a win
                } else if (metodo.equals("POST")) {
                    String numeroStr = extractParameter(cuerpo, "adivina");
                    respuesta = Juego1Adivina.adivinarNumero(numeroStr);
                }
            } else if (pathPart.equals("/dados")) {
                if (metodo.equals("GET")) {
                    if (queryPart.contains("reset=true")) {
                        Juego2Dados.resetJuego();
                    }
                    respuesta = Juego2Dados.estadoLanzarDados(); // Show current or initial state
                } else if (metodo.equals("POST")) {
                    respuesta = Juego2Dados.lanzarDados();
                }
            } else if (pathPart.equals("/ppt")) {
                if (metodo.equals("GET")) {
                    if (queryPart.contains("reset=true")) {
                        Juego3PPT.resetJuego();
                    }
                     // Always show initial state or reset state on GET
                    respuesta = Paginas.getPiedraPapelTijerasHtml("Elige Piedra, Papel o Tijeras.", null, null, Juego3PPT.victoriasUsuario, Juego3PPT.victoriasServidor, Juego3PPT.rondasJugadas, Juego3PPT.rondasJugadas >= Juego3PPT.TOTAL_RONDAS);
                } else if (metodo.equals("POST")) {
                    String eleccionUsuario = extractParameter(cuerpo, "eleccion");
                    respuesta = Juego3PPT.piedraPapelTijeras(eleccionUsuario);
                }
            } else if (pathPart.equals("/") || pathPart.isEmpty()) {
                // If at root path, behavior might depend on login state (handled before this method)
                // Or redirect to login/menu, currently handled in the main run() block.
                // This method will be called only if session is valid for protected routes.
                // So if user is at "/" and logged in, redirect to menu.
                // This specific case is better handled in the main routing logic in run().
                // For now, returning empty will trigger the 302 to /menu if logged in, or /login if not, from run().
                 return ""; // Let the main run() method handle redirection for root.
            }
            // else: respuesta will be empty, leading to a 404 or other handling in run()
            return respuesta;
        }

        /**
         * Extrae un parámetro del cuerpo de una solicitud POST (application/x-www-form-urlencoded).
         * @param cuerpo La cadena del cuerpo de la solicitud.
         * @param nombreParametro El nombre del parámetro a extraer.
         * @return El valor del parámetro, o null si no se encuentra.
         */
        private String extractParameter(String cuerpo, String nombreParametro) {
            if (cuerpo == null || cuerpo.isEmpty()) {
                return null;
            }
            String[] parametros = cuerpo.split("&");
            for (String param : parametros) {
                String[] pair = param.split("=", 2); // Split only on the first '='
                if (pair.length == 2 && pair[0].equals(nombreParametro)) {
                    try {
                        return java.net.URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        logger.log(Level.SEVERE, "Error decoding URL parameter: " + pair[1], e);
                        return pair[1]; // Return undecoded as a fallback, though this shouldn't happen with UTF-8
                    } catch (IllegalArgumentException e) {
                        logger.log(Level.WARNING, "Error decoding URL parameter (illegal hex characters): " + pair[1], e);
                        return null; // Or handle as appropriate
                    }
                }
            }
            return null;
        }
    }
}