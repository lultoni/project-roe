public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game(true, false);
        Window window = new Window(game);

        game.doDBT(window, "Player0 Turn:(ST: 5)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(2, 6, 0, -1)\n" +
                "Move 2:\n" +
                "Move(3, 6, 0, -1)\n" +
                "Move 3:\n" +
                "Move(2, 7, 1, -1)\n" +
                "}");
        game.doDBT(window, "Player1 Turn: (ST: 5)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(2, 0, -1, 1)\n" +
                "Move 2:\n" +
                "Move(5, 1, 0, 1)\n" +
                "Move 3:\n" +
                "Move(5, 0, 0, 1)\n" +
                "}");
        game.doDBT(window, "Player0 Turn:(ST: 6)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(5, 6, 0, -1)\n" +
                "Move 2:\n" +
                "Move(5, 7, 1, -1)\n" +
                "Move 3:\n" +
                "Move(4, 7, 1, -1)\n" +
                "}");
        game.doDBT(window, "Player1 Turn: (ST: 6)\n" +
                "Turn {\n" +
                "Move 1:\n" +
                "Move(1, 1, -1, 1)\n" +
                "Move 2:\n" +
                "Move(2, 1, -1, 1)\n" +
                "Move 3:\n" +
                "Move(3, 0, -1, 1)\n" +
                "}");

        game.startGame(window);
        System.out.println("\nClose - [project-roe]");
    }
}