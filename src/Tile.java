public class Tile {
    private final Terrain terrain;
    private double blockedTimer;
    private double deathTimer;
    private Piece currentPiece;

    public Tile(Terrain terrain) {
        this.terrain = terrain;
        blockedTimer = 0;
        deathTimer = 0;
        currentPiece = null;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setPiece(Piece piece) {
        currentPiece = piece;
    }

    public Piece getPiece() {
        return currentPiece;
    }

    public double getBlockedTimer() {
        return blockedTimer;
    }

    public double getDeathTimer() {
        return deathTimer;
    }

    public void setBlockedTimer(double blockedTimer) {
        this.blockedTimer = blockedTimer;
    }

    public void setDeathTimer(double deathTimer) {
        this.deathTimer = deathTimer;
    }
}
