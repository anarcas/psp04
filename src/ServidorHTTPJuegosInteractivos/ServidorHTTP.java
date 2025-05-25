/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

// Se importan los paquetes necesarios
import org.mindrot.jbcrypt.BCrypt;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
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
 * 
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
            // Logear el error crítico al iniciar el servidor usando el logger
            logger.log(Level.SEVERE, String.format("Error crítico al iniciar el servidor SLL: %s", e.getMessage()), e);
            // Si el servidor no puede iniciar, se cierra el programa finaliando sin exito de conexión.
            System.exit(1);
        } finally {
            // Se cierra el servidor
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                    logger.log(Level.INFO, "Servidor cerrado.");
                } catch (IOException e) {
                    logger.log(Level.SEVERE, String.format("Error al cerrar el ServerSocket: %s", e.getMessage()), e);
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
         * Método run que se ejecuta cuando se inicia el hilo. Maneja la
         * solicitud HTTP y genera una respuesta.
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
                        logger.log(Level.WARNING, "Petición HTTP nula, cierre prematuro del servidor.");
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
                        salida.println("HTTP/1.1 400 Solicitud incorrecta%nContent-Type: text/plain%n%nSolicitud incorrecta");
                    } else {
                        
                        // Se declaran las variables, y se inician las necesarias.
                        String metodo = partesSolicitudHTTP[0]; // Se recoge el método HTTP de la solicitud recibida del cliente.
                        String ruta = partesSolicitudHTTP[1]; // Se almacena la ruta (path) del recurso que el cliente está solicitando.
                        int contentLength = 0; // Longitud del cuerpo de la solicitud HTTP en bytes.
                        String cookieHeader = null; // Valor de la cabecera Cookie de la solicitud HTTP
                        String linea; // Variable auxiliar que se utiliza para leer cada línea individualmente del flujo de entrada de la solicitud HTTP.

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
                                    logger.log(Level.SEVERE, String.format("Error al leer Content-Length: %s",  e.getMessage()), e);
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
                        String idSesion = extraerSessionIdDeCookie(cookieHeader);
                        // Valida el ID de sesión para obtener el email del usuario; null si la sesión no es válida.
                        String emailUsuario = SessionManager.validarSesion(idSesion);
                        
                        // Inicialización de variables para la respuesta del servidor
                        String respuestaHtml = ""; // Almacenará el contenido HTML a enviar al cliente.
                        String establecerCookieCabecera = null; // Se usará para enviar una nueva cookie.
                        String ubicacionRedireccion = null; // Para indicar una redirección HTTP a otra URL.

                        // --- Bloque principal para manejar las diferentes rutas de autenticación y juegos ---
                        // Maneja las rutas relacionadas con el login, registro y logout.
                        if (ruta.equals("/login")) {
                            if (metodo.equals("GET")) {
                                // Si es una petición GET a /login, devuelve el formulario de inicio de sesión.
                                respuestaHtml = Paginas.getLoginAndRegisterFormsHtml(null,null);
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
                                        String newSessionId = SessionManager.crearSesion(email);
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
                                respuestaHtml = Paginas.getLoginAndRegisterFormsHtml(null,null);
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
                                        String newSessionId = SessionManager.crearSesion(email);
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
                                SessionManager.invalidarSesion(idSesion);
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
         * Método que autentica a un usuario verificando su email y contraseña.
         * Este método recupera la contraseña hasheada del usuario desde un
         * archivo y la compara de forma segura con la contraseña en texto plano
         * proporcionada, utilizando el algoritmo BCrypt.
         *
         * @param email El correo electrónico del usuario que intenta iniciar
         * sesión.
         * @param password La contraseña en texto plano proporcionada por el
         * usuario.
         * @return {@code true} si la autenticación es exitosa (el email existe
         * y la contraseña coincide), {@code false} en caso contrario (el email
         * no existe, la contraseña no coincide, o el hash almacenado es
         * inválido).
         */
        private boolean autenticacionUsuario(String email, String password) {
            /*
             * Se leen los usuarios almacenados: Llama a un método estático
             * de la clase FileManager para obtener un mapa con los emails de
             * los usuarios como claves y sus contraseñas hasheadas como
             * valores.
             */
            Map<String, String> users = FileManager.lecturaUsuarios();
            // Se obtiene la contraseña hasheada del usuario.
            String hashedPassword = users.get(email);
            // Se verifica si el usuario existe.
            if (hashedPassword != null) {
                // Si el usuario existe se intenta verificar su contraseña.
                try {
                    return BCrypt.checkpw(password, hashedPassword);
                } catch (IllegalArgumentException e) {
                    // Manejo de excepciones (hash inválido) Si BCrypt lanza una IllegalArgumentException, significa que el 'hashedPassword' almacenado no es un hash BCrypt válido y se registra el error.
                    logger.log(Level.WARNING, String.format("Error checking password for user %s: %s", email, e.getMessage()));
                    return false;
                }
            }
            // Autenticación fallida si el email no se encontró en el mapa de usuario.
            return false;
        }

        /**
         * Extrae el ID de sesión del encabezado HTTP "Cookie". Este método
         * busca específicamente una cookie con el nombre "session_id" dentro
         * del encabezado "Cookie" proporcionado y devuelve su valor.
         *
         * @param cookieHeader El valor completo del encabezado "Cookie"
         * recibido en la solicitud HTTP.
         * @return El ID de sesión si se encuentra una cookie "session_id", o
         * {@code null} si el encabezado es nulo/vacío o no contiene la cookie
         * "session_id".
         */
        private String extraerSessionIdDeCookie(String cookieHeader) {
            // Validación inicial del encabezado, si el encabezado de la cookie es nulo o está vacío, no hay nada que procesar.
            if (cookieHeader == null || cookieHeader.isEmpty()) {
                return null;
            }
            // Se divide el encabezado en cookies individuales deliminadas por punto y coma (;), seguidas de espacios en blanco opcionalmente.
            String[] cookies = cookieHeader.split(";\\s*");
            // Se intera sobre cada cookie de la lista anterior y se devuelve la cadena de caracteres siguiente a cada 'session_id='.
            for (String cookie : cookies) {
                if (cookie.startsWith("session_id=")) {
                    return cookie.substring("session_id=".length());
                }
            }
            return null; // Se devuelve null si la ID de la sesión no ha sido encontrada.
        }


        /**
         * Método que maneja la solicitud HTTP y genera la respuesta adecuada
         * según la ruta y los parámetros. Este método actúa como un enrutador
         * principal para los diferentes juegos interactivos y sus estados.
         *
         * @param metodo El método HTTP (GET, POST).
         * @param ruta La ruta de la solicitud HTTP /adivina, /dados, /ppt
         * @param cuerpo La parte del cuerpo de la solicitud, utilizada
         * principalmente para solicitudes POST que envían datos de formulario.
         * @return El HTML de la página a devolver al cliente, o una cadena
         * vacía si la redirección es manejada por el contexto externo (run()).
         */
        public String gestionarSolicitud(String metodo, String ruta, String cuerpo) {
            // Variable auxiliar para almacenar el HTML de la respuesta final.
            String respuesta = "";
            // --- Extracción de la ruta base y parámetros de consulta ---
            String partePath = ruta; // Inicializa la parte de la ruta sin parámetros de consulta.
            String parteQuery = ""; // Inicializa la parte de los parámetros de consulta.
            if (ruta.contains("?")) {
                // Si la ruta contiene un signo de interrogación, significa que hay parámetros de consulta.
                // Se divide la ruta en dos partes: la ruta base y los parámetros.
                partePath = ruta.substring(0, ruta.indexOf('?'));
                parteQuery = ruta.substring(ruta.indexOf('?') + 1);
            }

            // --- Lógica de enrutamiento por ruta y método HTTP ---
            if (partePath.equals("/menu")) { // Si la ruta es "/menu", se genera la página del menú principal.
                respuesta = Paginas.getMenuHtml();
            } else if (partePath.equals("/adivina")) { // Si la ruta es "/adivina" (juego "Adivina el Número").
                if (metodo.equals("GET")) { // Si el método es GET (solicitud de página o reinicio).
                    if (parteQuery.contains("reset=true")) { // Si los parámetros de consulta contienen "reset=true", se reinicia el juego.
                        Juego1Adivina.resetJuego();
                    }
                    // Se obtiene el HTML de la página del juego "Adivina el Número", mostrando el estado inicial o el estado de reinicio.
                    respuesta = Paginas.getAdivinarNumeroHtml("Intenta adivinar el número.", Juego1Adivina.intentos, Juego1Adivina.intentos >= Juego1Adivina.NUM_MAX_INTENTOS || (Juego1Adivina.numeroSolucion == -1));
                } else if (metodo.equals("POST")) { // Si el método es POST (el usuario envió una suposición), se extrae el número propuesto del cuerpo de la solicitud POST.
                    String numeroStr = extraeParametroCuerpo(cuerpo, "adivina");
                    // Se llama a la lógica del juego para procesar el número y obtener la respuesta HTML.
                    respuesta = Juego1Adivina.adivinarNumero(numeroStr);
                }
            } else if (partePath.equals("/dados")) { // Si la ruta es "/dados" (juego "Lanza Dados").
                if (metodo.equals("GET")) { // Si el método es GET (solicitud de página o reinicio).
                    boolean isResetRequest = false;
                    if (parteQuery != null && parteQuery.contains("reset=true")) {
                        // Si se pide reiniciar el juego, se reinicia el estado de "Lanza Dados".
                        isResetRequest = true;
                    }
                    if (isResetRequest || (Juego2Dados.rondasJugadas >= Juego2Dados.TOTAL_RONDAS && Juego2Dados.rondasJugadas > 0)) {
                        Juego2Dados.resetJuego(); // Reinicia el estado del juego de dados.
                    }
                    // Se obtiene el HTML de la página del juego "Lanza Dados", mostrando el estado actual o inicial.
                    respuesta = Juego2Dados.estadoLanzarDados();
                } else if (metodo.equals("POST")) { // Si el método es POST (el usuario pulsa el botón para lanzar los dados).
                    // Se llama a la lógica del juego para lanzar los dados y obtener la respuesta HTML.
                    respuesta = Juego2Dados.lanzarDados();
                }
            } else if (partePath.equals("/ppt")) { // Si la ruta es "/ppt" (juego "Piedra, Papel o Tijeras").
                if (metodo.equals("GET")) { // Si el método es GET (solicitud de página o reinicio).
                    Juego3PPT.resetJuego();
                    respuesta = Juego3PPT.estadoPPT();

                } else if (metodo.equals("POST")) { // Si el método es POST (el usuario hace una elección), se extrae la elección del usuario del cuerpo de la solicitud POST.
                    String eleccionUsuario = extraeParametroCuerpo(cuerpo, "eleccion");
                    // Se llama a la lógica del juego para procesar la elección y obtener la respuesta HTML.
                    respuesta = Juego3PPT.piedraPapelTijeras(eleccionUsuario);
                }
            } else if (partePath.equals("/") || partePath.isEmpty()) {
                /*
                 * Si la ruta es la raíz ("/") o está vacía, el método devuelve
                 * una cadena vacía en este caso, dejando que la lógica
                 * principal en el método 'run()' del servidor maneje la
                 * redirección basada en el estado de la sesión
                 */
                return "";
            }
            // Si la ruta no coincide con ninguna de las rutas de juego o el menú, la variable 'respuesta' permanecerá vacía, llegando a una respuesta 404 Not Found manejada en el método 'run()' principal.
            return respuesta;
        }

        /**
         * Método que extrae el valor de un parámetro específico del cuerpo de una
         * solicitud HTTP POST. Este método está diseñado para cuerpos de tipo
         * `application/x-www-form-urlencoded`, donde los parámetros están
         * codificados como `nombre=valor&nombre2=valor2`. También se encarga de
         * decodificar el valor del parámetro usando URLDecoder.
         *
         * @param cuerpo La cadena completa del cuerpo de la solicitud HTTP
         * @param nombreParametro El nombre del parámetro cuyo valor se desea
         * extraer
         * @return El valor decodificado del parámetro especificado si se
         * encuentra, o {@code null} si el cuerpo es nulo/vacío, el parámetro no
         * existe o hay un error de decodificación.
         */
        private String extraeParametroCuerpo(String cuerpo, String nombreParametro) {
            // Se Verifica si el cuerpo de la solicitud es nulo o está vacío, no hay parámetros que extraer.
            if (cuerpo == null || cuerpo.isEmpty()) {
                return null;
            }
            // Se divide el cuerpo en pares de parámetros, se separa el cuerpo de la solicitud en un array de cadenas, usando '&' como delimitador.
            // Cada elemento del array será un par "nombre=valor"
            String[] parametros = cuerpo.split("&");
            // Se itera sobre cada par de parámetro, se recorre cada cadena en el array 'parametros'.
            for (String parametro : parametros) {
                // Se separa el nombre y valor del parámetro dividiendo cada par "nombre=valor" en dos partes usando '=' como delimitador.
                // El límite '2' asegura que solo se divida en el primer '=', útil si el valor contiene '='.
                String[] pair = parametro.split("=", 2);
                // Se comprueba si el par es válido y si coincide el nombre, verificando que el par tenga dos partes (nombre y valor) y que el nombre coincida con el buscado.
                if (pair.length == 2 && pair[0].equals(nombreParametro)) {
                    // Se intenta decodificar el valor del parámetro
                    try {
                        // Utiliza URLDecoder para decodificar el valor del parámetro y se especifica UTF-8 como codificación estándar.
                        return URLDecoder.decode(pair[1], StandardCharsets.UTF_8.name());
                    } catch (UnsupportedEncodingException e) {
                        // Se manejan los errores de codificación no soportada.
                        logger.log(Level.SEVERE, String.format("Error decodificando el parámetro de la URL (codificación UTF-8): %s",pair[1]), e);
                        return pair[1];
                    } catch (IllegalArgumentException e) {
                        // Se manejan los errores de caracteres ilegales.
                        logger.log(Level.WARNING, String.format("Error decodificando el parámetro de la URL (caracteres ilegale): %s", pair[1]), e);
                        return null;
                    }
                }
            }
            // Se devuelve null cuando el parámetro no ha sido encontrado en el cuerpo de la solicitud.
            return null;
        }
    }
}