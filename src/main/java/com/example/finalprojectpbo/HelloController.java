package com.example.finalprojectpbo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HelloController {
    @FXML
    private VBox startScreen;
    @FXML
    private VBox gameScreen;
    @FXML
    private VBox winScreen;
    @FXML
    private Label pairsFoundLabel;
    @FXML
    private GridPane gameGrid;

    private int gridSize;
    private List<String> tileValues;
    private Button[][] buttons;
    private Button firstButton = null;
    private Button secondButton = null;
    private int pairsFound = 0;
    private int totalPairs;
    private boolean isProcessing = false; // Prevent clicks during processing

    public void setGridSize4x4(ActionEvent event) {
        setupGame(4);
    }

    public void setGridSize6x6(ActionEvent event) {
        setupGame(6);
    }

    public void setGridSize8x8(ActionEvent event) {
        setupGame(8);
    }

    private void setupGame(int size) {
        gridSize = size;
        totalPairs = (gridSize * gridSize) / 2;
        pairsFound = 0;

        pairsFoundLabel.setText("Pairs Found: 0");
        startScreen.setVisible(false);
        gameScreen.setVisible(true);
        initializeGame();
    }

    private void initializeGame() {
        gameGrid.getChildren().clear(); // Clear previous buttons
        buttons = new Button[gridSize][gridSize];
        tileValues = generateTileValues();

        int index = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button button = new Button();
                button.setPrefSize(100, 100);

                ImageView imageView = new ImageView();
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                button.setGraphic(imageView);

                button.setOnAction(event -> handleTileClick(button));
                button.setUserData(tileValues.get(index++));

                buttons[row][col] = button;
                gameGrid.add(button, col, row);
            }
        }
    }

    private List<String> generateTileValues() {
        List<String> values = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            String value;
            value = "image" + (i + 1) + ".png";
            values.add(value);
            values.add(value);
        }
        Collections.shuffle(values);
        return values;
    }

    private void handleTileClick(Button clickedButton) {
        if (isProcessing || clickedButton.getGraphic() != null && ((ImageView) clickedButton.getGraphic()).getImage() != null) {
            return; // Prevent clicks during processing or on already revealed tiles
        }

        String imageFileName = (String) clickedButton.getUserData();
        try {
            // For 4x4 grid, load images
            System.out.println("Attempting to load image: " + imageFileName);
            java.io.InputStream imageStream = getClass().getResourceAsStream("/images/" + imageFileName);
            if (imageStream == null) {
                throw new Exception("Image resource not found: " + imageFileName);
            }
            Image image = new Image(imageStream);
            ImageView imageView = (ImageView) clickedButton.getGraphic();
            imageView.setImage(image);
            System.out.println("Image successfully loaded.");
        } catch (Exception e) {
            System.out.println("Error loading image: " + e.getMessage());
            e.printStackTrace();
        }

        if (firstButton == null) {
            firstButton = clickedButton;
        } else if (secondButton == null) {
            secondButton = clickedButton;
            checkMatch();
        }
    }

    private void checkMatch() {
        isProcessing = true; // Disable interactions during check

        if (firstButton.getUserData().equals(secondButton.getUserData())) {
            pairsFound++;
            pairsFoundLabel.setText("Pairs Found: " + pairsFound);
            firstButton = null;
            secondButton = null;
            isProcessing = false; // Re-enable interactions

            if (pairsFound == totalPairs) {
                showWinScreen();
            }
        } else {
            Button tempFirst = firstButton;
            Button tempSecond = secondButton;
            firstButton = null;
            secondButton = null;

            new Thread(() -> {
                try {
                    Thread.sleep(1500); // Pause before hiding tiles
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Platform.runLater(() -> {
                    ((ImageView) tempFirst.getGraphic()).setImage(null);
                    ((ImageView) tempSecond.getGraphic()).setImage(null);
                    isProcessing = false; // Re-enable interactions after hiding tiles
                });
            }).start();
        }
    }

    private void showWinScreen() {
        gameScreen.setVisible(false);
        winScreen.setVisible(true);
    }

    public void restartGame(ActionEvent event) {
        winScreen.setVisible(false);
        startScreen.setVisible(true);
    }
}
