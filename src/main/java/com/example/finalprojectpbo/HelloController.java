package com.example.finalprojectpbo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    @FXML
    private GridPane gameGrid;

    @FXML
    private StackPane winScreen;

    @FXML
    private Label winMessage;

    @FXML
    private Label pairsFoundLabel;

    @FXML
    private Button restartButton;

    private final int gridSize = 4;
    private final List<String> tileValues = new ArrayList<>();
    private final Button[][] buttons = new Button[gridSize][gridSize];

    private Button firstButton = null;
    private Button secondButton = null;
    private boolean isAnimating = false;

    private Timeline timeline;

    private int pairsFound = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeGame();
    }

    private void initializeGame() {
        resetGameState();
        populateTileValues();
        setupGrid();
        winScreen.setVisible(false);
        updatePairsFoundLabel();
    }

    private void resetGameState() {
        pairsFound = 0;
        firstButton = null;
        secondButton = null;
        tileValues.clear();
        gameGrid.getChildren().clear(); // Clear previous buttons from the grid
    }

    private void populateTileValues() {
        for (int i = 0; i < (gridSize * gridSize) / 2; i++) {
            String value = String.valueOf((char) ('A' + i));
            tileValues.add(value);
            tileValues.add(value);
        }
        Collections.shuffle(tileValues);
    }

    private void setupGrid() {
        int index = 0;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                Button button = new Button();
                button.setPrefSize(100, 100);
                button.setOnAction(this::handleTileClick);
                buttons[row][col] = button;
                gameGrid.add(button, col, row);

                button.setUserData(tileValues.get(index++));
            }
        }
    }

    private void handleTileClick(ActionEvent event) {
        if (isAnimating) return;

        Button clickedButton = (Button) event.getSource();

        // Ignore already revealed buttons
        if (!clickedButton.getText().isEmpty()) return;

        clickedButton.setText((String) clickedButton.getUserData());

        if (firstButton == null) {
            firstButton = clickedButton;
        } else {
            secondButton = clickedButton;
            checkMatch();
        }
    }

    private void checkMatch() {
        isAnimating = true;

        if (firstButton.getUserData().equals(secondButton.getUserData())) {
            pairsFound++;
            updatePairsFoundLabel();
            resetTurn();
            checkWinCondition();
        } else {
            timeline = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
                firstButton.setText("");
                secondButton.setText("");
                resetTurn();
            }));
            timeline.play();
        }
    }

    private void resetTurn() {
        firstButton = null;
        secondButton = null;
        isAnimating = false;
    }

    private void updatePairsFoundLabel() {
        pairsFoundLabel.setText("Pairs Found: " + pairsFound);
    }

    private void checkWinCondition() {
        if (pairsFound == (gridSize * gridSize) / 2) {
            winMessage.setText("Congratulations! You've found all pairs!");
            winScreen.setVisible(true);
        }
    }

    @FXML
    private void restartGame(ActionEvent event) {
        initializeGame();
    }
}
