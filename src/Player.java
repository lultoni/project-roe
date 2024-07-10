import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Player {
    private int spellTokens;
    private final Piece[] pieces;
    private final boolean isHuman;
    private final Map<String, Double> transpositionTable;

    public Player(Piece[] pieces, boolean isHuman) {
        spellTokens = 5;
        this.pieces = pieces;
        this.isHuman = isHuman;
        transpositionTable = new HashMap<>();
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

            double alpha = Double.NEGATIVE_INFINITY;
            double beta = Double.POSITIVE_INFINITY;

            for (int i = 0; i < possibleTurns.size(); i++) {
                Turn turn = possibleTurns.get(i);
                double score = bestScore;
                if (gameState.generatePositionFEN().equals("2rArErSrWrF1/2rGrGrGrGrG1/8/8/8/8/1bGbGbGbGbG2/1bFbWbSbEbA2 1.0 5 5") && i > 620) {
                    game.executeTurn(turn, game.getPlayer(isMaximizingPlayer), null);
                    game.setTurnCounter(game.getTurnCounter() - 0.5);
                    score = minimax(game, depth - 1, !isMaximizingPlayer, alpha, beta);
                } else if (gameState.generatePositionFEN().equals("2rArErSrWrF1/2rGrGrGrGrG1/8/8/8/8/1bGbGbGbGbG2/1bFbWbSbEbA2 1.0 5 5") && i <= 284) {
                    score = -222.51;
                } else if (gameState.generatePositionFEN().equals("2rArErSrWrF1/2rGrGrGrGrG1/8/8/8/8/1bGbGbGbGbG2/1bFbWbSbEbA2 1.0 5 5") && i >= 285 && i <= 400) {
                    score = -107.0;
                } else if (gameState.generatePositionFEN().equals("2rArErSrWrF1/2rGrGrGrGrG1/8/8/8/8/1bGbGbGbGbG2/1bFbWbSbEbA2 1.0 5 5") && i >= 400 && i <= 620) {
                    score = -110.5;
                }

                if (isMaximizingPlayer) {
                    if (score > bestScore) {
                        bestScore = score;
                        bestTurns.clear();
                        bestTurns.add(turn);
                    } else if (score == bestScore) {
                        bestTurns.add(turn);
                    }
                    alpha = Math.max(alpha, bestScore);
                } else {
                    if (score < bestScore) {
                        bestScore = score;
                        bestTurns.clear();
                        bestTurns.add(turn);
                    } else if (score == bestScore) {
                        bestTurns.add(turn);
                    }
                    beta = Math.min(beta, bestScore);
                }

                game.loadGameState(gameState);
                System.out.println("(" + String.format("%.3f", ((float) i / possibleTurns.size()) * 100) + "%) - " +
                        (bestScore > 0 ? "\033[32m" : bestScore < 0 ? "\033[31m" : "\033[0m") + Math.abs(bestScore) + "\033[0m" + " - " +
                        (score > 0 ? "\033[32m" : score < 0 ? "\033[31m" : "\033[0m") + Math.abs(score) + "\033[0m" + " - index: " + i +
                        " - (" + getCoolString(turn) + ")");

                if (beta <= alpha) {
                    System.out.println("Alpha-Beta Pruning stopped the continuation");
                    break;
                }
            }

            System.out.println("best_turns len " + bestTurns.size() + " - (" + bestScore + ")");
            System.out.println("time for d" + depth + ": " + (System.currentTimeMillis() - startTime) + " ms");
            Random random = new Random();
            return bestTurns.get(random.nextInt(bestTurns.size()));
        }
    }

    private String getCoolString(Turn turn) {
        String out = "";
        if (turn.move1 != null) out += "m";
        if (turn.move2 != null) out += "m";
        if (turn.move3 != null) out += "m";
        if (turn.move1 != null && turn.move2 != null &&turn.move3 != null) out += "x";
        if (turn.attack != null) {
            out += "a";
        } else {
            out += "x";
        }
        if (turn.spells != null) {
            out += "s";
        } else {
            out += "x";
        }
        return out;
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
