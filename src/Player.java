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
        } else { // TODO think of how you can create MCTS for this game
            double bestScore = (game.getTurnCounter() % 1 == 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            ArrayList<Turn> bestTurns = new ArrayList<>();
            Game gameState = game.copyGameState();

            System.out.println("---pos_turns len " + possibleTurns.size());
            System.out.println("cur turn " + ((game.getTurnCounter() % 1 == 0) ? "0" : "1"));

            for (Turn turn : possibleTurns) {
                game.executeTurn(turn, game.getPlayer(game.getTurnCounter() % 1 != 0));
                game.setTurnCounter(game.getTurnCounter() - 0.5);
                double score = game.evaluate();

                if (game.getTurnCounter() % 1 == 0) {
                    if (score > bestScore) {
                        bestScore = score;
                        bestTurns.clear();
                        bestTurns.add(turn);
                    } else if (score == bestScore) {
                        bestTurns.add(turn);
                    }
                } else {
                    if (score < bestScore) {
                        bestScore = score;
                        bestTurns.clear();
                        bestTurns.add(turn);
                    } else if (score == bestScore) {
                        bestTurns.add(turn);
                    }
                }
                game.loadGameState(gameState);
            }

            System.out.println("best_turns len " + bestTurns.size() + " - (" + bestScore + ")");

            Random random = new Random();
            return bestTurns.get(random.nextInt(bestTurns.size()));
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
