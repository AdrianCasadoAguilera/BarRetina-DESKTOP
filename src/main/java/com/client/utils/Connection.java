package com.client.utils;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public boolean connect(String url) {
        try {
            URI uri = new URI(url);
            conn = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connectat al servidor WebSocket!");
                }

                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                    // System.out.println("Missatge rebut: " + message);
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void loadAllProducts() {
        if(conn != null && conn.isOpen()) {
            JSONObject request = new JSONObject();
            request.put("type", "getTags");
            conn.send(request.toString());
        } else {
            System.out.println("Not connected to the server.");
        }
    }

    private void handleMessage(String message) {
        JSONObject jsonMessage = new JSONObject(message);
        String type = jsonMessage.getString("type");
        switch (type) {
            case "productes":
                loadProducts(jsonMessage.getJSONArray("productes"));
                break;
            case "tags":
                loadTags(jsonMessage.getJSONArray("tags"));
                break;
            default:
                System.out.println("Unknown message type: " + type);
                break;
        }
        
    }

    private void loadTags(JSONArray tags) {
        for (int i = 0; i < tags.length(); i++) {
            String tag = tags.getString(i);
            AppData.productes.put(tag, new HashMap<>());

            JSONObject request = new JSONObject();
            request.put("type", "getProductes");
            request.put("category", tag);
            conn.send(request.toString());
        }
    }

    private void loadProducts(JSONArray productes) {

        for(int i = 0; i < productes.length(); i++) {
            JSONObject producte = productes.getJSONObject(i);
            String nom = producte.getString("nom");
            Float preu = producte.getFloat("preu");
            String descripcio = producte.getString("descripcio");
            String categoria = producte.getString("categoria");

            Map<String, Object> producteMap = new HashMap<>();
            producteMap.put("nom", nom);
            producteMap.put("preu", preu);
            producteMap.put("descripcio", descripcio);

            AppData.productes.get(categoria).put(nom, producteMap);

            
        }
        System.out.println(AppData.productes);
    }
}
