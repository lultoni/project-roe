public class SpellData {
    int cost;
    String name;
    String description;
    SpellType spellType;
    PieceType mageType;

    public SpellData(int cost, String name, String description, SpellType spellType, PieceType mageType) {
        this.cost = cost;
        this.name = name;
        this.description = description;
        this.spellType = spellType;
        this.mageType = mageType;
    }

    public void castEffect(TurnSpell spell) {
        // TODO error if spell is not the same as spellData (?)
        // switch of everything
    }
}
