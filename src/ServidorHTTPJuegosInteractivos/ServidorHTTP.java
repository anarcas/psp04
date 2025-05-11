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
 *
 * @author Antonio Naranjo Castillo
 */
public class ServidorHTTP {

    /**
     * Método principal que lanza el servidor HTTP y maneja las excepciones IO.
     *
     * @param args Los argumentos de la línea de comandos.
     * @throws IOException Si ocurre un error de entrada/salida.
     */
    public static void main(String[] args) throws IOException {
        // Set system properties for the KeyStore
        // IMPORTANT: Ensure "AlmacenSSL" is a valid JKS keystore file,
        // located in the project root directory, and its password is "123456".
        // It must contain a private key entry for the server.
        // If using a different keystore type (e.g., PKCS12), set:
        // System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.keyStore", "AlmacenSSL");
        System.setProperty("javax.net.ssl.keyStorePassword", "123456"); // Hardcoded password as per requirement

        SSLServerSocket serverSocket = null;
        try {
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) ssf.createServerSocket(8066);
            System.out.println("Servidor HTTPS iniciado en el puerto 8066");
            System.out.println("Visita https://localhost:8066");
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor SSL: " + e.getMessage());
            e.printStackTrace();
            System.err.println("Asegúrese de que el fichero 'AlmacenSSL' existe en la raíz del proyecto, es un keystore válido y la contraseña es correcta.");
            System.exit(1); // Exit if server cannot start
        }

