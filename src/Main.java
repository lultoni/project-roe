import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        Window window = new Window(game);

//        game.doDBT(window, "Player0 Turn:(ST: 5)\n" +
//                "Turn {\n" +
//                "Spells:\n" +
//                "TurnSpell(5, 1, 7, [(1, 6)])\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 5)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(4, 1, 0, 1)\n" +
//                "Move 2:\n" +
//                "Move(5, 0, -1, 1)\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 4)\n" +
//                "Turn {\n" +
//                "Spells:\n" +
//                "TurnSpell(5, 1, 7, [(1, 6)])\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 6)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(2, 1, 0, 1)\n" +
//                "Move 2:\n" +
//                "Move(3, 0, -1, 1)\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 3)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(2, 6, 0, -1)\n" +
//                "Move 2:\n" +
//                "Move(3, 6, 0, -1)\n" +
//                "Move 3:\n" +
//                "Move(2, 7, 1, -1)\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 7)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(2, 2, 1, 0)\n" +
//                "Move 2:\n" +
//                "Move(2, 1, 0, 1)\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 4)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(3, 5, 0, -1)\n" +
//                "Move 2:\n" +
//                "Move(1, 7, 2, -2)\n" +
//                "Spells:\n" +
//                "TurnSpell(0, 3, 5, [(2, 2)])\n" +
//                "}");

//        ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(false));
//        System.out.println("\nPlayer0");
//        for (Piece p: game.getPlayer(false).getPieces()) {
//            System.out.println(p.getType() + "(" + p.getXPos() + ", " + p.getYPos() + ")");
//        }
//        System.out.println("\nPlayer1");
//        for (Piece p: game.getPlayer(true).getPieces()) {
//            System.out.println(p.getType() + "(" + p.getXPos() + ", " + p.getYPos() + ")");
//        }

        System.out.println("\n\n---Starting Game---");
        game.startGame(window);
        System.out.println("\nClose - [project-roe]");
    }
}