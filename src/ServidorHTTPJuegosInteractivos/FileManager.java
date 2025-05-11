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
    // TODO code application logic here (This is a default IDE comment, can be ignored or removed)

    private static final String USERS_FILE = "usuarios.txt";
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    // The AES_ENCRYPTION_KEY constant was removed as it was unused.
    // CryptoUtil.java manages its own key which is used for encryption/decryption.

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
                if (encryptedLine.trim().isEmpty()) continue; // Skip empty lines
                String decryptedLine = CryptoUtil.decrypt(encryptedLine);
                if (decryptedLine != null) {
                    String[] parts = decryptedLine.split(":", 2);
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]);
                    } else {
                        System.err.println("Malformed line in users file after decryption: " + decryptedLine);
                    }
                } else {
                     System.err.println("Failed to decrypt line from users file: " + encryptedLine);
                }
            }
        } catch (FileNotFoundException e) {
            // File might not exist yet, this is normal on first run.
            System.err.println("Users file '" + USERS_FILE + "' not found (will be created on first registration).");
        } catch (IOException e) {
            // Error during read.
            System.err.println("Error reading users file: " + e.getMessage());
        } finally {
            lock.readLock().unlock();
        }
        return users;
    }

    /**
     * Adds a new user to the encrypted file.
     * @param email The user's email.
     * @param hashedPassword The user's hashed password.
     * @return true if the user was added, false if the email already exists or an error occurred.
     */
    public static boolean addUser(String email, String hashedPassword) {
        lock.writeLock().lock();
        try {
            // It's crucial that readUsersInternal also handles decryption correctly
            // and that writeUsersInternal handles encryption.
            Map<String, String> users = readUsersInternal(); // Read current users while holding write lock
            if (users.containsKey(email)) {
                return false; // User already exists
            }

            users.put(email, hashedPassword);
            writeUsersInternal(users); // Write updated users while holding write lock
            return true;
        } catch (Exception e) { // Catch broader exceptions during add user process
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
            return false;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Internal helper to read users, assumes lock is already held.
     * @return Map of email to hashed password.
     */
    private static Map<String, String> readUsersInternal() {
        // This method is called while a write lock is held by addUser or a read lock by readUsers.
        // It should behave consistently.
        Map<String, String> users = new HashMap<>();
        File userFile = new File(USERS_FILE);
        if (!userFile.exists()) {
             System.err.println("Users file '" + USERS_FILE + "' not found during internal read (will be created).");
             return users; // Return empty map if file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(userFile))) {
            String encryptedLine;
            while ((encryptedLine = reader.readLine()) != null) {
                 if (encryptedLine.trim().isEmpty()) continue; // Skip empty lines
                String decryptedLine = CryptoUtil.decrypt(encryptedLine);
                if (decryptedLine != null) {
                    String[] parts = decryptedLine.split(":", 2);
                    if (parts.length == 2) {
                        users.put(parts[0], parts[1]);
                    } else {
                         System.err.println("Malformed line in users file after internal decryption: " + decryptedLine);
                    }
                } else {
                    System.err.println("Failed to decrypt line from users file internally: " + encryptedLine);
                }
            }
        } catch (FileNotFoundException e) {
             // Should be caught by the check above, but good to have
             System.err.println("Users file '" + USERS_FILE + "' not found during internal read.");
        } 
        catch (IOException e) {
            System.err.println("Error reading users file internally: " + e.getMessage());
        }
        return users;
    }

    /**
     * Internal helper to write users, assumes lock is already held.
     * @param users The map of users to write.
     */
    private static void writeUsersInternal(Map<String, String> users) {
        // This method is called while a write lock is held by addUser.
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, false))) { // Overwrite file
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String lineToEncrypt = entry.getKey() + ":" + entry.getValue();
                String encryptedLine = CryptoUtil.encrypt(lineToEncrypt);
                if (encryptedLine != null) {
                    writer.println(encryptedLine);
                } else {
                    System.err.println("Failed to encrypt user data for: " + entry.getKey());
                    // Decide on error handling: throw exception, log, skip?
                    // For now, it just means this user won't be written.
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing users file internally: " + e.getMessage());
            // Consider re-throwing as a runtime exception if this is critical
        }
    }
}