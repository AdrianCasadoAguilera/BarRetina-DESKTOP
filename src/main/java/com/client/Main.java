package com.client;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.json.JSONObject;

import com.client.utils.AppData;
import com.client.utils.Connection;
import com.client.utils.UtilsViews;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    final int WINDOW_WIDTH = 1280;
    final int WINDOW_HEIGHT = 720;

    @Override
    public void start(Stage stage) throws Exception {

        // Carrega les vistes FXML
        UtilsViews.addView(getClass(), "login", "/assets/layout.fxml");
        UtilsViews.addView(getClass(), "main", "/assets/main.fxml");
        Scene scene = new Scene(UtilsViews.parentContainer);

        Path configPath = Paths.get(System.getProperty("user.home"), ".barretina", "CONFIG.xml");

        if(Files.exists(configPath )) {
            System.out.println("Connectant al servidor WebSocket...");

            Map<String, String> config = AppData.getConfigMap();
            Connection conn = Connection.getInstance();
            boolean connected = conn.connect(config.get("url"));

            if(connected) {
                UtilsViews.setView("main");
                conn.loadAllProducts();
                JSONObject rst = new JSONObject();
                rst.put("type", "registration");
                rst.put("name", config.get("ubicacio"));
                conn.send(rst);
                System.out.println(AppData.productes);
            } 
        } else {
            UtilsViews.setView("login");
        }

        stage.setScene(scene);
        stage.setTitle("JavaFX App");
        stage.setWidth(WINDOW_WIDTH);
        stage.setHeight(WINDOW_HEIGHT);
        stage.show();

        // Afegeix una icona només si no és un Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            Image icon = new Image("file:icons/icon.png");
            stage.getIcons().add(icon);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
