package Controllers;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriviaGame {

    private int score = 0;
    private int currentQuestion = 0;
    private final int totalQuestions = 3;
    private List<JSONObject> questions = new ArrayList<>();
    private VBox gameLayout;
    private final VBox formLayout;

    public TriviaGame(VBox formLayout) {
        this.formLayout = formLayout;
    }

    public VBox start() {
        gameLayout = new VBox(10);
        gameLayout.setAlignment(Pos.CENTER);
        gameLayout.setPadding(new Insets(20));
        Label questionLabel = new Label("Fetching questions...");
        gameLayout.getChildren().add(questionLabel);
        Button[] answerButtons = new Button[4];

        try {
            fetchQuestions();
            displayQuestion(questionLabel, answerButtons);
        } catch (Exception e) {
            questionLabel.setText("Error: " + e.getMessage());
        }

        return gameLayout;
    }

    private void fetchQuestions() throws Exception {
        String apiUrl = "https://opentdb.com/api.php?amount=" + totalQuestions + "&type=multiple";
        int maxRetries = 3;
        int retryDelay = 5000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == 429) {
                    if (attempt == maxRetries) {
                        throw new Exception("API rate limit exceeded after " + maxRetries + " attempts.");
                    }
                    Thread.sleep(retryDelay);
                    continue;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder responseContent = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(responseContent.toString());
                JSONArray results = jsonResponse.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    questions.add(results.getJSONObject(i));
                }
                break;

            } catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new Exception("Failed to fetch questions after " + maxRetries + " attempts: " + e.getMessage());
                }
                Thread.sleep(retryDelay);
            }
        }
    }

    private void displayQuestion(Label questionLabel, Button[] answerButtons) {
        if (currentQuestion >= totalQuestions) {
            questionLabel.setText("Game Over! Score: " + score + "/" + totalQuestions);
            gameLayout.getChildren().removeIf(node -> node instanceof Button);

            Button backButton = new Button("Back to Form");
            backButton.setOnAction(e -> {
                try {
                    Parent gameView = FXMLLoader.load(getClass().getResource("/GameView.fxml"));
                    Scene gameScene = new Scene(gameView);

                    Stage window = (Stage)((Node) e.getSource()).getScene().getWindow(); // <<< use e here
                    window.setScene(gameScene);
                    window.setTitle("Liste des jeux");
                    window.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            gameLayout.getChildren().add(backButton);
            return;
        }

        JSONObject questionData = questions.get(currentQuestion);
        String question = cleanText(questionData.getString("question"));
        String correctAnswer = cleanText(questionData.getString("correct_answer"));
        JSONArray incorrectAnswers = questionData.getJSONArray("incorrect_answers");

        List<String> allAnswers = new ArrayList<>();
        allAnswers.add(correctAnswer);
        for (int i = 0; i < incorrectAnswers.length(); i++) {
            allAnswers.add(cleanText(incorrectAnswers.getString(i)));
        }
        Collections.shuffle(allAnswers);

        questionLabel.setText(question + "\n\nCategory: " + questionData.getString("category") +
                "\nDifficulty: " + questionData.getString("difficulty"));
        questionLabel.setWrapText(true);

        gameLayout.getChildren().removeIf(node -> node instanceof Button);
        for (int i = 0; i < allAnswers.size(); i++) {
            String answer = allAnswers.get(i);
            answerButtons[i] = new Button((i + 1) + ". " + answer);
            answerButtons[i].setPrefWidth(400);
            int finalI = i;
            answerButtons[i].setOnAction(e -> {
                if (answer.equals(correctAnswer)) {
                    score++;
                    questionLabel.setText("Correct! Score: " + score);
                } else {
                    questionLabel.setText("Wrong! Correct answer: " + correctAnswer);
                }
                currentQuestion++;
                displayQuestion(questionLabel, answerButtons);
            });
            gameLayout.getChildren().add(answerButtons[i]);
        }
    }

    private void resetGame() {
        score = 0;
        currentQuestion = 0;
        questions.clear();
        VBox parent = (VBox) gameLayout.getParent();
        parent.getChildren().clear();
        parent.getChildren().add(formLayout);
    }

    // Simple method to clean common HTML entities
    private String cleanText(String text) {
        return text.replace("&quot;", "\"")
                .replace("&amp;", "&")
                .replace("&#039;", "'")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}