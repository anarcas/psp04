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
//import java.util.regex.Matcher;
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
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                    PrintWriter salida = new PrintWriter(cliente.getOutputStream(), true)) {

                String primeraLineaPeticion = entrada.readLine();
                if (primeraLineaPeticion == null) {
                    return; // Client closed connection prematurely
                }
                System.out.println("Línea de Petición: " + primeraLineaPeticion);

                String metodo = primeraLineaPeticion.split(" ")[0];
                String ruta = primeraLineaPeticion.split(" ")[1];

                int contentLength = 0;
                String cookieHeader = null;
                String linea;

                while ((linea = entrada.readLine()) != null && !linea.isEmpty()) {
                    System.out.println("Línea de Encabezado: " + linea);
                    if (linea.startsWith("Content-Length: ")) {
                        try {
                            contentLength = Integer.parseInt(linea.substring(16).trim());
                        } catch (NumberFormatException e) {
                            logger.log(Level.SEVERE, "Error al leer Content-Length: " + e.getMessage(), e);
                        }
                    } else if (linea.startsWith("Cookie: ")) {
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
                            if (param.startsWith("email=")) {
                                email = java.net.URLDecoder.decode(param.substring(6), StandardCharsets.UTF_8.name());
                            } else if (param.startsWith("password=")) {
                                password = java.net.URLDecoder.decode(param.substring(9), StandardCharsets.UTF_8.name());
                            }
                        }

                        if (email == null || password == null) {
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
                            if (param.startsWith("email=")) {
                                email = java.net.URLDecoder.decode(param.substring(6), StandardCharsets.UTF_8.name());
                            } else if (param.startsWith("password=")) {
                                password = java.net.URLDecoder.decode(param.substring(9), StandardCharsets.UTF_8.name());
                            }
                        }

                        if (email == null || password == null) {
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
                } else if (ruta.equals("/logout")) {
                    if (sessionId != null) {
                        SessionManager.invalidateSession(sessionId);
                        setCookieHeader = "session_id=; Path=/; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly; Secure; SameSite=Lax"; // Clear cookie
                    }
                    redirectLocation = "/login"; // Redirect to login page
                } else {
                    // Protected routes: Check session before serving content
                    if (userEmail == null) {
                        redirectLocation = "/login"; // Redirect to login if no valid session
                    } else {
                        // Handle game logic (existing functionality)
                        respuestaHtml = gestionarSolicitud(ruta, cuerpo.toString()); // Call original gestionarSolicitud
                    }
                }

                // Send HTTP response
                if (redirectLocation != null) {
                    salida.println("HTTP/1.1 302 Found");
                    salida.println("Location: " + redirectLocation);
                    if (setCookieHeader != null) {
                        salida.println("Set-Cookie: " + setCookieHeader);
                    }
                    salida.println();
                } else {
                    salida.println("HTTP/1.1 200 OK");
                    salida.println("Content-Type: text/html; charset=UTF-8");
                    if (setCookieHeader != null) {
                        salida.println("Set-Cookie: " + setCookieHeader);
                    }
                    salida.println("Content-Length: " + respuestaHtml.length());
                    salida.println();
                    salida.println(respuestaHtml);
                }

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error handling client request", e);
            }
        }

        /**
         * Authenticates a user against stored credentials.
         * @param email The email to authenticate.
         * @param password The plain-text password.
         * @return true if authentication is successful, false otherwise.
         */
        private boolean authenticateUser(String email, String password) {
            // In a real application, you would load user data from a database
            // For this exercise, we load from the encrypted file
            java.util.Map<String, String> users = FileManager.readUsers();
            String hashedPassword = users.get(email);
            if (hashedPassword != null) {
                return BCrypt.checkpw(password, hashedPassword);
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
            String[] cookies = cookieHeader.split("; ");
            for (String cookie : cookies) {
                if (cookie.startsWith("session_id=")) {
                    return cookie.substring("session_id=".length());
                }
            }
            return null;
        }


        /**
         * Maneja la solicitud HTTP y genera la respuesta adecuada según la ruta
         * y los parámetros. (This is the original logic from previous task)
         *
         * @param peticionLa ruta de la solicitud HTTP (ej. /adivina, /dados).
         * @param cuerpo La parte del cuerpo de la solicitud, utilizada para POST.
         * @return El HTML de la página a devolver al cliente.
         */
        public String gestionarSolicitud(String peticion, String cuerpo) {
            String respuesta = "";

            if (peticion.contains("/menu")) {
                respuesta = Paginas.getMenuHtml();
            } else if (peticion.contains("/adivina")) {
                if (peticion.startsWith("GET")) {
                    // Reset game if requested (e.g., via link "Jugar otra vez")
                    if (peticion.contains("reset=true")) {
                        Juego1Adivina.resetJuego();
                    }
                    respuesta = Paginas.getAdivinarNumeroHtml("Intenta adivinar el número.", 0, false);
                } else if (peticion.startsWith("POST")) {
                    String numeroStr = extractParameter(cuerpo, "adivina");
                    respuesta = Juego1Adivina.adivinarNumero(numeroStr);
                }
            } else if (peticion.contains("/dados")) {
                if (peticion.startsWith("GET")) {
                    // Reset game if requested
                    if (peticion.contains("reset=true")) {
                        Juego2Dados.resetJuego();
                    }
                    respuesta = Juego2Dados.estadoLanzarDados();
                } else if (peticion.startsWith("POST")) {
                    respuesta = Juego2Dados.lanzarDados();
                }
            } else if (peticion.contains("/ppt")) {
                if (peticion.startsWith("GET")) {
                     // Reset game if requested
                    if (peticion.contains("reset=true")) {
                        Juego3PPT.resetJuego();
                    }
                    respuesta = Paginas.getPiedraPapelTijerasHtml("Elige Piedra, Papel o Tijeras.", null, null, 0, 0, 0, false);
                } else if (peticion.startsWith("POST")) {
                    String eleccionUsuario = extractParameter(cuerpo, "eleccion");
                    respuesta = Juego3PPT.piedraPapelTijeras(eleccionUsuario);
                }
            } else {
                // Default or fallback page
                respuesta = Paginas.getLoginFormHtml(null); // Default to login page
            }
            return respuesta;
        }

        /**
         * Extrae un parámetro del cuerpo de una solicitud POST.
         * @param cuerpo La cadena del cuerpo de la solicitud.
         * @param nombreParametro El nombre del parámetro a extraer.
         * @return El valor del parámetro, o null si no se encuentra.
         */
        private String extractParameter(String cuerpo, String nombreParametro) {
            String[] parametros = cuerpo.split("&");
            for (String param : parametros) {
                if (param.startsWith(nombreParametro + "=")) {
                    try {
                        return java.net.URLDecoder.decode(param.substring(param.indexOf('=') + 1), StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        logger.log(Level.SEVERE, "Error decoding URL parameter: " + e.getMessage(), e);
                    }
                }
            }
            return null;
        }
    }
}