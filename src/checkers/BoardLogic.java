
package checkers;

import java.util.*;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class BoardLogic {
    private final double pieceMargin, startX, startY, sideLength, unitLength;
    private Board board;
    private List<BoardPos> legalPos;
    boolean lastColor, gameOver, opponentSet;


    public BoardLogic(double _startX, double _startY, double _sideLength,
                      double _pieceMargin, int sideCount, int startCount) {
        startX = _startX;
        startY = _startY;
        sideLength = _sideLength;
        pieceMargin = _pieceMargin;
        unitLength = _sideLength / sideCount;
        board = new Board(sideCount, startCount);
        legalPos = new ArrayList<>();
        lastColor = true;
        gameOver = false;
        opponentSet = false;
    }

    public BoardLogic(BoardLogic boardLogic) {
        startX = boardLogic.startX;
        startY = boardLogic.startY;
        sideLength = boardLogic.sideLength;
        pieceMargin = boardLogic.pieceMargin;
        unitLength = boardLogic.unitLength;
        board = new Board(boardLogic.board);
        legalPos = new ArrayList<>(boardLogic.legalPos);
        lastColor = boardLogic.lastColor;
        gameOver = boardLogic.gameOver;
        opponentSet = boardLogic.opponentSet;
    }


    public void update(BoardLogic boardLogic) {
        board = new Board(boardLogic.board);
        legalPos = new ArrayList<>(boardLogic.legalPos);
        lastColor = boardLogic.lastColor;
        gameOver = boardLogic.gameOver;
        opponentSet = boardLogic.opponentSet;
    }


    public void reset() {
        board.reset();
        lastColor = true;
        gameOver = false;
        opponentSet = false;
    }


    public void highlightMoves(BoardPos from) {
        List<BoardPos> longest = longestAvailableMoves(2, !lastColor);

        if (longest.isEmpty() && from.inBounds(board.side()) &&
                !board.get(from).isEmpty() && board.get(from).color() != lastColor)
            legalPos = getMoves(from);
        else for (BoardPos strike : longest)
            legalPos.addAll(getMoves(strike));
    }


    public void attemptMove(BoardPos to) {
        if (legalPos.contains(to)) {
            lastColor = !lastColor;
            board.set(to, board.get(legalPos.get(legalPos.indexOf(to)).getRouteLast()));
            for (BoardPos step : legalPos.get(legalPos.indexOf(to)).getRoute())
                board.get(step).setEmpty();
            findCrown();
        }

        legalPos.clear();
    }


    public void draw(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREY);
        for (int i = 0; i < board.side(); i++)
            for (int j = (i % 2 == 0) ? 1 : 0; j < board.side(); j += 2)
                gc.fillRect(startX + j * unitLength, startY + i * unitLength,
                        unitLength, unitLength);

        gc.setStroke(Color.BLACK);
        gc.strokeRect(startX, startY, sideLength, sideLength);

        for (BoardPos pos : legalPos) {
            gc.setFill(Color.ORANGE);
            gc.fillRect(startX + pos.getX() * unitLength,
                    startY + pos.getY() * unitLength, unitLength, unitLength);
            gc.setFill(Color.LIGHTYELLOW);
            if (pos.route != null)
                for (BoardPos step : pos.route)
                    gc.fillRect(startX + step.getX() * unitLength,
                            startY + step.getY() * unitLength, unitLength, unitLength);
        }

        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                board.get(i, j).draw(gc, startX + i * unitLength,
                        startY + j * unitLength, pieceMargin, unitLength);
    }


    public String message() {
        if (!longestAvailableMoves(2, !lastColor).isEmpty())
            return "Strike available";
        else if (isGameOver())
            return "Game over! Click somewhere to continue";
        else return "Turn: " + (lastColor ? "Black" : "Red");
    }


    public boolean someLegalPos() {
        return !legalPos.isEmpty();
    }


    public boolean isGameOverDelayed() {
        return gameOver;
    }

    public boolean getLastColor() {
        return lastColor;
    }

    public BoardPos decodeMouse(double mouseX, double mouseY) {
        if (mouseX > startX && mouseY > startY && mouseX < startX + sideLength &&
                mouseY < startY + sideLength) // range check
            return new BoardPos((int) ((mouseX - startX) / unitLength),
                    (int) ((mouseY - startY) / unitLength));
        else return null;
    }


    public List<BoardPos> getMoves(BoardPos from) {
        List<BoardPos> result;

        if (board.get(from).isCrown())
            result = getStrikesCrown(from);
        else result = getStrikes(from);

        final int[] shifts = {-1, 1};
        if (result.isEmpty() && !board.get(from).isEmpty()) {
            if (board.get(from).isCrown())
                for (int shiftX : shifts)
                    for (int shiftY : shifts) {
                        BoardPos to = from.add(shiftX, shiftY);
                        while (to.inBounds(board.side()) && board.get(to).isEmpty()) {
                            result.add(to);
                            to = to.add(shiftX, shiftY);
                        }
                    }
            else for (int shift : shifts) { // add adjacent empty positions
                BoardPos move = from.add(new BoardPos(shift,
                        board.get(from).color() ? 1 : -1));
                if (board.get(move) != null && board.get(move).isEmpty())
                    result.add(new BoardPos(move));
            }
        }

        for (BoardPos pos : result)
            pos.addToRoute(new BoardPos(from));

        return result;
    }


    private List<BoardPos> getStrikes(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>();
        search.add(from);
        List<BoardPos> result = new ArrayList<>();
        final int[] offsets = {-2, 2};

        while (!search.isEmpty()) {
            boolean finalPos = true;
            for (int offX : offsets)
                for (int offY : offsets) {
                    BoardPos to = new BoardPos(search.peek().getX() + offX,
                            search.peek().getY() + offY);
                    to.setRoute(search.peek().getRoute());


                    if (to.inBounds(board.side()) && board.get(to).isEmpty() &&
                            !board.get(to.avg(search.peek())).isEmpty() &&
                            board.get(from).color() !=
                                    board.get(to.avg(search.peek())).color() &&
                            !to.getRoute().contains(to.avg(search.peek()))) {
                        to.addToRoute(new BoardPos(to.avg(search.peek())));
                        search.add(to);
                        finalPos = false;
                    }
                }

            if (finalPos && !search.peek().equals(from))
                result.add(search.peek());

            search.poll();
        }

        return filterShorter(result);
    }


    private List<BoardPos> getStrikesCrown(BoardPos from) {
        Queue<BoardPos> search = new LinkedList<>();
        search.add(from);
        List<BoardPos> result = new ArrayList<>();
        final int[] direction = {-1, 1};

        while (!search.isEmpty()) {
            boolean finalPos = true;
            for (int dirX : direction)
                for (int dirY : direction) {
                    BoardPos pos = search.peek().add(dirX, dirY);
                    BoardPos strike = null;
                    pos.setRoute(new ArrayList<>(search.peek().getRoute()));

                    while (pos.inBounds(board.side()) &&
                            (board.get(pos).isEmpty() ||
                                    (pos.add(dirX, dirY).inBounds(board.side()) &&
                                            board.get(pos.add(dirX, dirY)).isEmpty() &&
                                            board.get(from).color() != board.get(pos).color()))) {
                        if (!board.get(pos).isEmpty() && board.get(from).color()
                                != board.get(pos).color() && !pos.getRoute().contains(pos) &&
                                pos.add(dirX, dirY).inBounds(board.side()) &&
                                board.get(pos.add(dirX, dirY)).isEmpty()) {
                            strike = new BoardPos(pos);
                            finalPos = false;
                            pos = pos.add(dirX, dirY);

                            pos.addToRoute(strike);
                        }
                        if (strike != null && !pos.equals(strike))
                            search.add(pos);

                        pos = pos.add(dirX, dirY);
                    }
                }

            if (finalPos && !search.peek().equals(from))
                result.add(search.peek());

            search.poll();
        }

        return filterShorter(result);
    }


    public List<BoardPos> longestAvailableMoves(int minDepth, boolean color) {
        List<BoardPos> result = new ArrayList<>();

        for (int i = 0; i < board.side(); i++)
            for (int j = 0; j < board.side(); j++)
                if (!board.get(i, j).isEmpty() &&
                        board.get(i, j).color() == color) {
                    List<BoardPos> _legalPos = getMoves(new BoardPos(i, j));
                    if (!_legalPos.isEmpty()) {
                        if (_legalPos.get(0).routeLen() > minDepth) {
                            result.clear();
                            minDepth = _legalPos.get(0).routeLen();
                        }
                        if (_legalPos.get(0).routeLen() == minDepth)
                            result.add(new BoardPos(i, j));
                    }
                }

        return result;
    }


    private void findCrown() {
        for (int i = 0; i < board.side(); i++) {
            if (!board.get(i, 0).isEmpty() && !board.get(i, 0).color())
                board.get(i, 0).setCrown();
            if (!board.get(i, board.side() - 1).isEmpty() &&
                    board.get(i, board.side() - 1).color())
                board.get(i, board.side() - 1).setCrown();
        }
    }


    private boolean isGameOver() {

        gameOver = longestAvailableMoves(1, true).isEmpty() ||
                longestAvailableMoves(1, false).isEmpty();
        return gameOver;
    }


    private List<BoardPos> filterShorter(List<BoardPos> route) {
        int maxDepth = route.isEmpty() ? 0 : route.get(route.size() - 1).routeLen();
        Iterator<BoardPos> it = route.iterator();

        while (it.hasNext()) {
            BoardPos pos = it.next();
            if (pos.routeLen() != maxDepth)
                it.remove();
        }

        return route;
    }

    public Board getBoard() {
        return board;
    }

    public boolean isOpponentSet() {
        return opponentSet;
    }

    public boolean turn() {
        return !lastColor;
    }

    public void setOpponent() {
        opponentSet = true;
    }
}
