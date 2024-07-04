public class Piece {
    private int xPos;
    private int yPos;
    private final PieceType type;
    private final boolean player;
    private double spellProtectedTimer;
    private double spellReflectionTimer;
    private double attackProtectedTimer;
    private double overgrownTimer;
    private boolean hasMoved;

    public Piece(PieceType type, boolean player) {
        xPos = -1;
        yPos = -1;
        this.type = type;
        this.player = player;
        spellProtectedTimer = 0;
        spellReflectionTimer = 0;
        attackProtectedTimer = 0;
        hasMoved = false;
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

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public double getAttackProtectedTimer() {
        return attackProtectedTimer;
    }

    public double getSpellProtectedTimer() {
        return spellProtectedTimer;
    }

    public double getSpellReflectionTimer() {
        return spellReflectionTimer;
    }

    public void setAttackProtectedTimer(double attackProtectedTimer) {
        this.attackProtectedTimer = attackProtectedTimer;
    }

    public void setSpellProtectedTimer(double spellProtectedTimer) {
        this.spellProtectedTimer = spellProtectedTimer;
    }

    public void setSpellReflectionTimer(double spellReflectionTimer) {
        this.spellReflectionTimer = spellReflectionTimer;
    }

    public double getOvergrownTimer() {
        return overgrownTimer;
    }

    public void setOvergrownTimer(double overgrownTimer) {
        this.overgrownTimer = overgrownTimer;
    }
}
