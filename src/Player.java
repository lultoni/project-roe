import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

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
            long startTime = System.currentTimeMillis();
            System.out.println("---start gen");
            int depth = 1;
            boolean isMaximizingPlayer = !pieces[0].getPlayer();
            double bestScore = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            ArrayList<Turn> possibleTurns = game.generatePossibleTurns(game.getPlayer(!isMaximizingPlayer));
            ArrayList<Turn> bestTurns = new ArrayList<>();
            Game gameState = game.copyGameState();

            System.out.println("---pos_turns len " + numberSplit(possibleTurns.size()));
            System.out.println("---time gen: " + timerString(System.currentTimeMillis() - startTime));
            System.out.println("cur turn " + (isMaximizingPlayer ? "0" : "1"));
            startTime = System.currentTimeMillis();

            double currentEval = game.evaluate();
            double alpha = isMaximizingPlayer ? Double.NEGATIVE_INFINITY : currentEval;
            double beta = isMaximizingPlayer ? currentEval : Double.POSITIVE_INFINITY;
            System.out.println("alpha (" + alpha + ") - beta (" + beta + ")");

            ArrayList<Double> initialScores = new ArrayList<>();
            for (Turn turn : possibleTurns) {
                game.executeTurn(turn, game.getPlayer(!isMaximizingPlayer), null);
                game.setTurnCounter(game.getTurnCounter() - 0.5);
                initialScores.add(game.evaluate());
                game.loadGameState(gameState);
            }
            System.out.println("done with evaluating");

            if (depth > 1) {
                iterativeQuickSort(possibleTurns, initialScores, isMaximizingPlayer);
                System.out.println("done with sorting");
            }

            for (int i = 0; i < possibleTurns.size(); i++) {
                long startTime2 = System.currentTimeMillis();
                Turn turn = possibleTurns.get(i);
                if (depth > 1) {
                    game.executeTurn(turn, game.getPlayer(!isMaximizingPlayer), null);
                    game.setTurnCounter(game.getTurnCounter() - 0.5);
                }

                double score = (depth > 1) ? minimax(game, depth - 1, !isMaximizingPlayer, alpha, beta) : initialScores.get(i);

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
                if (depth > 1) System.out.println("(" + String.format("%.3f", ((float) i / possibleTurns.size()) * 100) + "%) - " +
                        (bestScore > 0 ? "\033[32m" : bestScore < 0 ? "\033[31m" : "\033[0m") + Math.abs(bestScore) + "\033[0m" + " - " +
                        (score > 0 ? "\033[32m" : score < 0 ? "\033[31m" : "\033[0m") + Math.abs(score) + "\033[0m" + " - index: " + i +
                        " - (" + getCoolString(turn) + ") - " + timerString(System.currentTimeMillis() - startTime2));

//                if (beta <= alpha) {
//                    System.out.println("Alpha-Beta Pruning stopped the continuation");
//                    break;
//                }
            }

            System.out.println("best_turns len " + bestTurns.size() + " - (" + bestScore + ")");
            System.out.println("time for d" + depth + ": " + timerString(System.currentTimeMillis() - startTime));
            Random random = new Random();
            return bestTurns.get(random.nextInt(bestTurns.size()));
        }
    }

    private String timerString(long number) {
        long totalSeconds = number / 1000;
        long minutes = totalSeconds / 60;
        double seconds = (number % 60000) / 1000.0;

        return String.format(Locale.US, "<%d min, %.3f s>", minutes, seconds);
    }

    private String numberSplit(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator('_');
        DecimalFormat formatter = new DecimalFormat("#,###.#####", symbols);
        return formatter.format(number);
    }

    private String getCoolString(Turn turn) {
        String out = "";
        if (turn.move1 != null) out += "m";
        if (turn.move2 != null) out += "m";
        if (turn.move3 != null) out += "m";
        if (turn.move1 == null && turn.move2 == null &&turn.move3 == null) out += "x";
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

    private void iterativeQuickSort(ArrayList<Turn> possibleTurns, ArrayList<Double> initialScores, boolean isMaximizingPlayer) {
        Stack<int[]> stack = new Stack<>();
        stack.push(new int[]{0, possibleTurns.size() - 1});

        while (!stack.isEmpty()) {
            int[] range = stack.pop();
            int low = range[0];
            int high = range[1];

            if (low < high) {
                int pi = partition(possibleTurns, initialScores, low, high, isMaximizingPlayer);
                stack.push(new int[]{low, pi - 1});
                stack.push(new int[]{pi + 1, high});
            }
        }
    }

    private int partition(ArrayList<Turn> possibleTurns, ArrayList<Double> initialScores, int low, int high, boolean isMaximizingPlayer) {
        double pivot = initialScores.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (isMaximizingPlayer) {
                if (initialScores.get(j) > pivot) {
                    i++;
                    swap(possibleTurns, initialScores, i, j);
                }
            } else {
                if (initialScores.get(j) < pivot) {
                    i++;
                    swap(possibleTurns, initialScores, i, j);
                }
            }
        }
        swap(possibleTurns, initialScores, i + 1, high);
        return i + 1;
    }

    private void swap(ArrayList<Turn> possibleTurns, ArrayList<Double> initialScores, int i, int j) {
        Turn tempTurn = possibleTurns.get(i);
        possibleTurns.set(i, possibleTurns.get(j));
        possibleTurns.set(j, tempTurn);

        double tempScore = initialScores.get(i);
        initialScores.set(i, initialScores.get(j));
        initialScores.set(j, tempScore);
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
