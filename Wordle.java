package me.rabbittv.wordle;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wordle extends Application {
    private String targetWord;
    private StringBuilder guessedWord;
    private Label guessedWordLabel;
    private int incorrectGuesses = 0;
    private final int maxIncorrectGuesses = 5;
    private Label incorrectGuessesLabel = new Label("Incorrect Guesses: " + incorrectGuesses);
    private GridPane boxesGrid = new GridPane();
    private TextField inputField;
    private List<Label> guessedLabels = new ArrayList<>(); // Define guessedLabels
    private GridPane keyboardGrid = new GridPane(); // Define keyboardGrid
    private VBox guessedWordsBox = new VBox(); // Define guessedWordsBox
    private List<String> guessedWordsList = new ArrayList<>(); // Track guessed words

    @Override
    public void start(Stage primaryStage) {
        initializeGame();

        // Create buttons for each letter
        String[] letters = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                "A", "S", "D", "F", "G", "H", "J", "K", "L", "Z",
                "X", "C", "V", "B", "N", "M"};

        for (int i = 0; i < letters.length; i++) {
            Button button = new Button(letters[i]);
            int finalI = i;
            button.setOnAction(e -> handleButtonClick(letters[finalI].charAt(0)));
            keyboardGrid.add(button, i % 10, i / 10);
        }

        HBox root = new HBox(keyboardGrid, guessedWordLabel);
        VBox vbox = new VBox(boxesGrid, root, incorrectGuessesLabel, guessedWordsBox);
        // Add text box to type the word
        inputField = new TextField();
        inputField.setPromptText("Enter your guess");
        inputField.setOnAction(event -> handleGuess(inputField.getText()));
        vbox.getChildren().add(inputField);

        Scene scene = new Scene(vbox, 1280, 720); //set starting resolution of game to 720p
        scene.getStylesheets().add("Style.css");
        primaryStage.setTitle("Wordle");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeGame() { //might want to change the path of words.txt
        List<String> words = readWordsFromFile("C:\\Users\\slach\\IdeaProjects\\Wordle\\src\\main\\java\\me\\rabbittv\\words.txt");
        Random random = new Random();
        targetWord = words.get(random.nextInt(words.size()));
        guessedWord = new StringBuilder("".repeat(targetWord.length()));
        guessedWordLabel = new Label(guessedWord.toString());
        guessedWordLabel.getStyleClass().add("guessed-word");
        incorrectGuesses = 0;
        incorrectGuessesLabel.setText("Incorrect Guesses: " + incorrectGuesses);
    }

    private List<String> readWordsFromFile(String filename) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                words.add(line.trim().toUpperCase());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    private void handleGuess(String guess) {
        guess = guess.toLowerCase(); // Convert guess to lowercase to make it not caps sensitive

        // Check if the guess is 5 chars long
        if (guess.length() != 5) {
            System.out.println("Make sure the word is 5 char long");
            return;
        }

        // Check if the guessed word exists in the list of valid words
        if (!readWordsFromFile("C:\\Users\\slach\\IdeaProjects\\Wordle\\src\\main\\java\\me\\rabbittv\\words.txt").contains(guess.toUpperCase())) {
            System.out.println("The word \"" + guess + "\" does not exist in the list.");
            return;
        }

        // Clear previous guesses
        guessedLabels.clear();
        boxesGrid.getChildren().clear(); // Clear previous boxes

        for (int i = 0; i < targetWord.length(); i++) {
            char letter = guess.charAt(i);
            Label box = new Label(Character.toString(letter));
            box.getStyleClass().add("guessed-letter");

            char targetLetter = Character.toLowerCase(targetWord.charAt(i)); // Convert target letter to lowercase
            if (guess.charAt(i) == targetLetter) {
                box.getStyleClass().add("correct");
                guessedWord.setCharAt(i, letter); // Update guessed word
            } else if (targetWord.toLowerCase().contains(Character.toString(letter))) {
                box.getStyleClass().add("incorrect");
            } else {
                box.getStyleClass().add("not-in-word");
            }
            guessedLabels.add(box);
            boxesGrid.add(box, i, 0); // Add box to boxesGrid
        }

        // Update guessed word label to show correct guesses
        guessedWordLabel.setText(guessedWord.toString());

        // Add guessed word to guessed words list
        guessedWordsList.add(guess);

        // Update guessed words box
        updateGuessedWordsBox();

        if (guessedWord.toString().equalsIgnoreCase(targetWord)) {
            System.out.println(" You won. The word was  " + targetWord);
            disableKeyboard();
        } else {
            incorrectGuesses++;
            incorrectGuessesLabel.setText("Incorrect Guesses: " + incorrectGuesses);
            if (incorrectGuesses >= maxIncorrectGuesses) {
                System.out.println("You lost lmao. The word was " + targetWord);
                disableKeyboard();
            } else {
                System.out.println("nope");
            }
        }
        inputField.setText("");
    }


    private void handleButtonClick(char letter) {
        if (incorrectGuesses < maxIncorrectGuesses) {
            inputField.setText(inputField.getText() + letter);
        }
    }

    private void disableKeyboard() {
        for (Node node : keyboardGrid.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
            }
        }
    }

    private void updateGuessedWordsBox() {
        guessedWordsBox.getChildren().clear();
        for (String word : guessedWordsList) {
            HBox wordBox = new HBox();
            Label wordLabel = new Label(word);
            wordBox.getChildren().add(wordLabel);

            // Display correct, incorrect, and not-in-word letters
            for (int i = 0; i < targetWord.length(); i++) {
                char guessChar = word.length() > i ? Character.toLowerCase(word.charAt(i)) : '.';
                char targetChar = Character.toLowerCase(targetWord.charAt(i));
                Label letterLabel = new Label(Character.toString(word.charAt(i)));

                if (guessChar == targetChar) {
                    letterLabel.getStyleClass().add("correct");
                } else if (targetWord.toLowerCase().contains(Character.toString(guessChar))) {
                    letterLabel.getStyleClass().add("incorrect");
                } else {
                    letterLabel.getStyleClass().add("not-in-word");
                }

                wordBox.getChildren().add(letterLabel);
            }

            guessedWordsBox.getChildren().add(wordBox);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
