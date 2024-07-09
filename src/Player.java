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

    public Turn fetchTurn(Game game) {
        if (isHuman) {
            while (game.isWaitingForHuman()) {
                Thread.onSpinWait();
            }
            return game.getHumanTurn();
        } else {
            double bestScore = (game.getTurnCounter() % 1 == 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(game.getTurnCounter() % 1 != 0));
            ArrayList<Turn> bestTurns = new ArrayList<>();
            Game gameState = game.copyGameState();

            System.out.println("---pos_turns len " + possibleTurns.size());
            System.out.println("cur turn " + ((game.getTurnCounter() % 1 == 0) ? "0" : "1"));

            for (Turn turn : possibleTurns) {
                game.executeTurn(turn, game.getPlayer(game.getTurnCounter() % 1 != 0), null);
                game.setTurnCounter(game.getTurnCounter() - 0.5);
                double score = (game.getTurnCounter() % 1 == 0) ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;

                try {
                    score = game.evaluate();
                } catch (Exception e) {
                    System.out.println("I am the little bitch that thinks it's funny to throw errors:");
                    turn.print();
                }

                if (game.getTurnCounter() % 1 == 0) {
                    if (score > bestScore) {
                        bestScore = score;
                        bestTurns.clear();
                        bestTurns.add(turn);
                    } else if (score == bestScore) {
                        bestTurns.add(turn);
                    }
                } else {
                    if (score < bestScore) { // TODO no way this ai ain't retarded bruh, like win the game!!!
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

    public boolean equals(Player player) {
        boolean baseInfo = isHuman == player.getIsHuman() && spellTokens == player.getSpellTokens();
        boolean piecesSame = true;
        for (int i = 0; i < pieces.length; i++) {
            boolean b1 = pieces[i].getType() == player.getPieces()[i].getType();
            boolean b2 = pieces[i].getPlayer() == player.getPieces()[i].getPlayer();
            boolean b3 = pieces[i].getXPos() == player.getPieces()[i].getXPos();
            boolean b4 = pieces[i].getYPos() == player.getPieces()[i].getYPos();
            boolean b5 = pieces[i].hasMoved() == player.getPieces()[i].hasMoved();
            boolean b6 = pieces[i].getAttackProtectedTimer() == player.getPieces()[i].getAttackProtectedTimer();
            boolean b7 = pieces[i].getSpellProtectedTimer() == player.getPieces()[i].getSpellProtectedTimer();
            boolean b8 = pieces[i].getSpellReflectionTimer() == player.getPieces()[i].getSpellReflectionTimer();
            boolean b9 = pieces[i].getOvergrownTimer() == player.getPieces()[i].getOvergrownTimer();
            piecesSame = piecesSame && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9;
        }
        return baseInfo && piecesSame;
    }
}
