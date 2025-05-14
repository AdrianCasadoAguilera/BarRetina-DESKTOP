package com.client.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class AppData {

    public static ObservableMap<String, ObservableMap<String, Map<String, Object>>> productes = FXCollections.observableHashMap();


    public static List<Map<String, String>> getConfig() {
        List<Map<String, String>> config = List.of(new HashMap<>());
        try {
            Path configPath = Paths.get(System.getProperty("user.home"),".barretina", "CONFIG.xml");
            if(Files.exists(configPath)) {
                // Load the XML file and parse it to get the configuration values
                // For example, using a library like JAXB or DOM parser
            } 
        } catch(Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public static void saveConfig(String ubicacio, String url) {
        try {
            Path dirPath = Paths.get(System.getProperty("user.home"), ".barretina");
            Path configPath = dirPath.resolve("CONFIG.xml");

            // Crear directorio si no existe
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String content = """
            <?xml version="1.0" encoding="UTF-8"?>
            <config>
                <ubicacio>%s</ubicacio>
                <url>%s</url>
            </config>
            """.formatted(ubicacio, url);
            Files.writeString(configPath, content, StandardOpenOption.CREATE);

        } catch(IOException e) {
            e.printStackTrace();
        }
        
    }

    public static Map<String, String> getConfigMap() {
        Map<String, String> config = new HashMap<>();
        try {
            Path configPath = Paths.get(System.getProperty("user.home"), ".barretina", "CONFIG.xml");
            if(Files.exists(configPath)) {
                List<String> lines = Files.readAllLines(configPath);
                for (String line : lines) {
                    if (line.contains("<ubicacio>")) {
                        config.put("ubicacio", line.replaceAll("<.*?>", "").trim());
                    } else if (line.contains("<url>")) {
                        config.put("url", line.replaceAll("<.*?>", "").trim());
                    }
                }
            } 
        } catch(Exception e) {
            e.printStackTrace();
        }
        return config;
    }

}
