/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

package ServidorHTTPJuegosInteractivos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages user sessions for authentication.
 */
public class SessionManager {
    // TODO code application logic here

    // Maps session ID to user email
    private static final Map<String, String> activeSessions = new ConcurrentHashMap<>();

    /**
     * Creates a new session for a user.
     * @param userEmail The email of the authenticated user.
     * @return The generated session ID.
     */
    public static String createSession(String userEmail) {
        String sessionId = UUID.randomUUID().toString();
        activeSessions.put(sessionId, userEmail);
        return sessionId;
    }

    /**
     * Validates a given session ID.
     * @param sessionId The session ID to validate.
     * @return The user email if the session is valid, null otherwise.
     */
    public static String validateSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    /**
     * Invalidates a session.
     * @param sessionId The session ID to invalidate.
     */
    public static void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}