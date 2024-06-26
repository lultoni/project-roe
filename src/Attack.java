public class Attack {
    int xFrom;
    int yFrom;
    int xChange;
    int yChange;

    public Attack(int xFrom, int yFrom, int xChange, int yChange) {
        this.xFrom = xFrom;
        this.yFrom = yFrom;
        this.xChange = xChange;
        this.yChange = yChange;
    }

    public void print() {
        System.out.println("Attack(" + xFrom + ", " + yFrom + ", " + xChange + ", " + yChange + ")");
    }
}
