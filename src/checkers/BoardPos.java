package checkers;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.abs;

public class BoardPos {
    private int x, y;
    public List<BoardPos> route;


    public BoardPos(int _x, int _y) {
        x = _x;
        y = _y;
    }


    public BoardPos(BoardPos pos) {
        x = pos.x;
        y = pos.y;
        if (pos.route != null)
            route = new ArrayList<>(pos.route);
    }


    public void addToRoute(BoardPos step) {
        if (route == null)
            route = new ArrayList<>();
        route.add(step);
    }


    public int routeLen() {
        if (route == null)
            return 0;
        else return route.size();
    }


    public List<BoardPos> getRoute() {
        if (route == null)
            return new ArrayList<>();
        return route;
    }


    public void setRoute(List<BoardPos> _route) {
        route = new ArrayList<>(_route);
    }


    public int getX() {
        return x;
    }


    public int getY() {
        return y;
    }


    public BoardPos getRouteLast() {
        return route.get(route.size() - 1);
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BoardPos))
            return false;
        BoardPos oo = (BoardPos) o;
        return oo.x == x && oo.y == y;
    }

    public boolean inBounds(int sideCount) {
        return x >= 0 && y >= 0 && x < sideCount && y < sideCount;
    }


    public boolean isNextTo(BoardPos pos) {
        return abs(x - pos.x) == 1 && abs(y - pos.y) == 1;
    }


    public BoardPos add(BoardPos pos) {
        return new BoardPos(x + pos.x, y + pos.y);
    }


    public BoardPos add(int _x, int _y) {
        BoardPos retVal = new BoardPos(x + _x, y + _y);
        if (route != null)
            retVal.route = new ArrayList<>(route);
        return retVal;
    }


    public BoardPos avg(BoardPos pos) {
        return new BoardPos((x + pos.x) / 2, (y + pos.y) / 2);
    }
}
