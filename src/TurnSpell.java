import java.util.ArrayList;

public class TurnSpell {
    int spellDataIndex;
    int xFrom;
    int yFrom;
    ArrayList<int[]> targets;

    public TurnSpell(int spellDataIndex, int xFrom, int yFrom, ArrayList<int[]> targets) {
        this.spellDataIndex = spellDataIndex;
        this.xFrom = xFrom;
        this.yFrom = yFrom;
        this.targets = targets;
    }

    public void print() {
        System.out.print("TurnSpell(" + spellDataIndex + ", " + xFrom + ", " + yFrom + ", [");
        System.out.print(getTargetsString());
        System.out.println("])");
    }

    public String getTargetsString() {
        String out = "";
        for (int i = 0; i < targets.size(); i++) {
            int[] target = targets.get(i);
            out += "(" + target[0] + ", " + target[1] + ")";
            if (i < targets.size() - 1) {
                out += ", ";
            }
        }
        return out;
    }
}
