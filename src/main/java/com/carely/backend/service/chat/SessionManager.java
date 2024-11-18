package com.carely.backend.service.chat;

import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;
public class SessionManager {
    private static final ConcurrentHashMap<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    public static void addSession(String sessionId, WebSocketSession session) {
        sessionMap.put(sessionId, session);
    }
    public static void removeSession(String sessionId) {
        sessionMap.remove(sessionId);
    }
    public static WebSocketSession getSession(String sessionId) {
        return sessionMap.get(sessionId);
    }
}
