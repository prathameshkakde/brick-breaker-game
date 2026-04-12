import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class GameApplication extends Application {

    @Override
    public void start(Stage stage) {
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
            if (event.getCode() == KeyCode.LEFT){
                paddle.setX(paddle.getX() - 20); // move Left
            } else if (event.getCode() == KeyCode.RIGHT){
                paddle.setX(paddle.getX() + 20); // move Right
            }
        });

        // Set window title
        stage.setTitle("Brick Breaker Game");

        // Set the scene to stage (window)
        stage.setScene(scene);

        // Show the window
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}