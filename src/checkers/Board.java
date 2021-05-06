package checkers;


public class Board {
    private Piece[][] pieces;
    private int startCount, sideCount;


    public Board(int _sideCount, int _startCount) {
        startCount = _startCount;
        sideCount = _sideCount;

        pieces = new Piece[sideCount][sideCount];
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j] = new Piece();

        for (int j = 0; j < startCount; j++)
            for (int i = (j % 2 == 0) ? 1 : 0; i < sideCount; i += 2) {
                pieces[i][j].setBlack();
                pieces[sideCount - 1 - i][sideCount - 1 - j].setWhite();
            }
    }

    public Board(Board board) {
        startCount = board.startCount;
        sideCount = board.sideCount;

        pieces = new Piece[sideCount][sideCount];
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j] = new Piece(board.pieces[i][j]);
    }


    public void reset() {
        for (int i = 0; i < pieces.length; i++)
            for (int j = 0; j < pieces[i].length; j++)
                pieces[i][j].setEmpty();

        for (int j = 0; j < startCount; j++)
            for (int i = (j % 2 == 0) ? 1 : 0; i < pieces.length; i += 2) {
                pieces[i][j].setBlack();
                pieces[pieces.length - 1 - i][pieces.length - 1 - j].setWhite();
            }
    }


    public Piece get(BoardPos pos) {
        if (pos.inBounds(pieces.length))
            return pieces[pos.getX()][pos.getY()];
        else return null;
    }


    public Piece get(int x, int y) {
        return get(new BoardPos(x, y));
    }


    public int side() {
        return pieces.length;
    }


    public void set(BoardPos pos, Piece piece) {
        pieces[pos.getX()][pos.getY()] = new Piece(piece);
    }
}
