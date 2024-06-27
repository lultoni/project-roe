import java.util.ArrayList;

public class Game {
    private Tile[][] board;
    private Player player0;
    private Player player1;

    public Game() {
        board = new Tile[8][8];
        loadMap("FPPP/FLLL/MLFP/MMMF");

        Piece[] p0p = new Piece[]{
                new Piece(PieceType.fire, false),
                new Piece(PieceType.water, false),
                new Piece(PieceType.spirit, false),
                new Piece(PieceType.earth, false),
                new Piece(PieceType.air, false),
                new Piece(PieceType.guard, false),
                new Piece(PieceType.guard, false),
                new Piece(PieceType.guard, false),
                new Piece(PieceType.guard, false),
                new Piece(PieceType.guard, false)};
        Piece[] p1p = new Piece[]{
                new Piece(PieceType.fire, true),
                new Piece(PieceType.water, true),
                new Piece(PieceType.spirit, true),
                new Piece(PieceType.earth, true),
                new Piece(PieceType.air, true),
                new Piece(PieceType.guard, true),
                new Piece(PieceType.guard, true),
                new Piece(PieceType.guard, true),
                new Piece(PieceType.guard, true),
                new Piece(PieceType.guard, true)};
        player0 = new Player(p0p, true);
        player1 = new Player(p1p, true);
        setPiecesStart();
    }

    private void loadMap(String mapFEN) {
        String[] halves = mapFEN.split("/");
        String fullMap = "";

        for (String lineSplit: halves) {
            fullMap += lineSplit + new StringBuilder(lineSplit).reverse();
        }
        fullMap = fullMap + new StringBuilder(fullMap).reverse();

        for (int i = 0; i < 64; i++) {
            char cellType = fullMap.charAt(i);
            Terrain terrain = switch (cellType) {
                case 'P' -> Terrain.plains;
                case 'F' -> Terrain.forest;
                case 'M' -> Terrain.mountain;
                case 'L' -> Terrain.lake;
                default -> throw new IllegalArgumentException("Invalid terrain symbol: " + cellType);
            };

            board[i % 8][i / 8] = new Tile(terrain);
        }
    }

    private void setPiecesStart() {
        Piece[] p0p = player0.getPieces();
        Piece[] p1p = player1.getPieces();

        setPiece(1, 7, p0p[0]);
        setPiece(2, 7, p0p[1]);
        setPiece(3, 7, p0p[2]);
        setPiece(4, 7, p0p[3]);
        setPiece(5, 7, p0p[4]);

        setPiece(6, 0, p1p[0]);
        setPiece(5, 0, p1p[1]);
        setPiece(4, 0, p1p[2]);
        setPiece(3, 0, p1p[3]);
        setPiece(2, 0, p1p[4]);

        setPiece(1, 6, p0p[5]);
        setPiece(2, 6, p0p[6]);
        setPiece(3, 6, p0p[7]);
        setPiece(4, 6, p0p[8]);
        setPiece(5, 6, p0p[9]);

        setPiece(6, 1, p1p[5]);
        setPiece(5, 1, p1p[6]);
        setPiece(4, 1, p1p[7]);
        setPiece(3, 1, p1p[8]);
        setPiece(2, 1, p1p[9]);
    }

    public void startGame() {
        while(isGameOver() == 2) {
            printBoardPieces();
            ArrayList<Turn> possibleTurns = generatePossibleTurns(player0);
            Turn turn = player0.fetchTurn(possibleTurns);
            System.out.println("Player0 Turn:");
            turn.print();
            executeTurn(turn, player0);
            printBoardPieces();
            possibleTurns = generatePossibleTurns(player1);
            turn = player1.fetchTurn(possibleTurns);
            System.out.println("Player1 Turn:");
            turn.print();
            executeTurn(turn, player1);
            if(isGameOver() != 2) printBoardPieces();
        }
        switch (isGameOver()) {
            case -1 -> System.out.println("Draw between p0 and p1");
            case 0 -> System.out.println("Win for p0");
            case 1 -> System.out.println("Win for p1");
        }
    }

