import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        game.printBoardTerrain();
        game.printBoardCoordinates();
        game.printBoardPieces(false);
        game.printBoardPieces(true);
        ArrayList<Move> moves = game.generatePossibleMoves(game.getPlayer(false));
        for (Move move: moves) {
            move.print();
        }
        System.out.println("\nClose - [project-roe]");
    }
}