import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();
        game.printBoardTerrain();
        game.printBoardCoordinates();
        game.printBoardPieces();
        ArrayList<Move> moves = game.generatePossibleMoves(game.getPlayer(false));
        for (Move move: moves) {
            move.print();
        }
        game.doMove(new Move(3, 6, 0, -1));
        game.doMove(new Move(3, 5, 0, -1));
        game.doMove(new Move(3, 4, 0, -1));
        game.printBoardPieces();
        game.doMove(new Move(5, 1, 0, 1));
        game.doMove(new Move(6, 0, -2, 2));
        game.doMove(new Move(2, 1, 1, 1));
        game.printBoardPieces();
        ArrayList<Attack> attacks = game.generatePossibleAttacks(game.getPlayer(false));
        for (Attack attack: attacks) {
            attack.print();
        }
        game.doAttack(new Attack(3, 3, 1, -1));
        game.printBoardPieces();
        game.doAttack(new Attack(3, 3, 1, -1));
        game.printBoardPieces();
        game.doAttack(new Attack(3, 3, 1, -1));
        game.printBoardPieces();
        game.doAttack(new Attack(3, 3, 1, -1));
        game.printBoardPieces();
        System.out.println("\nClose - [project-roe]");
    }
}