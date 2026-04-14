import javafx.application.Application;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {

        final int[] score = {0};

        // Create a simple empty Layout
        Pane root = new Pane();

        root.setFocusTraversable(true);

        // Create paddle (rectangle)
        Rectangle paddle = new Rectangle();

        // Set side of paddle
        paddle.setWidth(100);
        paddle.setHeight(10);

        // Position paddle (center bottom)
        paddle.setX(250); //Horizontal position
        paddle.setY(350); // vertical position

        // Set paddle color
        paddle.setFill(Color.BLUE);

        // Add paddle to the screen
        root.getChildren().add(paddle);

        // Create a scene with width 600 and height 400
        Scene scene = new Scene(root, 600, 400);

        // Add keyboard controls
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                if (paddle.getX() > 0) {
                    paddle.setX(paddle.getX() - 20);
                }
            } else if (event.getCode() == KeyCode.RIGHT) {
                if (paddle.getX() < 600 - paddle.getWidth()) {
                    paddle.setX(paddle.getX() + 20);
                }
            }
        });

        // Create ball
        Circle ball = new Circle();

        // Set radius (size)
        ball.setRadius(8);

        // Set position (center of screen)
        ball.setCenterX(300);
        ball.setCenterY(200);

        // Set Color
        ball.setFill(Color.RED);

        // Add ball to screen
        root.getChildren().add(ball);

        // Game over text
        Text gameOverText = new Text(200, 200, "GAME OVER");
        gameOverText.setStyle("-fx-font-size: 30px; -fx-fill: red;");
        gameOverText.setVisible(false);     // hidden initially

        root.getChildren().add(gameOverText);

        // You win text
        Text winText = new Text(200, 200, "YOU WIN!!!");
        winText.setStyle("-fx-font-size: 30px; -fx-fill: green;");
        winText.setVisible(false);     // hidden initially

        root.getChildren().add(winText);

        // Create score display
        Text scoreText = new Text(10, 20, "Score: 0");
        scoreText.setStyle("-fx-font-size: 18px;");

        root.getChildren().add(scoreText);

        List<Rectangle> bricks = new ArrayList<>();

        // Create multiple bricks
        for (int row = 0; row < 3; row++){
            for (int col = 0; col < 5; col++){

                Rectangle brick = new Rectangle();

                brick.setWidth(60);
                brick.setHeight(20);

                brick.setX(60 + col * 100);
                brick.setY(50 + row * 40);

                brick.setFill(Color.GREEN);

                root.getChildren().add(brick);

                bricks.add(brick);      // store in list
            }
        }

        // Ball movement speed
        final double[] dx = {2}; // Horizontal speed
        final double[] dy = {2}; // vertical speed

        // Set window title
        stage.setTitle("Brick Breaker Game");

        // Set the scene to stage (window)
        stage.setScene(scene);

        // Show the window
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                // Move the ball
                ball.setCenterX(ball.getCenterX() + dx[0]);
                ball.setCenterY(ball.getCenterY() + dy[0]);

                // Bounce of Left and Right walls
                if (ball.getCenterX() <= 0 || ball.getCenterX() >= 600){
                    dx[0] = -dx[0];
                }

                // Bounce off top wall
                if(ball.getCenterY() < 0){
                    dy[0] = -dy[0];
                }

                // Check collision with paddle
                if (ball.getBoundsInParent().intersects(paddle.getBoundsInParent())){

                    // Calculate hit position
                    double paddleCenter = paddle.getX() + paddle.getWidth() / 2;
                    double ballPosition = ball.getCenterX();

                    double offset = ballPosition - paddleCenter;

                    // Normalize offset (-1 to 1 range)
                    double normalized = offset / (paddle.getWidth() / 2);

                    // Set fixed speed
                    double speed = 3;

                    dx[0] = normalized * speed;     // horizontal direction
                    dy[0] = -Math.abs(speed);       // always go upward
                }

                // Check if ball goes below screen (Game Over)
                if (ball.getCenterY() >= 400) {
                    gameOverText.setVisible(true);
                    stop();     // stop the animation
                }

               // Check collision with bricks
                for (Rectangle brick : bricks) {
                    if (ball.getBoundsInParent().intersects(brick.getBoundsInParent())) {

                        dy[0] = -dy[0];     // bounce

                        root.getChildren().remove(brick);       // remove from screen
                        bricks.remove(brick);       // remove from list

                        score[0] += 10;     // increase score
                        scoreText.setText("Score: " + score[0]);    // update display

                        break;      // stop checking further
                    }
                }

                // Check win condition
                if (bricks.isEmpty()) {
                    winText.setVisible(true);
                    stop();     // stop the game
                }
            }
        };

        // Start animation
        timer.start();
    }

    public static void main(String[] args) {
        launch();
    }
}