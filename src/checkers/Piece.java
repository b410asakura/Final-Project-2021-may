
package checkers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Piece {
    private boolean empty, color, crown;


    public Piece() {
        empty = true;
        color = false;
        crown = false;
    }

    public Piece(Piece piece) {
        empty = piece.empty;
        color = piece.color;
        crown = piece.crown;
    }


    public boolean isEmpty() {
        return empty;
    }


    public boolean isCrown() {
        return crown;
    }


    public boolean color() {
        return color;
    }


    public void setWhite() {
        empty = false;
        crown = false;
        color = false;
    }

    public void setBlack() {
        empty = false;
        crown = false;
        color = true;
    }


    public void setCrown() {
        crown = true;
    }


    public void setEmpty() {
        empty = true;
    }


    public void draw(GraphicsContext gc, double x, double y, double margin,
                     double unitLength) {
        if (empty)
            return;

        if (color)
            gc.setFill(Color.RED);
        else gc.setFill(Color.BLACK);

        gc.fillOval(x + margin * unitLength, y + margin * unitLength,
                (1 - 2 * margin) * unitLength, (1 - 2 * margin) * unitLength);

        gc.setStroke(Color.WHITE);
        gc.strokeOval(x + margin * unitLength, y + margin * unitLength,
                (1 - 2 * margin) * unitLength, (1 - 2 * margin) * unitLength);

        if (color)
            gc.setStroke(Color.BLACK);
        if (crown)
            gc.strokeOval(x + 2 * margin * unitLength, y + 2 * margin * unitLength,
                    (1 - 4 * margin) * unitLength, (1 - 4 * margin) * unitLength);
    }
}