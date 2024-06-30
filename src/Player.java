import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            Scanner scanner = new Scanner(System.in);

            System.out.println("What is move 1?");
            String move1Str = scanner.nextLine();
            Move move1 = parseMove(move1Str);

            System.out.println("What is move 2?");
            String move2Str = scanner.nextLine();
            Move move2 = parseMove(move2Str);

            System.out.println("What is move 3?");
            String move3Str = scanner.nextLine();
            Move move3 = parseMove(move3Str);

            System.out.println("What is attack?");
            String attackStr = scanner.nextLine();
            Attack attack = parseAttack(attackStr);

            // TODO Spells are null for now
            return new Turn(move1, move2, move3, attack, null);
        } else {
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

    private Move parseMove(String moveStr) {
        Pattern movePattern = Pattern.compile("Move\\((\\d+),\\s*(\\d+),\\s*([-\\d]+),\\s*([-\\d]+)\\)");
        Matcher matcher = movePattern.matcher(moveStr);
        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int dx = Integer.parseInt(matcher.group(3));
            int dy = Integer.parseInt(matcher.group(4));
            return new Move(x, y, dx, dy);
        }
        return null; // or handle invalid input appropriately
    }

    private Attack parseAttack(String attackStr) {
        Pattern attackPattern = Pattern.compile("Attack\\((\\d+),\\s*(\\d+),\\s*([-\\d]+),\\s*([-\\d]+)\\)");
        Matcher matcher = attackPattern.matcher(attackStr);
        if (matcher.matches()) {
            int x = Integer.parseInt(matcher.group(1));
            int y = Integer.parseInt(matcher.group(2));
            int dx = Integer.parseInt(matcher.group(3));
            int dy = Integer.parseInt(matcher.group(4));
            return new Attack(x, y, dx, dy);
        }
        return null; // or handle invalid input appropriately
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
