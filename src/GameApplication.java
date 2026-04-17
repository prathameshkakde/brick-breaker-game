import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {

        final int[] score = {0};
        final double[] dx = {2};
        final double[] dy = {2};
        final boolean[] isPaused = {false};

        BorderPane root = new BorderPane();

        // ===== TOP UI =====
        VBox uiPane = new VBox(5);
        uiPane.setAlignment(Pos.CENTER);
        uiPane.setPrefHeight(60);
        uiPane.setStyle("-fx-background-color: #f0eeeb;");

        Text scoreText = new Text("Score: 0");
        scoreText.setVisible(false);

        Text statusText = new Text("");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button restartButton = new Button("RESTART");
        Button exitButton = new Button("EXIT");

        restartButton.setVisible(false);
        exitButton.setVisible(false);

        buttons.getChildren().addAll(restartButton, exitButton);
        uiPane.getChildren().addAll(scoreText, statusText, buttons);

        // ===== GAME AREA =====
        Pane gamePane = new Pane();
        gamePane.setPrefSize(600, 350);
        gamePane.setMaxSize(600, 350);
        gamePane.setMinSize(600, 350);
        gamePane.setStyle("-fx-background-color: #ebdfce; -fx-border-color: #204b65; -fx-border-width: 3px; -fx-padding: 10px;");

        StackPane wrapper = new StackPane(gamePane);
        root.setTop(uiPane);
        root.setCenter(wrapper);

        Scene scene = new Scene(root, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Brick Breaker Game");
        stage.show();

        gamePane.requestFocus();

        // ===== START MENU (CENTERED FIX) =====
        VBox startMenu = new VBox(10);
        startMenu.setAlignment(Pos.CENTER);

        Button playButton = new Button("PLAY");

        Text instructions = new Text(
                "\n← → Move\nP Pause\nBreak all bricks!"
        );

        startMenu.getChildren().addAll(playButton, instructions);

        // ⭐ THIS IS THE FIX
        StackPane startWrapper = new StackPane(startMenu);
        startWrapper.setPrefSize(600, 350);

        gamePane.getChildren().add(startWrapper);

        // ===== GAME OBJECTS =====
        Rectangle paddle = new Rectangle(100, 10, Color.web("#204b65"));
        paddle.setX(250);
        paddle.setY(320);
        paddle.setVisible(false);

        Circle ball = new Circle(8, Color.web("#010c15"));
        ball.setCenterX(300);
        ball.setCenterY(150);
        ball.setVisible(false);

        gamePane.getChildren().addAll(paddle, ball);

        List<Rectangle> bricks = new ArrayList<>();
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 5; c++) {
                Rectangle brick = new Rectangle(60, 20, Color.web("#006898"));
                brick.setX(60 + c * 100);
                brick.setY(20 + r * 40);
                brick.setVisible(false);
                gamePane.getChildren().add(brick);
                bricks.add(brick);
            }
        }

        // ===== CONTROLS =====
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) isPaused[0] = !isPaused[0];

            if (!isPaused[0] && e.getCode() == KeyCode.LEFT && paddle.getX() > 10)
                paddle.setX(paddle.getX() - 20);

            if (!isPaused[0] && e.getCode() == KeyCode.RIGHT &&
                    paddle.getX() < gamePane.getWidth() - paddle.getWidth() - 10)
                paddle.setX(paddle.getX() + 20);
        });

        // ===== GAME LOOP =====
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (isPaused[0]) return;

                ball.setCenterX(ball.getCenterX() + dx[0]);
                ball.setCenterY(ball.getCenterY() + dy[0]);

                if (ball.getCenterX() <= 10 || ball.getCenterX() >= gamePane.getWidth() - 10)
                    dx[0] *= -1;

                if (ball.getCenterY() <= 10)
                    dy[0] *= -1;

                if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())) {
                    double center = paddle.getX() + paddle.getWidth() / 2;
                    double offset = (ball.getCenterX() - center) / (paddle.getWidth() / 2);
                    double speed = 3;
                    dx[0] = offset * speed;
                    dy[0] = -Math.abs(speed);
                }

                if (ball.getCenterY() >= gamePane.getHeight() - 10) {
                    stop();
                    statusText.setText("GAME OVER");
                    restartButton.setVisible(true);
                    exitButton.setVisible(true);
                }

                for (Rectangle brick : new ArrayList<>(bricks)) {
                    if (ball.getBoundsInParent().intersects(brick.getBoundsInParent())) {
                        dy[0] *= -1;
                        gamePane.getChildren().remove(brick);
                        bricks.remove(brick);
                        score[0]++;
                        scoreText.setText("Score: " + score[0]);
                        break;
                    }
                }

                if (bricks.isEmpty()) {
                    stop();
                    statusText.setText("YOU WIN!");
                    restartButton.setVisible(true);
                    exitButton.setVisible(true);
                }
            }
        };

        // ===== PLAY =====
        playButton.setOnAction(e -> {
            startWrapper.setVisible(false);

            paddle.setVisible(true);
            ball.setVisible(true);
            scoreText.setVisible(true);

            for (Rectangle b : bricks) b.setVisible(true);

            gamePane.requestFocus();
            timer.start();
        });

        // ===== RESTART =====
        restartButton.setOnAction(e -> {

            ball.setCenterX(300);
            ball.setCenterY(150);
            dx[0] = 2;
            dy[0] = 2;

            score[0] = 0;
            scoreText.setText("Score: 0");
            statusText.setText("");

            restartButton.setVisible(false);
            exitButton.setVisible(false);

            for (Rectangle b : bricks) gamePane.getChildren().remove(b);
            bricks.clear();

            for (int r = 0; r < 3; r++) {
                for (int c = 0; c < 5; c++) {
                    Rectangle brick = new Rectangle(60, 20, Color.web("#006898"));
                    brick.setX(60 + c * 100);
                    brick.setY(20 + r * 40);
                    gamePane.getChildren().add(brick);
                    bricks.add(brick);
                }
            }

            gamePane.requestFocus();
            timer.start();
        });

        exitButton.setOnAction(e -> System.exit(0));
    }

    public static void main(String[] args) {
        launch();
    }
}