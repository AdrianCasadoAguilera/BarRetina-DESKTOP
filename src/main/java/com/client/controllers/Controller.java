package com.client.controllers;

import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;

import java.util.Map;

import com.client.utils.AppData;

public class Controller {

    @FXML
    private ScrollPane scrollPane;

    private VBox contentBox;

    @FXML
    public void initialize() {
        contentBox = new VBox(20); // espacio entre categorías
        scrollPane.setContent(contentBox);

        loadAllProducts(); // Cargar productos al iniciar
    }

    private void loadAllProducts() {
        for (String categoria : AppData.productes.keySet()) {
            VBox categoriaBox = new VBox(10);

            Text categoriaTitle = new Text(categoria);
            categoriaTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-padding: 15 0 10 0;");
            categoriaBox.getChildren().add(categoriaTitle);

            Map<String, Map<String, Object>> productesCategoria = AppData.productes.get(categoria);

            for (String nomProducte : productesCategoria.keySet()) {
                Map<String, Object> dadesProducte = productesCategoria.get(nomProducte);

                String nom = (String) dadesProducte.get("nom");
                Float preu = (Float) dadesProducte.get("preu");
                String descripcio = (String) dadesProducte.get("descripcio");

                HBox producteBox = new HBox(20);
                producteBox.setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(producteBox, Priority.ALWAYS);

                VBox infoBox = new VBox(5);
                HBox.setHgrow(infoBox, Priority.ALWAYS);
                infoBox.setMaxWidth(Double.MAX_VALUE);
                Text nomText = new Text(nom);
                nomText.setStyle("-fx-font-size: 16px;");

                Text descripcioText = new Text(descripcio);
                descripcioText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

                infoBox.getChildren().addAll(nomText, descripcioText);

                Text preuText = new Text(String.format("%.2f €", preu));
                preuText.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                producteBox.getChildren().addAll(infoBox, preuText);
                categoriaBox.getChildren().add(producteBox);
            }

            contentBox.getChildren().add(categoriaBox);
        }
    }

    public void refreshProducts() {
        contentBox.getChildren().clear(); // Vaciar antes de volver a cargar
        loadAllProducts(); // Vuelve a pintar los productos
    }
}
