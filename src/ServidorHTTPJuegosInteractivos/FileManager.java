/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package ServidorHTTPJuegosInteractivos;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Manages user credentials storage in usuarios.txt with AES encryption and concurrency control.
 */
public class FileManager {
    // TODO code application logic here

    private static final String USERS_FILE = "usuarios.txt";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final String AES_ENCRYPTION_KEY = "your_aes_encryption_key_here"; // Make sure this matches CryptoUtil's key

    /**
     * Reads all users from the encrypted file.
     * @return A map of email to hashed password.
     */
    public static Map<String, String> readUsers() {
        lock.readLock().lock();
        Map<String, String> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String encryptedLine;
            while ((encryptedLine = reader.readLine()) != null) {
                String decryptedLine = CryptoUtil.decrypt(encryptedLine);
                if (decryptedLine != null) {
                    String[] parts = decryptedLine.split(":", 2);
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            // File might not exist yet, or error during read. Return empty map.
            System.err.println("Error reading users file (may not exist yet): " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return users;
    }

    /**
     * Adds a new user to the encrypted file.
     * @param email The user's email.
     * @param hashedPassword The user's hashed password.
     * @return true if the user was added, false if the email already exists.
     */
    public static boolean addUser(String email, String hashedPassword) {
        lock.writeLock().lock();
        try {
            Map<String, String> users = readUsersInternal(); // Read current users while holding write lock
            if (users.containsKey(email)) {
                return false; // User already exists
            }

            users.put(email, hashedPassword);
            writeUsersInternal(users); // Write updated users while holding write lock
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Internal helper to read users, assumes lock is already held.
     * @return Map of email to hashed password.
     */
    private static Map<String, String> readUsersInternal() {
        Map<String, String> users = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String encryptedLine;
            while ((encryptedLine = reader.readLine()) != null) {
                String decryptedLine = CryptoUtil.decrypt(encryptedLine);
                if (decryptedLine != null) {
                    String[] parts = decryptedLine.split(":", 2);
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file internally: " + e.getMessage());
        }
        return users;
    }

    /**
     * Internal helper to write users, assumes lock is already held.
     * @param users The map of users to write.
     */
    private static void writeUsersInternal(Map<String, String> users) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, false))) { // Overwrite file
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String lineToEncrypt = entry.getKey() + ":" + entry.getValue();
                String encryptedLine = CryptoUtil.encrypt(lineToEncrypt);
                if (encryptedLine != null) {
                    writer.println(encryptedLine);
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing users file internally: " + e.getMessage());
        }
    }
}