/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package ServidorHTTPJuegosInteractivos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que gestiona el almacenamiento de credenciales de usuario en el archivo
 * `usuarios.txt`, incluyendo cifrado AES y control de concurrencia.
 * 
 * @author Antonio Naranjo Castillo
 * 
 */
public class FileManager {
    // TODO code application logic here (This is a default IDE comment, can be ignored or removed)

    // Obtener una instancia del Logger para esta clase
    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    private static final String ARCHIVO_USUARIOS = "usuarios.txt"; // Nombre del archivo donde se almacenan los datos de los usuarios.
    /**
     * Objeto de bloqueo de lectura/escritura para controlar el acceso
     * concurrente al archivo de usuarios. Permite múltiples lectores
     * simultáneamente, pero solo un escritor a la vez, garantizando la
     * integridad de los datos.
     */
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    // La constante AES_ENCRYPTION_KEY fue eliminada ya que no se utilizaba.
    // CryptoUtil.java gestiona su propia clave, que es utilizada para el cifrado/descifrado.

    /**
     * Lee todos los usuarios del archivo cifrado. Utiliza un bloqueo de lectura
     * para permitir que múltiples hilos lean simultáneamente, pero bloquea las
     * operaciones de escritura.
     *
     * @return Un mapa donde la clave es el correo electrónico del usuario y el
     * valor es la contraseña hash.
     */
    public static Map<String, String> lecturaUsuarios() {
        lock.readLock().lock();
        Map<String, String> usuarios = new HashMap<>();
        try (BufferedReader lector = new BufferedReader(new FileReader(ARCHIVO_USUARIOS))) {
            String lineaEncriptada;
            // Lee el archivo línea por línea
            while ((lineaEncriptada = lector.readLine()) != null) {
                if (lineaEncriptada.trim().isEmpty()) {
                    continue; // Skip empty lines
                }                // Descifra la línea leída del archivo.
                String lineaDesencriptada = CryptoUtil.decrypt(lineaEncriptada);
                if (lineaDesencriptada != null) {
                    // Divide la línea descifrada en dos partes: correo electrónico y contraseña hash.
                    String[] parts = lineaDesencriptada.split(":", 2);
                    if (parts.length == 2) {
                        usuarios.put(parts[0], parts[1]);
                    } else {
                        logger.log(Level.WARNING, String.format("Línea mal formada en el archivo de usuarios después del descifrado: ", lineaDesencriptada));
                    }
                } else {
                    logger.log(Level.WARNING, String.format("No se pudo descifrar la línea del archivo de usuarios: ", lineaEncriptada));
                }
            }
        } catch (FileNotFoundException e) {
            // Este es un caso normal en la primera ejecución, ya que el archivo puede no existir.
            logger.log(Level.SEVERE, String.format("Archivo de usuario %s no encontrado (durante el primer registro).", ARCHIVO_USUARIOS));

        } catch (IOException e) {
            // Captura errores durante la lectura del archivo.
            logger.log(Level.SEVERE, String.format("Error leyendo el archivo de usuario: ", e.getMessage()));
        } finally {
            lock.readLock().unlock();
        }
        return usuarios;
    }

    /**
     * Añade un nuevo usuario al archivo cifrado. Utiliza un bloqueo de
     * escritura para garantizar que solo un hilo pueda modificar el archivo a
     * la vez, evitando condiciones de carrera.
     *
     * @param email El correo electrónico del usuario a añadir.
     * @param hashedPassword La contraseña hash del usuario.
     * @return `true` si el usuario fue añadido exitosamente, `false` si el
     * correo electrónico ya existe o si ocurrió un error durante el proceso.
     */
    public static boolean addUser(String email, String hashedPassword) {
        lock.writeLock().lock();
        try {
            // Es crucial que 'readUsersInternal' también maneje el descifrado correctamente y que 'writeUsersInternal' maneje el cifrado.
            // Lee los usuarios actuales mientras se mantiene el bloqueo de escritura
            Map<String, String> usuarios = readUsersInternal();
            if (usuarios.containsKey(email)) {
                return false;
            }

            usuarios.put(email, hashedPassword); // Añade el nuevo usuario al mapa
            writeUsersInternal(usuarios); // Escribe el mapa de usuarios actualizado en el archivo mientras se mantiene el bloqueo de escritura.
            return true; // El usuario fue añadido exitosamente.
        } catch (Exception e) {
            logger.log(Level.SEVERE, String.format("Error añadiendo usuario: ", e.getMessage()));
            return false;
        } finally {
            lock.writeLock().unlock(); // Libera el bloqueo de escritura
        }
    }

    /**
     * Método auxiliar interno para leer usuarios. Se asume que el bloqueo (de
     * lectura o escritura) ya ha sido adquirido por el método llamador.
     *
     * @return Un mapa donde la clave es el correo electrónico y el valor es la
     * contraseña hash.
     */
    private static Map<String, String> readUsersInternal() {
        // Este método es llamado mientras un bloqueo de escritura es mantenido por 'addUser' o un bloqueo de lectura por 'lecturaUsuarios'. Debe comportarse de manera consistente.
        Map<String, String> users = new HashMap<>();
        File userFile = new File(ARCHIVO_USUARIOS);
        // Verifica si el archivo de usuarios existe
        if (!userFile.exists()) {
            logger.log(Level.WARNING, String.format("Archivo de usuario %s no encontrado (durante el proceso de lectura interna, pero será creado).", ARCHIVO_USUARIOS));
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String encryptedLine;
            while ((encryptedLine = reader.readLine()) != null) {
                if (encryptedLine.trim().isEmpty()) {
                    continue;
                }
                String decryptedLine = CryptoUtil.decrypt(encryptedLine);
                if (decryptedLine != null) {
                    String[] parts = decryptedLine.split(":", 2);
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]);
                    } else {
                        logger.log(Level.WARNING, String.format("Línea mal formada después del cifrado: ", decryptedLine));
                    }
                } else {
                    logger.log(Level.WARNING, String.format("La encriptación ha fallado en el archivo ususarios: ", encryptedLine));
                }
            }
        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, String.format("Archivo de usuario %s no encontrado (durante el proceso de lectura interna).", ARCHIVO_USUARIOS));
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Error leyendo el archivo de usuarios: ", e.getMessage()));
        }
        return users;
    }

    /**
     * Método auxiliar interno para escribir usuarios. Se asume que el bloqueo
     * de escritura ya ha sido adquirido por el método llamador (ej. `addUser`).
     *
     * @param users El mapa de usuarios a escribir en el archivo.
     */
    private static void writeUsersInternal(Map<String, String> users) {
        // Este método es llamado mientras un bloqueo de escritura es mantenido por 'addUser'.
        try (PrintWriter writer = new PrintWriter(new FileWriter(ARCHIVO_USUARIOS, false))) {
            // Itera sobre cada entrada del mapa de usuarios.
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String lineToEncrypt = entry.getKey() + ":" + entry.getValue();
                String encryptedLine = CryptoUtil.encrypt(lineToEncrypt);
                if (encryptedLine != null) {
                    writer.println(encryptedLine);
                } else {
                    logger.log(Level.WARNING, String.format("No se pudieron cifrar los datos de usuario para: ", entry.getKey()));

                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, String.format("Error escribiendo el archivo de usuario: ", e.getMessage()));
        }
    }
}
