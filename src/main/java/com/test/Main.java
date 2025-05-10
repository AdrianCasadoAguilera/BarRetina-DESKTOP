package com.test;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

public class Main {

    private static Set<String> categoriesDisponibles = new HashSet<>();
    private static WebSocketClient client;
    private static boolean acabat = false;

    public static void main(String[] args) {
        try {
            URI uri = new URI("wss://barretina7.ieti.site");

            client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connectat al servidor WebSocket!");
                    mostrarMenu();
                }

                @Override
                public void onMessage(String message) {
                    JSONObject resposta = new JSONObject(message);
                    String tipus = resposta.optString("type");

                    switch (tipus) {
                        case "tags":
                            JSONArray tags = resposta.getJSONArray("tags");
                            categoriesDisponibles.clear();
                            System.out.println("\nCategories disponibles:");
                            for (int i = 0; i < tags.length(); i++) {
                                String tag = tags.getString(i);
                                categoriesDisponibles.add(tag);
                                System.out.println(" - " + tag);
                            }
                            break;

                        case "productes":
                            JSONArray plats = resposta.getJSONArray("productes");
                            if (plats.length() == 0) {
                                System.out.println("Aquesta categoria no té plats.");
                            } else {
                                System.out.println("\nPlats trobats:");
                                for (int i = 0; i < plats.length(); i++) {
                                    JSONObject plat = plats.getJSONObject(i);
                                    System.out.println(" - " + plat.getString("nom") +
                                            " (" + plat.getDouble("preu") + " €): " +
                                            plat.getString("descripcio"));
                                }
                            }
                            break;

                        case "pong":
                            System.out.println("Connexió activa (pong).");
                            break;

                        default:
                            System.out.println("Resposta desconeguda del servidor: " + message);
                    }

                    mostrarMenu();
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connexió tancada: " + reason);
                    acabat = true;
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error en la connexió:");
                    ex.printStackTrace();
                    acabat = true;
                }
            };

            client.connect();

            Scanner scanner = new Scanner(System.in);
            while (!acabat) {
                if (!client.isOpen()) {
                    Thread.sleep(500);
                    continue;
                }

                System.out.print("\nEscriu una opció: ");
                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        JSONObject getTags = new JSONObject();
                        getTags.put("type", "getTags");
                        client.send(getTags.toString());
                        break;

                    case "2":
                        System.out.print("Introdueix la categoria que vols consultar: ");
                        String categoria = scanner.nextLine().trim();

                        if (categoriesDisponibles.contains(categoria)) {
                            JSONObject getProductes = new JSONObject();
                            getProductes.put("type", "getProductes");
                            getProductes.put("category", categoria);
                            client.send(getProductes.toString());
                        } else {
                            System.out.println("Categoria no vàlida. Escriu abans l'opció 1 per consultar les categories disponibles.");
                        }
                        break;

                    case "3":
                        client.close();
                        break;

                    default:
                        System.out.println("Opció no vàlida. Escriu un número del 1 al 3.");
                        mostrarMenu();
                }
            }

        } catch (Exception e) {
            System.out.println("Error iniciant el client:");
            e.printStackTrace();
        }
    }

    private static void mostrarMenu() {
        System.out.println("\nMENÚ:");
        System.out.println("1. Consultar categories disponibles");
        System.out.println("2. Consultar plats d'una categoria");
        System.out.println("3. Sortir");
    }
}
