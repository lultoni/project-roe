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

    public void castEffect(TurnSpell spell, Player player, Tile[][] board, Player otherPlayer) {
        player.setSpellTokens(player.getSpellTokens() - cost);
        switch (spellType) {
            case offense -> {
                int x = spell.targets.getFirst()[0];
                int y = spell.targets.getFirst()[1];
                switch (mageType) {
                    case fire -> {
                        removePiece(board, x, y);
                        setInfernoEffect(board, x, y, 0.75);
                    }
                    case water -> removePiece(board, x, y);
                    case earth -> {
                        removePiece(board, x, y);
                        setBlockedEffect(board, x, y, 1.75);
                    }
                    case air -> {
                        removePiece(board, x, y);
                        pushBackAirOffense(board, x, y, (board[spell.xFrom][spell.yFrom].getPiece().getPlayer()) ? 1 : -1);
                    }
                    case spirit -> {
                        removePiece(board, x, y);
                        if (otherPlayer.getSpellTokens() > 0) {
                            otherPlayer.setSpellTokens(otherPlayer.getSpellTokens() - 1);
                            player.setSpellTokens(player.getSpellTokens() + 1);
                        }
                    }
                }
            }
            case defense -> { // TODO
                switch (mageType) {
                    case fire -> {

                    }
                    case water -> {

                    }
                    case earth -> {

                    }
                    case air -> {

                    }
                    case spirit -> {

                    }
                }
            }
            case utility -> { // TODO
                switch (mageType) {
                    case fire -> {

                    }
                    case water -> {

                    }
                    case earth -> {

                    }
                    case air -> {

                    }
                    case spirit -> {

                    }
                }
            }
        }
    }

    private void pushBackAirOffense(Tile[][] board, int x, int y, int direction) {
        // Define the sequence of tiles to check based on the direction
        int[] sequence = new int[8];
        if (direction == 1) {
            // Direction 1: (8)(7)(6)
            //              (5)(x,y)(4)
            //              (3)(2)(1)
            sequence = new int[]{1, 2, 3, 4, 6, 7, 8};
        } else if (direction == -1) {
            // Direction -1: (1)(2)(3)
            //               (4)(x,y)(5)
            //               (6)(7)(8)
            sequence = new int[]{3, 2, 1, 8, 6, 5, 4};
        }

        for (int i : sequence) {
            int[] pos = getTilePositionInDirection(x, y, i);
            int newX = pos[0];
            int newY = pos[1];
            if (isValidPosition(newX, newY) && board[newX][newY].getPiece() != null && board[newX][newY].getPiece().getSpellProtectedTimer() == 0 && board[newX][newY].getPiece().getPlayer() != (direction == 1)) {
                pushBackPiece(board, newX, newY, direction);
            }
        }
    }

    private void pushBackPiece(Tile[][] board, int x, int y, int direction) {
        Piece piece = board[x][y].getPiece();
        int newPosY = y + direction;
        for (int i = (int) (y + Math.signum(direction)); i != newPosY; i += (int) (Math.signum(direction))) {
            if (board[x][i].getPiece() != null) return;
        }
        if (isValidPosition(x, newPosY) && board[x][newPosY].getPiece() == null) {
            board[x][newPosY].setPiece(piece);
            board[x][y].setPiece(null);
            piece.setPosition(x, newPosY);
        }
    }

    private int[] getTilePositionInDirection(int x, int y, int direction) {
        return switch (direction) {
            case 1 -> new int[]{x - 1, y - 1};
            case 2 -> new int[]{x, y - 1};
            case 3 -> new int[]{x + 1, y - 1};
            case 4 -> new int[]{x - 1, y};
            case 5 -> new int[]{x + 1, y};
            case 6 -> new int[]{x - 1, y + 1};
            case 7 -> new int[]{x, y + 1};
            case 8 -> new int[]{x + 1, y + 1};
            default -> throw new IllegalArgumentException("Invalid direction: " + direction);
        };
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < 8 && y >= 0 && y < 8;
    }


    private void setBlockedEffect(Tile[][] board, int x, int y, double timer) {
        board[x][y].setBlockedTimer(timer);
    }

    private void setInfernoEffect(Tile[][] board, int x, int y, double timer) {
        board[x][y].setDeathTimer(timer);
    }

    private void removePiece(Tile[][] board, int x, int y) {
        board[x][y].getPiece().setPosition(-1, -1);
        board[x][y].setPiece(null);
    }
}
