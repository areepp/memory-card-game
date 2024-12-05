package com.example.finalprojectpbo;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {

    @FXML
    private GridPane gameGrid;

    private final int gridSize = 4;
    private final List<String> tileValues = new ArrayList<>();
    private final Button[][] buttons = new Button[gridSize][gridSize];

    private Button firstButton = null;
    private Button secondButton = null;
    private boolean isAnimating = false;

    private Timeline timeline;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeGame();
    }

    private void initializeGame() {
        populateTileValues();
        setupGrid();
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
            // Match: Keep buttons revealed
            resetTurn();
        } else {
            // No match: Hide buttons after 1.5 seconds
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
}
