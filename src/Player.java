import java.util.ArrayList;
import java.util.Random;

public class Player {
    private int spellTokens;
    private final Piece[] pieces;
    private final boolean isHuman;

    public Player(Piece[] pieces, boolean isHuman) {
        spellTokens = 5;
        this.pieces = pieces;
        this.isHuman = isHuman;
    }

    public Piece[] getPieces() {
        return pieces;
    }

    public Turn fetchTurn(ArrayList<Turn> possibleTurns, Game game) {
        if (isHuman) {
            while (game.isWaitingForHuman()) {
                Thread.onSpinWait();
            }
            return game.getHumanTurn();
        } else {
            MCTS mcts = new MCTS();
            System.out.println("\nthinking for (expected) " + mcts.TIME_LIMIT + " ms");
            boolean is_p0 = game.getTurnCounter() % 1 != 0;
            Turn bestTurn = mcts.findBestMove(game, is_p0);
            System.out.println("games_simulated: " + mcts.games_simulated);
            return bestTurn;
        }
    }

    public boolean getIsHuman() {
        return isHuman;
    }

    public int getSpellTokens() {
        return spellTokens;
    }

    public void setSpellTokens(int spellTokens) {
        this.spellTokens = spellTokens;
    }
}
