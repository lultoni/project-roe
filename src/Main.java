import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        System.out.println("Boot - [project-roe]\n");
        Game game = new Game();

        game.printBoardPieces();
        ArrayList<ArrayList<TurnSpell>> possibleTurnSpells = game.generatePossibleSpellCombinations(game.getPlayer(false));
        System.out.println("PossibleSpellCombinations: " + possibleTurnSpells.size());
        for (ArrayList<TurnSpell> spellCombination: possibleTurnSpells) {
            System.out.println("\nSpellCombination:");
            for (TurnSpell spell: spellCombination) {
                spell.print();
                System.out.println("-" + game.getSpellData()[spell.spellDataIndex].mageType);
                System.out.println("-" + game.getSpellData()[spell.spellDataIndex].spellType);
            }
        }

//        System.out.println("\n\n---Starting Game---");
//        game.startGame();
        System.out.println("\nClose - [project-roe]");
    }
}