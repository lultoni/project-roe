import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();

        game.printBoardPieces();
        ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(false));
        System.out.println("PossibleTurns: " + possibleTurns.size());

        System.out.println("\n\n---Starting Game---");
        game.startGame(false);
        System.out.println("\nClose - [project-roe]");
    }
}