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
}
