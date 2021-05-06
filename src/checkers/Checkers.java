
package checkers;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Checkers extends Application {

    private void drawMessage(GraphicsContext gc, String str,
                             double x, double y, double size) {
        gc.setFont(new Font("Arial", size));
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillText(str, x, y);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Checkers");

        primaryStage.setMaximized(true);

        Group root = new Group();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);

        Canvas canvas = new Canvas(1000, 1000);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        BoardLogic board = new BoardLogic(200.0, 100.0, 800.0, 0.1, 8, 3);
        board.draw(gc);
        drawMessage(gc, "Press 'space' to START!",
                50.0, 40.0, 32.0);

        primaryScene.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent e) {
                        if (!board.isOpponentSet())
                            return;
                        if (board.isGameOverDelayed())
                            board.reset();

                        else if (board.someLegalPos())
                            board.attemptMove(board.decodeMouse(e.getX(), e.getY()));
                        else
                            board.highlightMoves(board.decodeMouse(e.getX(), e.getY()));

                        gc.clearRect(0, 0, gc.getCanvas().getWidth(),
                                gc.getCanvas().getHeight());
                        board.draw(gc);
                        if (board.isOpponentSet())
                            drawMessage(gc, board.message(), 50.0, 40.0, 32.0);
                        else
                            drawMessage(gc, "H-human player",
                                    50.0, 40.0, 22.0);
                    }
                });

        primaryScene.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent event) {
                        if (!board.isOpponentSet() && event.getText().equals(" ")) {
                            board.setOpponent();
                        }

                        gc.clearRect(0, 0, gc.getCanvas().getWidth(),
                                gc.getCanvas().getHeight());
                        board.draw(gc);
                        if (board.isOpponentSet())
                            drawMessage(gc, board.message(), 50.0, 40.0, 32.0);
                    }
                });

        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
