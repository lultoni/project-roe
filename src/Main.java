import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game(false, false);
        Window window = new Window(game);

        game.doDBT(window, "Player0 Turn:(ST: 5)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(5, 7, 1, -1)\n" +
                "}");
        game.doDBT(window, "Player1 Turn: (ST: 5)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(2, 0, -1, 0)\n" +
                "Move 2:\n" +
                "Move(6, 1, -1, 1)\n" +
                "Move 3:\n" +
                "Move(5, 0, 1, 1)\n" +
                "}");
        game.doDBT(window, "Player0 Turn:(ST: 6)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(1, 7, -1, -2)\n" +
                "Move 2:\n" +
                "Move(2, 7, -1, 0)\n" +
                "Move 3:\n" +
                "Move(6, 6, -1, -1)\n" +
                "Spells:\n" +
                "TurnSpell(13, 5, 5, [(4, 4)])\n" +
                "TurnSpell(13, 4, 4, [(5, 3)])\n" +
                "}");

//        long startTime = System.currentTimeMillis();
//        ArrayList<Turn> possibleTurnsP0 = game.generatePossibleTurns(game.getPlayer(false));
//        System.out.println("\npossibleTurnsP0: " + possibleTurnsP0.size());
//        System.out.println("time: " + (System.currentTimeMillis() - startTime) + " ms");
//        System.out.println(game.generatePositionFEN());
//        game.printBoardPieces();

        game.startGame(window);
        System.out.println("\nClose - [project-roe]");
    }
}