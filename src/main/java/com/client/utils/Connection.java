package com.client.utils;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class Connection {

    private static Connection instance = null;
    private WebSocketClient conn = null;

    private Connection() {
        
    }

    public static Connection getInstance() {
        if (instance == null) {
            instance = new Connection();
        }
        return instance;
    }

    public void connect(String url) {
        try {
            URI uri = new URI(url);
            conn = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connectat al servidor WebSocket!");
                }

                @Override
                public void onMessage(String message) {
                    System.out.println("Missatge rebut: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connexió tancada: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            };
            conn.connectBlocking();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
