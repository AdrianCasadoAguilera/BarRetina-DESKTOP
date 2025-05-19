package com.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.application.Platform;

import java.util.Map;
import java.util.HashMap;

import com.client.utils.AppData;
import com.client.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Controller {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox ordersBox;

    @FXML
    private VBox orderDetail;

    private VBox contentBox;

    // Associació entre el text visible de la comanda i el detall complet
    private Map<String, JSONArray> tableOrders = new HashMap<>();

    @FXML
    public void initialize() {
        // Configuració scroll de productes
        contentBox = new VBox(20);
        contentBox.setStyle("-fx-background-color: white;");
        contentBox.setFillWidth(true);

        scrollPane.setContent(contentBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        loadAllProducts();
    }

    private void loadAllProducts() {
        contentBox.getChildren().clear();

        for (String categoria : AppData.productes.keySet()) {
            VBox categoriaBox = new VBox(10);

            Text categoriaTitle = new Text(Utils.capitalize(categoria));
            categoriaTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 15 0 10 0;");
            categoriaBox.getChildren().add(categoriaTitle);

            Map<String, Map<String, Object>> productesCategoria = AppData.productes.get(categoria);

            for (String nomProducte : productesCategoria.keySet()) {
                Map<String, Object> dadesProducte = productesCategoria.get(nomProducte);

                String nom = Utils.capitalize((String) dadesProducte.get("nom"));
                Float preu = (Float) dadesProducte.get("preu");
                String descripcio = (String) dadesProducte.get("descripcio");

                HBox producteBox = new HBox(25);
                producteBox.setFillHeight(true);
                producteBox.setMaxWidth(Double.MAX_VALUE);
                producteBox.setStyle("-fx-background-color: #89A36D; -fx-padding: 12px;");
                HBox.setHgrow(producteBox, Priority.ALWAYS);

                VBox infoBox = new VBox(5);
                HBox.setHgrow(infoBox, Priority.ALWAYS);
                infoBox.setMaxWidth(Double.MAX_VALUE);
                Text nomText = new Text(nom);
                nomText.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Text descripcioText = new Text(descripcio);
                descripcioText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                infoBox.getChildren().addAll(nomText, descripcioText);

                Text preuText = new Text(String.format("%.2f €", preu));
                preuText.setStyle("-fx-font-size: 20px; -fx-text-fill: #333;");

                producteBox.getChildren().addAll(infoBox, preuText);
                categoriaBox.getChildren().add(producteBox);
            }

            contentBox.setStyle("-fx-background-color: white; -fx-padding: 16 31 16 16;");
            contentBox.getChildren().add(categoriaBox);
        }
    }

    public void refreshProducts() {
        contentBox.getChildren().clear();
        loadAllProducts();
    }

    // Actualitza la llista de comandes rebuda del servidor
    public void updateTablesView(JSONArray tables) {
        Platform.runLater(() -> {
            ordersBox.getChildren().clear();
            orderDetail.getChildren().clear();
            tableOrders.clear();

            final Map<HBox, String> boxToEntryMap = new HashMap<>();
            final HBox[] selectedBox = {null}; // Referència mutable al box seleccionat

            for (int i = 0; i < tables.length(); i++) {
                JSONObject table = tables.getJSONObject(i);
                int number = table.getInt("number");
                int clients = table.getInt("clients");
                JSONArray items = table.getJSONArray("items");

                if (items.length() == 0) continue;

                int totalQuantity = 0;
                float totalPrice = 0f;

                for (int j = 0; j < items.length(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    int amount = item.getInt("amount");
                    float price = item.has("price") ? item.getFloat("price") : 0f;

                    totalQuantity += amount;
                    totalPrice += amount * price;
                }

                String entry = String.format("Taula %d - %d productes - Total: %.2f €", number, totalQuantity, totalPrice);
                tableOrders.put(entry, items);

                // Crear contenidor clicable
                HBox orderBox = new HBox();
                orderBox.setStyle("-fx-background-color: #F4C430; -fx-padding: 8px; -fx-background-radius: 6px;");
                orderBox.setMaxWidth(250);
                orderBox.setMinHeight(30);
                orderBox.setCursor(javafx.scene.Cursor.HAND);

                Text entryText = new Text(entry);
                entryText.setStyle("-fx-font-size: 16px; -fx-fill: #000;");
                orderBox.getChildren().add(entryText);

                boxToEntryMap.put(orderBox, entry);

                // Hover
                orderBox.setOnMouseEntered(e -> {
                    if (selectedBox[0] != orderBox)
                        orderBox.setStyle("-fx-background-color: #e5b221; -fx-padding: 8px; -fx-background-radius: 6px;");
                });

                orderBox.setOnMouseExited(e -> {
                    if (selectedBox[0] != orderBox)
                        orderBox.setStyle("-fx-background-color: #F4C430; -fx-padding: 8px; -fx-background-radius: 6px;");
                });

                // Clic
                orderBox.setOnMousePressed(e -> {
                    orderBox.setStyle("-fx-background-color: #d1a31d; -fx-padding: 8px; -fx-background-radius: 6px;");
                });

                orderBox.setOnMouseReleased(e -> {
                    // Deselecciona l'anterior
                    if (selectedBox[0] != null) {
                        selectedBox[0].setStyle("-fx-background-color: #F4C430; -fx-padding: 8px; -fx-background-radius: 6px;");
                    }

                    // Marca el nou com seleccionat
                    orderBox.setStyle("-fx-background-color:rgb(204, 133, 0); -fx-padding: 8px; -fx-background-radius: 6px;");
                    selectedBox[0] = orderBox;

                    String selectedEntry = boxToEntryMap.get(orderBox);
                    showOrderDetail(tableOrders.get(selectedEntry));
                });

                ordersBox.getChildren().add(orderBox);
            }
        });
    }

    private void showOrderDetail(JSONArray items) {
        orderDetail.getChildren().clear();

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String product = Utils.capitalize(item.getString("product"));
            int amount = item.getInt("amount");
            float price = item.getFloat("price");

            HBox productBox = new HBox(8); 
            productBox.setMaxWidth(Double.MAX_VALUE);
            productBox.setPrefWidth(Double.MAX_VALUE);
            productBox.setStyle(i % 2 == 0
                ? "-fx-background-color: rgb(223, 223, 223); -fx-padding: 8px;"
                : "-fx-background-color: rgb(202, 202, 202); -fx-padding: 8px;");

            Text producte = new Text("x"+ amount+ " " + product);
            producte.setStyle("-fx-font-size: 14px;");
            HBox.setHgrow(producte, Priority.ALWAYS);

            Text preu = new Text(String.format("%.2f €", price*amount));
            preu.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            productBox.getChildren().addAll(producte, preu);
            orderDetail.getChildren().add(productBox);
        }
    }

}
