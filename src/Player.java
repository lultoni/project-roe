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

    public Turn fetchTurn(Game game) {
        if (isHuman) {
            while (game.isWaitingForHuman()) {
                Thread.onSpinWait();
            }
            return game.getHumanTurn();
        } else {
            MCTS mcts = new MCTS();
            System.out.println("\nthinking for (expected) " + mcts.TIME_LIMIT + " ms");
            boolean player = game.getTurnCounter() % 1 != 0;
            Turn bestTurn = mcts.findBestMove(game, player);
            System.out.println("games_simulated: " + mcts.games_simulated);
            System.out.println("average_len_game: " + mcts.average_len_game);
            return bestTurn;
        }
    }

    public boolean getIsHuman() {
        return isHuman;
    }

    public int getSpellTokens() {
        return spellTokens;
    }

    public void setSpellTokens(int spellTokens) {
        this.spellTokens = spellTokens;
    }

    public boolean equals(Player player) {
        boolean baseInfo = isHuman == player.getIsHuman() && spellTokens == player.getSpellTokens();
        boolean piecesSame = true;
        for (int i = 0; i < pieces.length; i++) {
            boolean b1 = pieces[i].getType() == player.getPieces()[i].getType();
            boolean b2 = pieces[i].getPlayer() == player.getPieces()[i].getPlayer();
            boolean b3 = pieces[i].getXPos() == player.getPieces()[i].getXPos();
            boolean b4 = pieces[i].getYPos() == player.getPieces()[i].getYPos();
            boolean b5 = pieces[i].hasMoved() == player.getPieces()[i].hasMoved();
            boolean b6 = pieces[i].getAttackProtectedTimer() == player.getPieces()[i].getAttackProtectedTimer();
            boolean b7 = pieces[i].getSpellProtectedTimer() == player.getPieces()[i].getSpellProtectedTimer();
            boolean b8 = pieces[i].getSpellReflectionTimer() == player.getPieces()[i].getSpellReflectionTimer();
            boolean b9 = pieces[i].getOvergrownTimer() == player.getPieces()[i].getOvergrownTimer();
            piecesSame = piecesSame && b1 && b2 && b3 && b4 && b5 && b6 && b7 && b8 && b9;
        }
        return baseInfo && piecesSame;
    }
}
