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

    public Turn fetchTurn(ArrayList<Turn> possibleTurns) {
        if (isHuman && false) {
            // Placeholder for human player input handling
            // You can implement the logic here to allow a human player to choose a turn
            // For example, you might print the possible turns and then read a selection from the console
//            System.out.println("Please select a turn from the following options:");
//            for (int i = 0; i < possibleTurns.size(); i++) {
//                System.out.println(i + ": " + possibleTurns.get(i));
//            }
            // Insert logic to read player's choice and return the selected turn
            // For now, just return null as a placeholder
            return null; // TODO: Implement human player turn selection
        } else {
            Random random = new Random();
            int selectedIndex = random.nextInt(possibleTurns.size());
            return possibleTurns.get(selectedIndex);
        }
    }
}
