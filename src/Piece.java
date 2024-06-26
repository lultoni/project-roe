public class Piece {
    private int xPos;
    private int yPos;
    private PieceType type;
    private boolean player;
    private double spellProtectedTimer;
    private double spellReflectionTimer;
    private double attackProtectedTimer;

    public Piece(PieceType type, boolean player) {
        xPos = -1;
        yPos = -1;
        this.type = type;
        this.player = player;
        spellProtectedTimer = 0;
        spellReflectionTimer = 0;
        attackProtectedTimer = 0;
    }

    public void setPosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public PieceType getType() {
        return type;
    }

    public boolean getPlayer() {
        return player;
    }
}
