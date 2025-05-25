/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package ServidorHTTPJuegosInteractivos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestiona las sesiones de usuario para la autenticación y el mantenimiento del estado.
 * Esta clase es responsable de crear, validar e invalidar los IDs de sesión,
 * asociándolos con los correos electrónicos de los usuarios autenticados.
 * 
 * @author Antonio Naranjo Castillo
 * 
 */
public class SessionManager {
    // TODO code application logic here
    
    /**
     * Mapa estático y final que almacena las sesiones activas.
     * La clave es el ID de sesión (String) y el valor es el correo electrónico del usuario (String).
     * Se utiliza ConcurrentHashMap para permitir el acceso y la modificación segura desde múltiples hilos.
     */
    private static final Map<String, String> sesionesActivas = new ConcurrentHashMap<>();

    /**
     * Crea una nueva sesión para un usuario autenticado.
     * Genera un ID de sesión único y lo asocia con el correo electrónico del usuario
     * en el mapa de sesiones activas.
     *
     * @param userEmail El correo electrónico del usuario autenticado para quien se crea la sesión.
     * @return El ID de sesión generado (String) que el servidor enviará al cliente.
     */
    public static String crearSesion(String userEmail) {
        String idSesion = UUID.randomUUID().toString(); // Genera un nuevo ID de sesión único utilizando UUID.randomUUID().
        sesionesActivas.put(idSesion, userEmail); // Almacena el ID de sesión y el correo electrónico del usuario en el mapa de sesiones activas.
        return idSesion; // Devuelve el ID de sesión generado.
    }

   /**
     * Valida un ID de sesión proporcionado por el cliente.
     * Comprueba si el ID de sesión existe en las sesiones activas y, si es así,
     * devuelve el correo electrónico del usuario asociado a esa sesión.
     *
     * @param idSesion El ID de sesión (String) a validar, recibido del cliente.
     * @return El correo electrónico (String) del usuario si la sesión es válida,
     * o {@code null} si el ID de sesión es nulo o no se encuentra en las sesiones activas.
     */
    public static String validarSesion(String idSesion) {
        // Comprueba si el ID de sesión recibido es nulo, en tal caso devuelve null.
        if (idSesion == null) {
            return null;
        }
        // Intenta obtener el correo electrónico del usuario asociado al 'idSesion' del mapa.
        // Si el 'idSesion' no existe como clave, get() devolverá null.
        return sesionesActivas.get(idSesion);
    }

    /**
     * Invalida una sesión existente, eliminándola del registro de sesiones activas.
     * Esto se utiliza típicamente cuando un usuario cierra sesión o la sesión expira.
     *
     * @param idSesion El ID de sesión (String) de la sesión a invalidar.
     */
    public static void invalidarSesion(String idSesion) {
        // Se comprueba si el ID de sesión no es nulo antes de intentar eliminarlo.
        if (idSesion != null) {
            // Se elimina la entrada correspondiente al 'idSesion' del mapa de sesiones activas.
            // Si el idSesion no existe, el método remove() no hará nada.
            sesionesActivas.remove(idSesion);
        }
    }
}