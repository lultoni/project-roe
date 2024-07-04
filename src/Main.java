import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        Window window = new Window(game);

//        game.doDBT(window, "Player0 Turn:(ST: 5)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(3, 6, -1, -1)\n" +
//                "Move 2:\n" +
//                "Move(2, 7, 1, -1)\n" +
//                "Spells:\n" +
//                "TurnSpell(14, 3, 7, [(4, 7), (2, 5)])\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 5)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(2, 0, -1, 1)\n" +
//                "Move 2:\n" +
//                "Move(6, 1, -1, 1)\n" +
//                "Spells:\n" +
//                "TurnSpell(13, 1, 1, [(0, 2)])\n" +
//                "TurnSpell(3, 0, 2, [(2, 5)])\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 2)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(1, 7, 2, -2)\n" +
//                "Move 2:\n" +
//                "Move(3, 6, -2, -1)\n" +
//                "Spells:\n" +
//                "TurnSpell(1, 1, 5, [(0, 2)])\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 1)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(5, 0, 1, 1)\n" +
//                "Move 2:\n" +
//                "Move(6, 0, -1, 0)\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 1)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(1, 5, 2, 1)\n" +
//                "Move 2:\n" +
//                "Move(5, 7, 1, -1)\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 2)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(5, 2, 0, 1)\n" +
//                "Move 2:\n" +
//                "Move(6, 1, 0, 1)\n" +
//                "Spells:\n" +
//                "TurnSpell(1, 6, 2, [(3, 5)])\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 2)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(1, 6, 1, 0)\n" +
//                "Move 2:\n" +
//                "Move(6, 6, 1, -1)\n" +
//                "Spells:\n" +
//                "TurnSpell(1, 3, 6, [(5, 3)])\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 1)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(6, 2, 1, 2)\n" +
//                "Attack:\n" +
//                "Attack(7, 4, 0, 1)\n" +
//                "}");
//        game.doDBT(window, "Player0 Turn:(ST: 1)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(5, 6, 1, -1)\n" +
//                "Attack:\n" +
//                "Attack(6, 5, 1, 0)\n" +
//                "}");
//        game.doDBT(window, "Player1 Turn: (ST: 3)\n" +
//                "Turn {\n" +
//                "Move 1:\n" +
//                "Move(5, 1, 1, 1)\n" +
//                "Move 2:\n" +
//                "Move(5, 0, -1, 2)\n" +
//                "Spells:\n" +
//                "TurnSpell(0, 4, 2, [(7, 5)])\n" +
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