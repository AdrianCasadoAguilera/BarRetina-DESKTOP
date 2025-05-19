package com.client.utils;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import com.client.controllers.Controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;

public class Connection {

    private static Connection instance = null;
    private WebSocketClient conn = null;
    private ScheduledExecutorService pingScheduler;

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
                    JSONObject request = new JSONObject();
                    request.put("type", "getTables");
                    conn.send(request.toString());
                }

                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                    // System.out.println("Missatge rebut: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (pingScheduler != null) {
                        pingScheduler.shutdownNow();
                    }
                    System.out.println("Connexió tancada: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("Error: " + ex.getMessage());
                }
            };
            conn.connectBlocking();
            startPing();
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
        System.out.println("Missatge rebut pel client: " + message); 

        // Missatges amb "type"
        if (jsonMessage.has("type")) {
            String type = jsonMessage.getString("type");
            switch (type) {
                case "productes":
                    loadProducts(jsonMessage.getJSONArray("productes"), jsonMessage.getString("tag"));
                    break;
                case "tags":
                    loadTags(jsonMessage.getJSONArray("tags"));
                    break;
                case "pong":
                    // No cal fer res
                    break;
                default:
                    System.out.println("Unknown message type: " + type);
                    break;
            }
        }

        // Missatges amb "key"
        if (jsonMessage.has("key")) {
            String key = jsonMessage.getString("key");
            switch (key) {
                case "allProductes":
                    JSONArray productes = jsonMessage.getJSONArray("productes");
                    System.out.println("Rebuts tots els productes: " + productes.length());
                    break;
                case "tables":
                    JSONArray tables = jsonMessage.getJSONArray("tables");
                    System.out.println("Taules rebudes: "+tables.length());
                    Controller controller = (Controller) UtilsViews.getController("main");
                    controller.updateTablesView(tables); 
                    break;
                default:
                    System.out.println("Unknown message key: " + key);
                    break;
            }
        }
    }


    private void loadTags(JSONArray tags) {
        for (int i = 0; i < tags.length(); i++) {
            String tag = tags.getString(i);
            AppData.productes.put(tag, FXCollections.observableHashMap());

            JSONObject request = new JSONObject();
            request.put("type", "getProductes");
            request.put("category", tag);
            conn.send(request.toString());
        }
    }

    private void loadProducts(JSONArray productes, String categoria) {

        for(int i = 0; i < productes.length(); i++) {
            JSONObject producte = productes.getJSONObject(i);
            String nom = producte.getString("nom");
            Float preu = producte.getFloat("preu");
            String descripcio = producte.getString("descripcio");
            // String categoria = producte.getString("tag");

            Map<String, Object> producteMap = new HashMap<>();
            producteMap.put("nom", nom);
            producteMap.put("preu", preu);
            producteMap.put("descripcio", descripcio);

            AppData.productes.get(categoria).put(nom, producteMap);

            Platform.runLater(() -> {
                Controller controller = (Controller) UtilsViews.getController("main");
                controller.refreshProducts();
            });
        }
        // System.out.println(AppData.productes);
    }

    private void startPing() {
        if (pingScheduler != null && !pingScheduler.isShutdown()) return;

        pingScheduler = Executors.newSingleThreadScheduledExecutor();
        pingScheduler.scheduleAtFixedRate(() -> {
            if (conn != null && conn.isOpen()) {
                JSONObject ping = new JSONObject();
                ping.put("type", "ping");
                conn.send(ping.toString());
            }
        }, 30, 30, TimeUnit.SECONDS);
    }
    public void send(JSONObject message) {
        if (conn != null && conn.isOpen()) {
            conn.send(message.toString());
        } else {
            System.err.println("No WebSocket connection.");
        }
    }

}