    private int isGameOver() {
        // Win p0 = 0
        // Win p1 = 1
        // Draw = -1
        // game isn't over = 2
        boolean isOnlyP0Spirit = false;
        boolean isOnlyP1Spirit = false;
        for(Piece piece: player0.getPieces()) {
            if (piece.getType() == PieceType.spirit && piece.getXPos() == -1) {
                return 1;
            }
            if (piece.getType() != PieceType.spirit && piece.getXPos() == -1 || piece.getType() == PieceType.spirit && piece.getXPos() != -1) {
                isOnlyP0Spirit = true;
            } else {
                isOnlyP0Spirit = false;
            }
        }
        for(Piece piece: player1.getPieces()) {
            if (piece.getType() == PieceType.spirit && piece.getXPos() == -1) {
                return 0;
            }
            if (piece.getType() != PieceType.spirit && piece.getXPos() == -1 || piece.getType() == PieceType.spirit && piece.getXPos() != -1) {
                isOnlyP1Spirit = true;
            } else {
                isOnlyP1Spirit = false;
            }
        }
        return (isOnlyP0Spirit && isOnlyP1Spirit) ? -1 : 2;
    }

    private void executeTurn(Turn turn, Player player) {
        doMove(turn.move1, false);
        doMove(turn.move2, false);
        doMove(turn.move3, false);
        resetHasMoved(player);
        updateTimers();
        doAttack(turn.attack);
        for (TurnSpell spell: turn.spells) {
            // TODO castSpell(spell) with effects and taking st of player
        }
        updateTimers();
    }

    public ArrayList<Turn> generatePossibleTurns(Player player) {
        ArrayList<Turn> possibleTurns = new ArrayList<>();

        // x x x
        possibleTurns.add(new Turn(null, null, null, null, null));
        // x a x
        ArrayList<Attack> possibleAttacks = generatePossibleAttacks(player);
        if (!possibleAttacks.isEmpty()) for (Attack attack: possibleAttacks) {
            possibleTurns.add(new Turn(null, null, null, attack, null));
        }
        // x x s
        ArrayList<ArrayList<TurnSpell>> possibleSpellCombinations = generatePossibleSpellCombinations(player);
        if (!possibleSpellCombinations.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellCombinations) {
            possibleTurns.add(new Turn(null, null, null, null, spells));
        }
        // x a s
        if (!possibleAttacks.isEmpty()) for (Attack attack: possibleAttacks) {
            if (!possibleSpellCombinations.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellCombinations) {
                possibleTurns.add(new Turn(null, null, null, attack, spells));
            }
        }
        // m x x TODO
        // m a x TODO
        // m x s TODO
        // m a s TODO
        // m m x x TODO
        // m m a x TODO
        // m m x s TODO
        // m m a s TODO
        // m m m x x TODO
        // m m m a x TODO
        // m m m x s TODO
        // m m m a s TODO

        ArrayList<Move> possibleMoves = generatePossibleMoves(player);

        return possibleTurns;
    }

    public void copyGameState() {
        // TODO: Implement the copy game state logic
    }

    public void saveGameState() {
        // TODO: Implement the save game state logic
    }

    public ArrayList<ArrayList<TurnSpell>> generatePossibleSpellCombinations(Player player) {
        ArrayList<ArrayList<TurnSpell>> possibleSpellCombinations = new ArrayList<>();
        // TODO generate all possible spell combinations
        // TODO no empty spell combination
        return possibleSpellCombinations;
    }

