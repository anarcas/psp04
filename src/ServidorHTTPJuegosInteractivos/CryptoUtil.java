/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package ServidorHTTPJuegosInteractivos;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Clase de utilidad para el cifrado y descifrado de cadenas de texto utilizando el algoritmo AES.
 * Proporciona métodos para proteger información sensible mediante cifrado simétrico.
 * 
 * @author Antonio Naranjo Castillo
 * 
 */
public class CryptoUtil {
    // TODO code application logic here
    
    private static SecretKeySpec secretKey; // Clave secreta AES utilizada para las operaciones de cifrado y descifrado.

    // Inicializar 'key' llamando a un método estático privado.
    private static final byte[] key = initializeKey();

    private static final String AES_KEY_STRING = "EsteEsMiSuperSecretoParaAES"; // Cadena base a partir de la cual se deriva la clave AES.

    private static final Logger logger = Logger.getLogger(CryptoUtil.class.getName()); // Obtener una instancia del Logger para esta clase.

    /**
     * Bloque estático de inicialización que se ejecuta una vez cuando la clase `CryptoUtil` es cargada.
     * Este bloque se encarga de derivar y preparar la clave AES a partir de `AES_KEY_STRING`
     * utilizando SHA-1 y ajustando su tamaño a 128 bits (16 bytes), que es el tamaño requerido para AES.
     *
     * Este bloque ahora solo inicializa 'secretKey', ya que 'key' se inicializa en su declaración.
     */
    static {
        
        try { // Intenta crear la clave secreta AES a partir de los bytes 'key' (ya derivados y validados).
            secretKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) { // Captura cualquier otra excepción que pueda ocurrir al crear SecretKeySpec
            // Captura cualquier excepción que ocurra durante la creación de SecretKeySpec.
             logger.log(Level.SEVERE, String.format("Error al crear SecretKeySpec: %s", e.getMessage()), e);
            throw new RuntimeException("Fallo crítico: No se pudo inicializar la clave AES.", e);
        }
    }

    /**
     * Método auxiliar privado para inicializar y derivar la clave AES.
     * Se llama una única vez durante la inicialización de la clase para asignar el valor final a 'key'.
     * @return El array de bytes que representa la clave AES de 128 bits.
     * @throws RuntimeException Si ocurre un error irrecuperable durante la derivación de la clave.
     */
    private static byte[] initializeKey() {
        byte[] rawKey; // Variable temporal para almacenar los bytes de la clave durante el procesamiento.
        try {
            // Convierte la cadena secreta a bytes (UTF-8).
            rawKey = AES_KEY_STRING.getBytes(StandardCharsets.UTF_8);
            // Obtiene una instancia del algoritmo de hash SHA-1.
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            // Calcula el hash SHA-1 de los bytes de la clave.
            rawKey = sha.digest(rawKey);
            // Trunca o rellena el hash resultante a 16 bytes (128 bits), tamaño requerido para AES.
            return Arrays.copyOf(rawKey, 16); // Retorna el valor final para 'key'.
        } catch (NoSuchAlgorithmException e) {
            // Si el algoritmo SHA-1 no está disponible, se registra un error grave y se lanza una excepción.
            logger.log(Level.SEVERE, String.format("Error durante la inicialización de la clave AES (NoSuchAlgorithmException): %s", e.getMessage()), e);
            throw new RuntimeException("Fallo crítico: Algoritmo de hash no disponible para inicializar la clave.", e);
        } catch (Exception e) { // Captura cualquier otra excepción inesperada
            // Captura cualquier otra excepción inesperada durante la derivación de la clave, registra y relanza.
            logger.log(Level.SEVERE, String.format("Error inesperado durante la inicialización de la clave AES: %s", e.getMessage()), e);
            throw new RuntimeException("Fallo crítico: Error inesperado al inicializar la clave.", e);
        }
    }
    
    /**
     * Cifra una cadena de texto utilizando el algoritmo AES en modo ECB con padding PKCS5.
     * La cadena cifrada resultante se codifica en Base64 para facilitar su transmisión o almacenamiento.
     *
     * @param strToEncrypt La cadena de texto que se desea cifrar.
     * @return La cadena cifrada, codificada en Base64, o `null` si ocurre un error durante el cifrado.
     */
    public static String encrypt(String strToEncrypt) {
        try {
            // Obtiene una instancia del objeto Cipher para AES en modo ECB con PKCS5Padding.
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // Inicializa el cifrador en modo ENCRYPT_MODE con la clave secreta.
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            // Cifra la cadena de entrada y la codifica en Base64 antes de retornarla.
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            // Imprime un mensaje de error si el cifrado falla.
            logger.log(Level.SEVERE, String.format("Error durante al encriptación: ", e.toString()));
        }
        return null;
    }

    /**
     * Descifra una cadena de texto que ha sido cifrada con AES y codificada en Base64.
     *
     * @param strToDecrypt La cadena codificada en Base64 que se desea descifrar.
     * @return La cadena de texto descifrada, o `null` si ocurre un error durante el descifrado.
     */
    public static String decrypt(String strToDecrypt) {
        try {
            // Obtiene una instancia del objeto Cipher para AES en modo ECB con PKCS5Padding.
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // Inicializa el cifrador en modo DECRYPT_MODE con la clave secreta.
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            // Decodifica la cadena Base64 y luego descifra los bytes.
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException e) {
            // Imprime un mensaje de error si el descifrado falla.
            logger.log(Level.SEVERE, String.format("Error durante al encriptación: ", e.toString()));
        }
        return null;
    }
}