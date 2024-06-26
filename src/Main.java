public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        game.printBoardTerrain();
        game.printBoardCoordinates();
        game.printBoardPieces(false);
        game.printBoardPieces(true);
        System.out.println("\nClose - [project-roe]");
    }
}