import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player {
    private int spellTokens;
    private final Piece[] pieces;
    private final boolean isHuman;
    private final Map<String, Double> transpositionTable = new HashMap<>();

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
            int depth = 2;
            boolean isMaximizingPlayer = game.getTurnCounter() % 1 == 0;
            double bestScore = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(isMaximizingPlayer));
            ArrayList<Turn> bestTurns = new ArrayList<>();
            Game gameState = game.copyGameState();
            long startTime = System.currentTimeMillis();

            System.out.println("---pos_turns len " + possibleTurns.size());
            System.out.println("cur turn " + (isMaximizingPlayer ? "0" : "1"));

            for (int i = 0; i < possibleTurns.size(); i++) {
                Turn turn = possibleTurns.get(i);
                game.executeTurn(turn, game.getPlayer(isMaximizingPlayer), null);
                game.setTurnCounter(game.getTurnCounter() - 0.5);

                double score = minimax(game, depth - 1, !isMaximizingPlayer, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

                if (isMaximizingPlayer) {
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
                System.out.println("(" + String.format("%.2f", ((float) i / possibleTurns.size()) * 100) + "%) - " + bestScore);
            }

            System.out.println("best_turns len " + bestTurns.size() + " - (" + bestScore + ")");
            System.out.println("time for d" + depth + ": " + (System.currentTimeMillis() - startTime) + " ms");
            Random random = new Random();
            return bestTurns.get(random.nextInt(bestTurns.size()));
        }
    }

    private double minimax(Game game, int depth, boolean isMaximizingPlayer, double alpha, double beta) {
        String fen = game.generatePositionFEN();
        if (transpositionTable.containsKey(fen)) {
            return transpositionTable.get(fen);
        }

        if (depth == 0 || game.isGameOver() != 2) {
            double evaluation = game.evaluate();
            transpositionTable.put(fen, evaluation);
            return evaluation;
        }

        ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(isMaximizingPlayer));
        double bestScore = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Game gameState = game.copyGameState();

        for (Turn turn : possibleTurns) {
            game.executeTurn(turn, game.getPlayer(isMaximizingPlayer), null);
            game.setTurnCounter(game.getTurnCounter() - 0.5);

            double score = minimax(game, depth - 1, !isMaximizingPlayer, alpha, beta);

            if (isMaximizingPlayer) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, score);
            }

            game.loadGameState(gameState);

            if (beta <= alpha) {
                break;
            }
        }

        transpositionTable.put(fen, bestScore);
        return bestScore;
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
