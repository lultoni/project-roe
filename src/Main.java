public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        Window window = new Window(game);

        System.out.println("\n\n---Starting Game---");
        game.startGame(window);
        System.out.println("\nClose - [project-roe]");
    }
}