        while (true) {
            Socket cliente = serverSocket.accept();
            Thread hiloservidor = new HiloServidor(cliente);
            hiloservidor.start();
        }
    }

    /**
     * Clase interna que representa un hilo para manejar las solicitudes de un
     * cliente.
     */
    static class HiloServidor extends Thread {

        private static final Logger logger = Logger.getLogger(HiloServidor.class.getName());
        private final Socket cliente;

        // Regex for email validation
        private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
        // Regex for password validation (at least 6 alphanumeric characters)
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
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream(), StandardCharsets.UTF_8)); // Specify UTF-8
                    PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true)) {

                String primeraLineaPeticion = entrada.readLine();
                if (primeraLineaPeticion == null) {
                    return; // Client closed connection prematurely
                }
                System.out.println("Línea de Petición: " + primeraLineaPeticion);

                String[] requestParts = primeraLineaPeticion.split(" ");
                if (requestParts.length < 2) {
                     // Malformed request line
                    logger.log(Level.WARNING, "Malformed request line: " + primeraLineaPeticion);
                    // Send a basic error response
                    salida.println("HTTP/1.1 400 Bad Request");
                    salida.println("Content-Type: text/plain");
                    salida.println();
                    salida.println("Bad Request");
                    return;
                }
                String metodo = requestParts[0];
                String ruta = requestParts[1];


                int contentLength = 0;
                String cookieHeader = null;
                String linea;

                while ((linea = entrada.readLine()) != null && !linea.isEmpty()) {
                    System.out.println("Línea de Encabezado: " + linea);
                    if (linea.toLowerCase().startsWith("content-length: ")) { // Use toLowerCase for header name
                        try {
                            contentLength = Integer.parseInt(linea.substring(16).trim());
                        } catch (NumberFormatException e) {
                            logger.log(Level.SEVERE, "Error al leer Content-Length: " + e.getMessage(), e);
                        }
                    } else if (linea.toLowerCase().startsWith("cookie: ")) { // Use toLowerCase for header name
                        cookieHeader = linea.substring(8).trim();
                    }
                }
                System.out.println("Fin de encabezados.");

                StringBuilder cuerpo = new StringBuilder();
                if (metodo.equals("POST") && contentLength > 0) {
                    char[] buffer = new char[contentLength];
                    int bytesLeidos = 0;
                    // Read until contentLength bytes are read or end of stream
                    while (bytesLeidos < contentLength) {
                        int read = entrada.read(buffer, bytesLeidos, contentLength - bytesLeidos);
                        if (read == -1) break; // End of stream
                        bytesLeidos += read;
                    }
                    cuerpo.append(buffer, 0, bytesLeidos);
                }
                System.out.println("Cuerpo de la Petición: " + cuerpo.toString());

                String sessionId = extractSessionIdFromCookie(cookieHeader);
                String userEmail = SessionManager.validateSession(sessionId);

                String respuestaHtml = "";
                String setCookieHeader = null;
                String redirectLocation = null; // For HTTP redirects

                // Handle login/registration/logout
                if (ruta.equals("/login")) {
                    if (metodo.equals("GET")) {
                        respuestaHtml = Paginas.getLoginFormHtml(null);
                    } else if (metodo.equals("POST")) {
                        String[] formData = cuerpo.toString().split("&");
                        String email = null;
                        String password = null;
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

                        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                            respuestaHtml = Paginas.getLoginFormHtml("Email o contraseña no pueden estar vacíos.");
                        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                            respuestaHtml = Paginas.getLoginFormHtml("El formato del email no es válido.");
                            ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                 "Formato de email inválido en login.", email);
                        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                            respuestaHtml = Paginas.getLoginFormHtml("La contraseña debe tener al menos 6 caracteres alfanuméricos.");
                             ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                 "Formato de contraseña inválido en login.", "***********"); // Hide password
                        } else {
                            // Authenticate user
                            if (authenticateUser(email, password)) {
                                String newSessionId = SessionManager.createSession(email);
                                setCookieHeader = "session_id=" + newSessionId + "; Path=/; HttpOnly; Secure; SameSite=Lax";
                                redirectLocation = "/menu"; // Redirect to game menu
                            } else {
                                respuestaHtml = Paginas.getLoginFormHtml("Email o contraseña incorrectos.");
                            }
                        }
                    }
                } else if (ruta.equals("/register")) {
                    if (metodo.equals("GET")) {
                        respuestaHtml = Paginas.getRegisterFormHtml(null);
                    } else if (metodo.equals("POST")) {
                        String[] formData = cuerpo.toString().split("&");
                        String email = null;
                        String password = null;
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
                        
                        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                            respuestaHtml = Paginas.getRegisterFormHtml("Email o contraseña no pueden estar vacíos.");
                        } else if (!EMAIL_PATTERN.matcher(email).matches()) {
                            respuestaHtml = Paginas.getRegisterFormHtml("El formato del email no es válido.");
                            ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                 "Formato de email inválido en registro.", email);
                        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
                            respuestaHtml = Paginas.getRegisterFormHtml("La contraseña debe tener al menos 6 caracteres alfanuméricos.");
                            ErrorLogger.logError("Auth", Thread.currentThread().getStackTrace()[1].getLineNumber(),
                                 "Formato de contraseña inválido en registro.", "***********"); // Hide password
                        } else {
                            // Register user
                            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                            if (FileManager.addUser(email, hashedPassword)) {
                                String newSessionId = SessionManager.createSession(email);
                                setCookieHeader = "session_id=" + newSessionId + "; Path=/; HttpOnly; Secure; SameSite=Lax";
                                redirectLocation = "/menu"; // Redirect to game menu
                            } else {
                                respuestaHtml = Paginas.getRegisterFormHtml("El email ya está registrado.");
                            }
                        }
                    }
                } else if (ruta.equals("/logout") && metodo.equals("POST")) { // Logout should typically be POST
                    if (sessionId != null) {
                        SessionManager.invalidateSession(sessionId);
                        setCookieHeader = "session_id=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; Secure; SameSite=Lax"; // Clear cookie
                    }
                    redirectLocation = "/login"; // Redirect to login page
                } else {
                    // Protected routes: Check session before serving content
                    if (userEmail == null && !ruta.equals("/login") && !ruta.equals("/register")) { // Allow access to /login and /register without session
                        redirectLocation = "/login"; // Redirect to login if no valid session for other routes
                    } else {
                        // Handle game logic (existing functionality)
                        // Pass method, route, and body to gestionarSolicitud
                        respuestaHtml = gestionarSolicitud(metodo, ruta, cuerpo.toString());
                    }
                }

                // Send HTTP response
                if (redirectLocation != null) {
                    salida.println("HTTP/1.1 302 Found");
                    salida.println("Location: " + redirectLocation);
                    if (setCookieHeader != null) {
                        salida.println("Set-Cookie: " + setCookieHeader);
                    }
                    salida.println("Connection: close"); // Good practice for redirects
                    salida.println();
                } else {
                    if (respuestaHtml == null || respuestaHtml.isEmpty()) {
                        // Handle cases where no HTML is generated (e.g. unhandled route)
                        // Check if it's an API endpoint that shouldn't return HTML or a genuine error
                        if (userEmail != null && (ruta.equals("/") || ruta.isEmpty())) { // If logged in and at root, redirect to menu
                             salida.println("HTTP/1.1 302 Found");
                             salida.println("Location: /menu");
                             if (setCookieHeader != null) {
                                 salida.println("Set-Cookie: " + setCookieHeader);
                             }
                             salida.println("Connection: close");
                             salida.println();
                        } else if (userEmail == null && (ruta.equals("/") || ruta.isEmpty())) { // If not logged in and at root, redirect to login
                             salida.println("HTTP/1.1 302 Found");
                             salida.println("Location: /login");
                              if (setCookieHeader != null) {
                                 salida.println("Set-Cookie: " + setCookieHeader);
                             }
                             salida.println("Connection: close");
                             salida.println();
                        }
                        else { // Default 404
                            salida.println("HTTP/1.1 404 Not Found");
                            salida.println("Content-Type: text/html; charset=UTF-8");
                            salida.println("Connection: close");
                            salida.println();
                            salida.println("<html><body><h1>404 Not Found</h1><p>The requested resource was not found.</p></body></html>");
                        }
                    } else {
                        salida.println("HTTP/1.1 200 OK");
                        salida.println("Content-Type: text/html; charset=UTF-8");
                        if (setCookieHeader != null) {
                            salida.println("Set-Cookie: " + setCookieHeader);
                        }
                        salida.println("Content-Length: " + respuestaHtml.getBytes(StandardCharsets.UTF_8).length); // Use getBytes for accurate length
                        salida.println("Connection: close"); // Good practice
                        salida.println();
                        salida.println(respuestaHtml);
                    }
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error handling client request", e);
            } finally {
                try {
                    if (cliente != null && !cliente.isClosed()) {
                        cliente.close();
                    }
                } catch (IOException e) {
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
        private boolean authenticateUser(String email, String password) {
            java.util.Map<String, String> users = FileManager.readUsers();
            String hashedPassword = users.get(email);
            if (hashedPassword != null) {
                try {
                    return BCrypt.checkpw(password, hashedPassword);
                } catch (IllegalArgumentException e) {
                    // Log if password hash is invalid for some reason (e.g. not a BCrypt hash)
                    logger.log(Level.WARNING, "Error checking password for user " + email + ": " + e.getMessage());
                    return false;
                }
            }
            return false;
        }

        /**
         * Extracts the session ID from the "Cookie" HTTP header.
         * @param cookieHeader The value of the "Cookie" header.
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