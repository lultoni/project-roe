import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game(false, false);
        Window window = new Window(game);

//        long startTime = System.currentTimeMillis();
//        ArrayList<Turn> possibleTurnsP0 = game.generatePossibleTurns(game.getPlayer(false));
//        System.out.println("\npossibleTurnsP0: " + possibleTurnsP0.size());
//        System.out.println("time: " + (System.currentTimeMillis() - startTime) + " ms");
//        System.out.println(game.generatePositionFEN());
//        game.printBoardPieces();

        game.startGame(window);
        System.out.println("\nClose - [project-roe]");
        System.exit(0);
    }
}