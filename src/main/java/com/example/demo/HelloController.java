package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HelloController {
    @FXML
    private ComboBox<String> companyComboBox;
    @FXML
    private ComboBox<String> giftComboBox;
    @FXML
    private CheckBox concertCheckBox;
    @FXML
    private CheckBox loyalClientCheckBox;
    @FXML
    private Label totalCostLabel;

    private Map<String, Map<String, Double>> companyGifts = new HashMap<>();
    private double concertCost;

    public void initialize() {
        loadDataFromFile("data.txt");

        companyComboBox.getItems().addAll(companyGifts.keySet());

        companyComboBox.setOnAction(event -> updateGifts());

        giftComboBox.setOnAction(event -> calculateCost());
        concertCheckBox.setOnAction(event -> calculateCost());
        loyalClientCheckBox.setOnAction(event -> calculateCost());
    }

    private void loadDataFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentCompany = null;

            while ((line = br.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty()) continue;

                if (line.contains(",")) {
                    String[] parts = line.split(",");
                    String itemName = parts[0].trim();
                    double itemCost = Double.parseDouble(parts[1].trim());

                    if ("Концерт".equals(itemName)) {
                        concertCost = itemCost;
                    } else if (currentCompany != null) {
                        companyGifts.get(currentCompany).put(itemName, itemCost);
                    }
                } else {
                    currentCompany = line;
                    companyGifts.put(currentCompany, new HashMap<>());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR, "Ошибка при чтении файла данных: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void updateGifts() {
        giftComboBox.getItems().clear();
        String selectedCompany = companyComboBox.getValue();
        if (selectedCompany != null && companyGifts.containsKey(selectedCompany)) {
            giftComboBox.getItems().addAll(companyGifts.get(selectedCompany).keySet());
            giftComboBox.getSelectionModel().selectFirst();
        }
        calculateCost();
    }

    private void calculateCost() {
        double totalCost = 0.0;
        String selectedCompany = companyComboBox.getValue();
        String selectedGift = giftComboBox.getValue();

        if (selectedCompany != null && selectedGift != null) {
            totalCost += companyGifts.get(selectedCompany).getOrDefault(selectedGift, 0.0);
        }

        if (concertCheckBox.isSelected()) {
            totalCost += concertCost;
        }

        if (loyalClientCheckBox.isSelected()) {
            totalCost *= 0.9;
        }

        totalCostLabel.setText(String.format("Итого: %.2f", totalCost));
    }
}
