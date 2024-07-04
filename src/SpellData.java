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
                        boolean isSpellReflecting = board[x][y].getPiece().getSpellReflectionTimer() > 0;
                        removePiece(board, x, y);
                        setInfernoEffect(board, x, y, 0.75);
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) {
                            removePiece(board, spell.xFrom, spell.yFrom);
                            setInfernoEffect(board, spell.xFrom, spell.yFrom, 0.75);
                        }
                    }
                    case water -> {
                        boolean isSpellReflecting = board[x][y].getPiece().getSpellReflectionTimer() > 0;
                        removePiece(board, x, y);
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) {
                            removePiece(board, spell.xFrom, spell.yFrom);
                        }
                    }
                    case earth -> {
                        boolean isSpellReflecting = board[x][y].getPiece().getSpellReflectionTimer() > 0;
                        removePiece(board, x, y);
                        setBlockedEffect(board, x, y);
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) {
                            removePiece(board, spell.xFrom, spell.yFrom);
                        }
                    }
                    case air -> { // TODO test
                        boolean isSpellReflecting = board[x][y].getPiece().getSpellReflectionTimer() > 0;
                        removePiece(board, x, y);
                        pushBackAirOffense(board, x, y, (board[spell.xFrom][spell.yFrom].getPiece().getPlayer()) ? 1 : -1);
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) {
                            boolean b = board[spell.xFrom][spell.yFrom].getPiece().getPlayer();
                            removePiece(board, spell.xFrom, spell.yFrom);
                            pushBackAirOffense(board, spell.xFrom, spell.yFrom, (b) ? -1 : 1);
                        }
                    }
                    case spirit -> {
                        boolean isSpellReflecting = board[x][y].getPiece().getSpellReflectionTimer() > 0;
                        removePiece(board, x, y);
                        if (otherPlayer.getSpellTokens() > 0 && !(isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0)) {
                            otherPlayer.setSpellTokens(otherPlayer.getSpellTokens() - 1);
                            player.setSpellTokens(player.getSpellTokens() + 1);
                        }
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) {
                            removePiece(board, spell.xFrom, spell.yFrom);
                        }
                    }
                }
            }
            case defense -> {
                int x = -1;
                int y = -1;
                if (!spell.targets.isEmpty()) {
                    x = spell.targets.getFirst()[0];
                    y = spell.targets.getFirst()[1];
                }
                switch (mageType) {
                    case fire -> attackProtect(board, spell.xFrom, spell.yFrom, x, y);
                    case water, earth, spirit -> spellProtect(board, spell.xFrom, spell.yFrom, x, y);
                    case air -> reflectProtect(board, spell.xFrom, spell.yFrom, x, y);
                }
            }
            case utility -> {
                switch (mageType) {
                    case fire -> { // TODO test
                        for (int[] t: spell.targets) {
                            setInfernoEffect(board, t[0], t[1], 1.75);
                        }
                    }
                    case water -> { // TODO test
                        int direction = (board[spell.xFrom][spell.yFrom].getPiece().getPlayer()) ? 2 : -2;
                        boolean isSpellReflecting = false;
                        for (int[] t: spell.targets) {
                            if (board[t[0]][t[1]].getPiece().getSpellReflectionTimer() > 0) {
                                isSpellReflecting = true;
                            }
                            if (board[t[0]][t[1]].getPiece().getSpellProtectedTimer() == 0) pushBackPiece(board, t[0], t[1], direction);
                        }
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) pushBackPiece(board, spell.xFrom, spell.yFrom, -direction);
                    }
                    case earth -> { // TODO test
                        boolean isSpellReflecting = false;
                        for (int[] t: spell.targets) {
                            if (board[t[0]][t[1]].getPiece() == null) continue;
                            if (board[t[0]][t[1]].getPiece().getSpellReflectionTimer() > 0) {
                                isSpellReflecting = true;
                            }
                            if (board[t[0]][t[1]].getPiece().getSpellProtectedTimer() == 0) givePieceOvergrown(board, t[0], t[1]);
                        }
                        if (isSpellReflecting && board[spell.xFrom][spell.yFrom].getPiece().getSpellProtectedTimer() == 0) givePieceOvergrown(board, spell.xFrom, spell.yFrom);
                    }
                    case air -> {
                        int x = spell.targets.getFirst()[0];
                        int y = spell.targets.getFirst()[1];
                        Piece piece = board[spell.xFrom][spell.yFrom].getPiece();
                        if (board[x][y].getDeathTimer() > 0) {
                            piece.setPosition(-1, -1);
                            setPiece(board, spell.xFrom, spell.yFrom, null);
                        } else {
                            setPiece(board, x, y, piece);
                            setPiece(board, spell.xFrom, spell.yFrom, null);
                        }
                    }
                    case spirit -> {
                        int x1 = spell.targets.getFirst()[0];
                        int y1 = spell.targets.getFirst()[1];
                        Piece p1 = board[x1][y1].getPiece();
                        int x2 = spell.targets.getLast()[0];
                        int y2 = spell.targets.getLast()[1];
                        Piece p2 = board[x2][y2].getPiece();
                        setPiece(board, x2, y2, p1);
                        setPiece(board, x1, y1, p2);
                    }
                }
            }
        }
    }

    private void setPiece(Tile[][] board, int x, int y, Piece piece) {
        if (x >= 0 && x < 8 && y >= 0 && y < 8) board[x][y].setPiece(piece);
        if (piece != null) piece.setPosition(x, y);
    }

    private void givePieceOvergrown(Tile[][] board, int x, int y) {
        if (board[x][y].getPiece().getSpellProtectedTimer() == 0) board[x][y].getPiece().setOvergrownTimer(1);
    }

    private void reflectProtect(Tile[][] board, int xFrom, int yFrom, int x, int y) {
        board[xFrom][yFrom].getPiece().setSpellReflectionTimer(1);
        board[x][y].getPiece().setSpellReflectionTimer(1);
    }

    private void spellProtect(Tile[][] board, int xFrom, int yFrom, int x, int y) {
        board[xFrom][yFrom].getPiece().setSpellProtectedTimer(1);
        board[x][y].getPiece().setSpellProtectedTimer(1);
    }

    private void attackProtect(Tile[][] board, int xFrom, int yFrom, int x, int y) {
        board[xFrom][yFrom].getPiece().setAttackProtectedTimer(1);
        board[x][y].getPiece().setAttackProtectedTimer(1);
    }

    private void pushBackAirOffense(Tile[][] board, int x, int y, int direction) { // TODO blocked/death
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

    private void pushBackPiece(Tile[][] board, int x, int y, int direction) { // TODO blocked/death
        Piece piece = board[x][y].getPiece();
        int newPosY = y + direction;

        // Check if newPosY is valid, if not, reduce it nearer to 0 by 1
        while (!isValidPosition(x, newPosY)) {
            direction -= (int) Math.signum(direction);
            newPosY = y + direction;
        }

        // Check if all positions in the path are empty
        for (int i = y + (int) Math.signum(direction); i != newPosY; i += (int) Math.signum(direction)) {
            if (!isValidPosition(x, i) || board[x][i].getPiece() != null) return;
        }

        // Check if the final position is valid and empty
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


    private void setBlockedEffect(Tile[][] board, int x, int y) {
        board[x][y].setBlockedTimer(1.75);
    }

    private void setInfernoEffect(Tile[][] board, int x, int y, double timer) {
        board[x][y].setDeathTimer(timer);
    }

    private void removePiece(Tile[][] board, int x, int y) {
        board[x][y].getPiece().setPosition(-1, -1);
        board[x][y].setPiece(null);
    }
}
