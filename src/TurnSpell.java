import java.util.ArrayList;

public class TurnSpell {
    SpellType spellType;
    PieceType mageType;
    int xFrom;
    int yFrom;
    ArrayList<int[]> targets;

    public TurnSpell(SpellType spellType, PieceType mageType, int xFrom, int yFrom, ArrayList<int[]> targets) {
        this.spellType = spellType;
        this.mageType = mageType;
        this.xFrom = xFrom;
        this.yFrom = yFrom;
        this.targets = targets;
    }

    public void print() {
        System.out.print("TurnSpell(" + spellType + ", " + mageType + ", " + xFrom + ", " + yFrom + ", [");
        for (int i = 0; i < targets.size(); i++) {
            int[] target = targets.get(i);
            System.out.print("(" + target[0] + ", " + target[1] + ")");
            if (i < targets.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("])");
    }
}
