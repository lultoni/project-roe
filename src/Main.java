import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(false));
        System.out.println("PossibleTurns: " + possibleTurns.size());
        game.startGame();
        System.out.println("\nClose - [project-roe]");
    }
}