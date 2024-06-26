public class Move {
    int xFrom;
    int yFrom;
    int xChange;
    int yChange;

    public Move(int xFrom, int yFrom, int xChange, int yChange) {
        this.xFrom = xFrom;
        this.yFrom = yFrom;
        this.xChange = xChange;
        this.yChange = yChange;
    }

    public void print() {
        System.out.println("Move(" + xFrom + ", " + yFrom + ", " + xChange + ", " + yChange + ")");
    }
}