    private void updateTimers() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = board[x][y];
                tile.setBlockedTimer(Math.max(tile.getBlockedTimer() - 0.25, 0));
                tile.setDeathTimer(Math.max(tile.getDeathTimer() - 0.25, 0));
            }
        }
        for (Piece piece: player1.getPieces()) {
            piece.setAttackProtectedTimer(Math.max(piece.getAttackProtectedTimer() - 0.25, 0));
            piece.setSpellProtectedTimer(Math.max(piece.getSpellProtectedTimer() - 0.25, 0));
            piece.setSpellReflectionTimer(Math.max(piece.getSpellReflectionTimer() - 0.25, 0));
        }
        for (Piece piece: player0.getPieces()) {
            piece.setAttackProtectedTimer(Math.max(piece.getAttackProtectedTimer() - 0.25, 0));
            piece.setSpellProtectedTimer(Math.max(piece.getSpellProtectedTimer() - 0.25, 0));
            piece.setSpellReflectionTimer(Math.max(piece.getSpellReflectionTimer() - 0.25, 0));
        }
    }

    private void resetHasMoved(Player player) {
        for (Piece piece: player.getPieces()) {
            piece.setHasMoved(false);
        }
    }

    public ArrayList<Move> generatePossibleMoves(Player player) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            int xPos = piece.getXPos();
            int yPos = piece.getYPos();
            int range = getRange(xPos, yPos);

            for (int xChange = -range; xChange <= range; xChange++) {
                for (int yChange = -range; yChange <= range; yChange++) {
                    if (xChange != 0 || yChange != 0) {
                        int newX = xPos + xChange;
                        int newY = yPos + yChange;
                        if (newX >= 0 && newX <= 7 && newY >= 0 && newY <= 7) {
                            Move move = new Move(xPos, yPos, xChange, yChange);
                            if (isLegalMove(move)) {
                                possibleMoves.add(move);
                            }
                        }
                    }
                }
            }
        }
        return possibleMoves;
    }

    public ArrayList<Attack> generatePossibleAttacks(Player player) {
        ArrayList<Attack> possibleAttacks = new ArrayList<>();
        for (Piece piece : player.getPieces()) {
            int xPos = piece.getXPos();
            int yPos = piece.getYPos();
            int range = 1;

            for (int xChange = -range; xChange <= range; xChange++) {
                for (int yChange = -range; yChange <= range; yChange++) {
                    if (xChange != 0 || yChange != 0) {
                        int newX = xPos + xChange;
                        int newY = yPos + yChange;
                        if (newX >= 0 && newX <= 7 && newY >= 0 && newY <= 7) {
                            Attack attack = new Attack(xPos, yPos, xChange, yChange);
                            if (isLegalAttack(attack)) {
                                possibleAttacks.add(attack);
                            }
                        }
                    }
                }
            }
        }
        return possibleAttacks;
    }

    private int getRange(int xPos, int yPos) {
        return (pieceTerrainAdvantage(xPos, yPos) == 1) ? 2 : 1;
    }

    public void doMove(Move move, boolean debug) {
        if (move != null) {
            if (!isLegalMove(move)) throw new IllegalArgumentException("The Move that was provided is not Legal.\nxFrom: " + move.xFrom + "\nyFrom: " + move.yFrom + "\nxChange: " + move.xChange + "\nyChange: " + move.yChange);
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            setPiece(move.xFrom + move.xChange, move.yFrom + move.yChange, piece);
            setPiece(move.xFrom, move.yFrom, null);
            if (!debug) piece.setHasMoved(true);
        }
    }

    public void doAttack(Attack attack) {
        if (attack != null) {
            if (!isLegalAttack(attack)) throw new IllegalArgumentException("The Attack that was provided is not Legal.\nxFrom: " + attack.xFrom + "\nyFrom: " + attack.yFrom + "\nxChange: " + attack.xChange + "\nyChange: " + attack.yChange);
            Piece attackingPiece = board[attack.xFrom][attack.yFrom].getPiece();
            Piece defendingPiece = board[attack.xFrom + attack.xChange][attack.yFrom + attack.yChange].getPiece();
            Piece protectingGuard = (defendingPiece.getType() != PieceType.guard && !guardSkip(attack)) ? findProtectingGuard(attack) : null;
            if (protectingGuard == null) {
                defendingPiece.setPosition(-1, -1);
                setPiece(attack.xFrom + attack.xChange, attack.yFrom + attack.yChange, attackingPiece);
                setPiece(attack.xFrom, attack.yFrom, null);
            } else {
                setPiece(protectingGuard.getXPos(), protectingGuard.getYPos(), null);
                protectingGuard.setPosition(-1, -1);
            }
        }
    }

    private boolean guardSkip(Attack attack) {
        Piece attackingPiece = board[attack.xFrom][attack.yFrom].getPiece();
        Piece defendingPiece = board[attack.xFrom + attack.xChange][attack.yFrom + attack.yChange].getPiece();
        return attackingPiece.getType() == PieceType.water && defendingPiece.getType() == PieceType.fire ||
                attackingPiece.getType() == PieceType.fire && defendingPiece.getType() == PieceType.air ||
                attackingPiece.getType() == PieceType.air && defendingPiece.getType() == PieceType.earth ||
                attackingPiece.getType() == PieceType.earth && defendingPiece.getType() == PieceType.water;
    }

    private Piece findProtectingGuard(Attack attack) {
        int xDif = -attack.xChange;
        int yDif = -attack.yChange;

        if (xDif == 0 && yDif == 1) {
            int[][] directions = {
                    { -1, 0, 1 }, { 1, 0, 1 },
                    { -1, -1, 2 }, { 1, -1, 2 },
                    { -1, -2, 3 }, { 1, -2, 3 },
                    { 0, -2, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == -1 && yDif == 1) {
            int[][] directions = {
                    { 0, -1, 1 }, { 1, 0, 1 },
                    { 0, -2, 2 }, { 2, 0, 2 },
                    { 1, -2, 3 }, { 2, -1, 3 },
                    { 2, -2, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == -1 && yDif == 0) {
            int[][] directions = {
                    { 0, -1, 1 }, { 0, 1, 1 },
                    { 1, -1, 2 }, { 1, 1, 2 },
                    { 2, -1, 3 }, { 2, 1, 3 },
                    { 2, 0, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == -1 && yDif == -1) {
            int[][] directions = {
                    { 1, 0, 1 }, { 0, 1, 1 },
                    { 2, 0, 2 }, { 0, 2, 2 },
                    { 2, 1, 3 }, { 1, 2, 3 },
                    { 2, 2, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == 0 && yDif == -1) {
            int[][] directions = {
                    { 1, 0, 1 }, { -1, 0, 1 },
                    { 1, 1, 2 }, { -1, 1, 2 },
                    { 1, 2, 3 }, { -1, 2, 3 },
                    { 0, 2, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == 1 && yDif == -1) {
            int[][] directions = {
                    { 0, 1, 1 }, { -1, 0, 1 },
                    { 0, 2, 2 }, { -2, 0, 2 },
                    { -1, 2, 3 }, { -2, 1, 3 },
                    { -2, 2, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == 1 && yDif == 0) {
            int[][] directions = {
                    { 0, -1, 1 }, { 0, 1, 1 },
                    { -1, -1, 2 }, { -1, 1, 2 },
                    { -2, -1, 3 }, { -2, 1, 3 },
                    { -2, 0, 4 }
            };
            return closestGuard(directions, attack);
        } else if (xDif == 1 && yDif == 1) {
            int[][] directions = {
                    { 0, -1, 1 }, { -1, 0, 1 },
                    { 0, -2, 2 }, { -2, 0, 2 },
                    { -1, -2, 3 }, { -2, -1, 3 },
                    { -2, -2, 4 }
            };
            return closestGuard(directions, attack);
        }
        return null;
    }

    private Piece closestGuard(int[][] directions, Attack attack) {
        Piece closestGuard = null;
        int minDistance = Integer.MAX_VALUE;
        for (int[] dir : directions) {
            int x = attack.xFrom + dir[0];
            int y = attack.yFrom + dir[1];
            int distance = dir[2];

            if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                Piece potentialGuard = board[x][y].getPiece();
                if (potentialGuard != null && potentialGuard.getType() == PieceType.guard) {
                    int currentGuardDistance = Math.abs(x + y - 7);
                    int closestGuardDistance = closestGuard != null ? Math.abs(closestGuard.getXPos() + closestGuard.getYPos() - 7) : Integer.MAX_VALUE;
                    if (closestGuard == null || distance < minDistance || (distance == minDistance && currentGuardDistance < closestGuardDistance)) {
                        closestGuard = potentialGuard;
                        minDistance = distance;
                    }
                }
            }
        }
        return closestGuard;
    }

    private boolean isLegalAttack(Attack attack) {
        int range = getRange(attack.xFrom, attack.yFrom);
        if (range == 1 || Math.abs(attack.xChange) == 1 && Math.abs(attack.yChange) == 1 || Math.abs(attack.xChange) == 0 && Math.abs(attack.yChange) == 1 || Math.abs(attack.xChange) == 1 && Math.abs(attack.yChange) == 0) {
            Piece attackingPiece = board[attack.xFrom][attack.yFrom].getPiece();
            Piece defendingPiece = board[attack.xFrom + attack.xChange][attack.yFrom + attack.yChange].getPiece();
            return defendingPiece != null && defendingPiece.getPlayer() != attackingPiece.getPlayer();
        }
        return false;
    }

    private boolean isLegalMove(Move move) {
        if (board[move.xFrom][move.yFrom].getPiece().hasMoved()) return false;
        int range = getRange(move.xFrom, move.yFrom);
        if (range == 1 || Math.abs(move.xChange) == 1 && Math.abs(move.yChange) == 1 || Math.abs(move.xChange) == 0 && Math.abs(move.yChange) == 1 || Math.abs(move.xChange) == 1 && Math.abs(move.yChange) == 0) {
            return board[move.xFrom + move.xChange][move.yFrom + move.yChange].getPiece() == null;
        } else {
            return Math.abs(move.xChange) <= range && Math.abs(move.yChange) <= range && board[move.xFrom + move.xChange][move.yFrom + move.yChange].getPiece() == null && isNoPieceBetween(move);
        }
    }

    private boolean isNoPieceBetween(Move move) {
        if (move.xChange == 2) {
            switch (move.yChange) {
                case 2 -> {
                    return board[move.xFrom + 1][move.yFrom + 1].getPiece() == null;
                }
                case 1 -> {
                    return board[move.xFrom + 1][move.yFrom + 1].getPiece() == null || board[move.xFrom + 1][move.yFrom].getPiece() == null;
                }
                case 0 -> {
                    return (move.yFrom < 7 && board[move.xFrom + 1][move.yFrom + 1].getPiece() == null) || board[move.xFrom + 1][move.yFrom].getPiece() == null || (move.yFrom > 0 && board[move.xFrom + 1][move.yFrom - 1].getPiece() == null);
                }
                case -1 -> {
                    return board[move.xFrom + 1][move.yFrom].getPiece() == null || board[move.xFrom + 1][move.yFrom - 1].getPiece() == null;
                }
                case -2 -> {
                    return board[move.xFrom + 1][move.yFrom - 1].getPiece() == null;
                }
            }
        } else if (move.xChange == 1) {
            switch (move.yChange) {
                case -2 -> {
                    return board[move.xFrom][move.yFrom - 1].getPiece() == null || board[move.xFrom + 1][move.yFrom - 1].getPiece() == null;
                }
                case 2 -> {
                    return board[move.xFrom][move.yFrom + 1].getPiece() == null || board[move.xFrom + 1][move.yFrom + 1].getPiece() == null;
                }
            }
        } else if (move.xChange == 0) {
            switch (move.yChange) {
                case -2 -> {
                    return (move.xFrom > 0 && board[move.xFrom - 1][move.yFrom - 1].getPiece() == null) || board[move.xFrom][move.yFrom - 1].getPiece() == null || (move.xFrom < 7 && board[move.xFrom + 1][move.yFrom - 1].getPiece() == null);
                }
                case 2 -> {
                    return (move.xFrom > 0 && board[move.xFrom - 1][move.yFrom + 1].getPiece() == null) || board[move.xFrom][move.yFrom + 1].getPiece() == null || (move.xFrom < 7 && board[move.xFrom + 1][move.yFrom + 1].getPiece() == null);
                }
            }
        } else if (move.xChange == -1) {
            switch (move.yChange) {
                case -2 -> {
                    return board[move.xFrom][move.yFrom - 1].getPiece() == null || board[move.xFrom - 1][move.yFrom - 1].getPiece() == null;
                }
                case 2 -> {
                    return board[move.xFrom][move.yFrom + 1].getPiece() == null || board[move.xFrom - 1][move.yFrom + 1].getPiece() == null;
                }
            }
        } else if (move.xChange == -2) {
            switch (move.yChange) {
                case 2 -> {
                    return board[move.xFrom - 1][move.yFrom + 1].getPiece() == null;
                }
                case 1 -> {
                    return board[move.xFrom - 1][move.yFrom + 1].getPiece() == null || board[move.xFrom - 1][move.yFrom].getPiece() == null;
                }
                case 0 -> {
                    return (move.yFrom < 7 && board[move.xFrom - 1][move.yFrom + 1].getPiece() == null) || board[move.xFrom - 1][move.yFrom].getPiece() == null || (move.yFrom > 0 && board[move.xFrom - 1][move.yFrom - 1].getPiece() == null);
                }
                case -1 -> {
                    return board[move.xFrom - 1][move.yFrom].getPiece() == null || board[move.xFrom - 1][move.yFrom - 1].getPiece() == null;
                }
                case -2 -> {
                    return board[move.xFrom - 1][move.yFrom - 1].getPiece() == null;
                }
            }
        }
        return false;
    }

    private int pieceTerrainAdvantage(int x, int y) {
        if (board[x][y].getPiece() == null) throw new IllegalArgumentException("There is no Piece on x" + x + " y" + y + ".");
        PieceType type = board[x][y].getPiece().getType();
        if (type == PieceType.guard || type == PieceType.spirit) return 0;
        Terrain terrain = board[x][y].getTerrain();
        switch (type) {
            case air -> {
                if (terrain == Terrain.mountain) return 1;
                if (terrain == Terrain.forest) return -1;
            }
            case earth -> {
                if (terrain == Terrain.forest) return 1;
                if (terrain == Terrain.plains) return -1;
            }
            case water -> {
                if (terrain == Terrain.lake) return 1;
                if (terrain == Terrain.mountain) return -1;
            }
            case fire -> {
                if (terrain == Terrain.plains) return 1;
                if (terrain == Terrain.lake) return -1;
            }
        }
        return 0;
    }

    private void setPiece(int x, int y, Piece piece) {
        board[x][y].setPiece(piece);
        if (piece != null) piece.setPosition(x, y);
    }

    public void printBoardTerrain() {
        System.out.println("BoardTerrain:");
        for (int y = 0; y < 8; y++) {
            String out = "";
            for (int x = 0; x < 8; x++) {
                String addition = switch (board[x][y].getTerrain()) {
                    case Terrain.forest -> " F ";
                    case Terrain.plains -> " P ";
                    case Terrain.mountain -> " M ";
                    case Terrain.lake -> " L ";
                };
                out += addition;
            }
            System.out.println(out);
        }
    }

    public void printBoardCoordinates() {
        System.out.println("BoardCoordinates:");
        for (int y = 0; y < 8; y++) {
            String out = "";
            for (int x = 0; x < 8; x++) {
                out += " x" + x + "y" + y + " ";
            }
            System.out.println(out);
        }
    }

    public void printBoardPieces() {
        System.out.println("BoardPieces of Player 0 (Blue) and Player 1 (Red):");
        for (int y = 0; y < 8; y++) {
            String out = "";
            for (int x = 0; x < 8; x++) {
                String addition = " - ";
                if (board[x][y].getPiece() != null) {
                    Piece piece = board[x][y].getPiece();
                    String colorCode = piece.getPlayer() ? "\033[31m" : "\033[34m"; // Red for player 1, Blue for player 0
                    String pieceChar = switch (piece.getType()) {
                        case PieceType.air -> " A ";
                        case PieceType.spirit -> " S ";
                        case PieceType.earth -> " E ";
                        case PieceType.fire -> " F ";
                        case PieceType.guard -> " G ";
                        case PieceType.water -> " W ";
                    };
                    addition = colorCode + pieceChar + "\033[0m";
                }
                out += addition;
            }
            System.out.println(out);
        }
    }

    public Player getPlayer(boolean player) {
        return (player) ? player1 : player0;
    }
}
