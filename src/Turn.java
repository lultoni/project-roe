import java.util.ArrayList;

public class Turn {
    Move move1;
    Move move2;
    Move move3;
    Attack attack;
    ArrayList<TurnSpell> spells;

    public Turn(Move move1, Move move2, Move move3, Attack attack, ArrayList<TurnSpell> spells) {
        this.move1 = move1;
        this.move2 = move2;
        this.move3 = move3;
        this.attack = attack;
        this.spells = spells;
    }

    public void print() {
        if (move1 != null) { // TODO fix if you change this approach
            System.out.println("Move 1:");
            move1.print();
        }
        if (move2 != null) {
            System.out.println("Move 2:");
            move2.print();
        }
        if (move3 != null) {
            System.out.println("Move 3:");
            move3.print();
        }
        if (attack != null) {
            System.out.println("Attack:");
            attack.print();
        }
        if (spells != null && !spells.isEmpty()) {
            System.out.println("Spells:");
            for (TurnSpell spell: spells) {
                spell.print();
            }
        }

    }
}
