import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {
    final Tile[][] board;
    private Player player0;
    private Player player1;
    private double turnCounter;
    private final SpellData[] spellData;
    private boolean waitingForHuman;
    private Turn humanTurn;
    private boolean printDebug;

    public Game() {
        board = new Tile[8][8];
        loadMap();

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
        player0 = new Player(p0p, false);
        player1 = new Player(p1p, false);
        setPiecesStart();

        turnCounter = 1;

        spellData = new SpellData[15];
        spellData[0] = new SpellData(3, "Fireball", "Kill the target, give space Inferno effect for 1 turn.", SpellType.offense, PieceType.fire);
        spellData[1] = new SpellData(2, "Icy Spear", "Kill the target.", SpellType.offense, PieceType.water);
        spellData[2] = new SpellData(3, "Rock Slide", "Kill the target and make space unavailable for two turns.", SpellType.offense, PieceType.earth);
        spellData[3] = new SpellData(3, "Gale Force", "Kill the target and push back surrounding pieces by one.", SpellType.offense, PieceType.air);
        spellData[4] = new SpellData(3, "Soul Siphon", "Kill the target and take one spell token from the opponent if they have at least 1 token.", SpellType.offense, PieceType.spirit);
        spellData[5] = new SpellData(2, "Blazing Barrier", "Immune to attacks (not spells) for one turn.", SpellType.defense, PieceType.fire);
        spellData[6] = new SpellData(2, "Aqua Shield", "Immune to spells for one turn.", SpellType.defense, PieceType.water);
        spellData[7] = new SpellData(2, "Stone Wall", "Immune to spells for one turn.", SpellType.defense, PieceType.earth);
        spellData[8] = new SpellData(2, "Areal Shield", "Apply spell cast on mage on attacker as well, lasts for one turn.", SpellType.defense, PieceType.air);
        spellData[9] = new SpellData(2, "Ethereal Shield", "Immune to spells for one turn.", SpellType.defense, PieceType.spirit);
        spellData[10] = new SpellData(3, "Inferno", "3 by 1 Area in which every piece dies that passes through, can’t be cast on full squares, lasts for two turns.", SpellType.utility, PieceType.fire);
        spellData[11] = new SpellData(3, "Tidal Surge", "Push back all pieces from range plus 2 by two spaces.", SpellType.utility, PieceType.water);
        spellData[12] = new SpellData(4, "Overgrown", "2 by 2 area make pieces skip turn (Range + 1).", SpellType.utility, PieceType.earth);
        spellData[13] = new SpellData(2, "Zephyr Step", "Move to an adjacent tile.", SpellType.utility, PieceType.air);
        spellData[14] = new SpellData(4, "Soul Swap", "Switch two of your own pieces.", SpellType.utility, PieceType.spirit);

        waitingForHuman = false;
        humanTurn = null;
        printDebug = false;
    }

    private void loadMap() {
        String[] halves = "FPPP/FLLL/MLFP/MMMF".split("/");
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

    public void startGame(Window window) {
        boolean player = turnCounter % 1 != 0;
        while(isGameOver() == 2) {
            System.out.println("\nCURRENT TURN: " + turnCounter + " - STG: " + getSpellTokenChange() + " - SA: " + getSpellAmount());
            Player p = getPlayer(player);
            printBoardPieces();
            ArrayList<Turn> possibleTurns = (p.getIsHuman()) ? null : generatePossibleTurns(p);
            waitingForHuman = p.getIsHuman();
            Turn turn = p.fetchTurn(possibleTurns, this);
            System.out.println(((player) ? "\nPlayer1 Turn: (ST: " : "\nPlayer0 Turn:(ST: ") + p.getSpellTokens() + ")");
            turn.print();
            executeTurn(turn, p, window);
            player = !player;
            p.setSpellTokens(p.getSpellTokens() + getSpellTokenChange());
            if(isGameOver() != 2) printBoardPieces();
            window.updateWindow();
            waiter(100);
        }
        switch (isGameOver()) {
            case -1 -> System.out.println("Draw between p0 and p1");
            case 0 -> System.out.println("Win for p0");
            case 1 -> System.out.println("Win for p1");
        }
    }

    private void waiter(int ms) {
        long endTime = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < endTime) {
            Thread.onSpinWait();
        }
    }

    private int getSpellTokenChange() {
        int currentTurnGroup = (int) (turnCounter / 5);
        return 1 + currentTurnGroup;
    }

    double evaluate() {
        double score = 0;
        int over = isGameOver();
        if (over != 2) {
            if (over == -1) return 0;
            if (over == 0) return 99999;
            if (over == 1) return -99999;
        }
        score += evaluateHelper(player0);
        score -= evaluateHelper(player1);
        return score;
    }

    private double evaluateHelper(Player p) {
        double score = 0;
        double spellTokenValue = 20;
        double mageScoreBoost = 1.3;
        double protectedScoreBoost = 0.35;
        double enemyAdjacentReduction = 8.5;
        double guardProtectionBonus = 20;
        double goodTerrain = 100;
        double okayTerrain = goodTerrain * 0.5;
        double badTerrain = okayTerrain * 0.5;

        score += p.getSpellTokens() * spellTokenValue * Math.max(-0.5 * Math.round(turnCounter - 0.5) + 3, 1);
        boolean isGuardProtected = false;

        for (Piece piece: p.getPieces()) {
            if (piece.getXPos() != -1 && piece.getOvergrownTimer() == 0) {
                boolean isMage = piece.getType() != PieceType.guard;
                int pta = pieceTerrainAdvantage(piece.getXPos(), piece.getYPos());
                switch (pta) {
                    case 1 -> score += goodTerrain * ((isMage) ? mageScoreBoost : 1);
                    case 0 -> score += okayTerrain * ((isMage) ? mageScoreBoost : 1);
                    case -1 -> score += badTerrain * ((isMage) ? mageScoreBoost : 1);
                }
                score += getTilesInRange(piece.getXPos(), piece.getYPos(), getMovementRange(piece.getXPos(), piece.getYPos())).size();
                for (Tile tile: getTilesInRange(piece.getXPos(), piece.getYPos(), 1)) {
                    if (tile.getPiece() != null && tile.getPiece().getType() == PieceType.guard && tile.getPiece().getPlayer() == piece.getPlayer() && isMage) {
                        isGuardProtected = true;
                        if (piece.getType() == PieceType.spirit) score += guardProtectionBonus * 0.2;
                    }
                    if (tile.getPiece() != null && tile.getPiece().getPlayer() != piece.getPlayer()) score -= enemyAdjacentReduction / (piece.getAttackProtectedTimer() > 0 ? 2 : 1);
                    if (isMage && pta != 1) if (terrainAdvantage(piece, tile) == 1) {
                        score += goodTerrain * 0.05;
                    }
                }
                for (Tile tile: getTilesInRange(piece.getXPos(), piece.getYPos(), 2)) {
                    if (tile.getPiece() != null && tile.getPiece().getPlayer() != piece.getPlayer()) score -= enemyAdjacentReduction / (piece.getAttackProtectedTimer() > 0 ? 4 : 2);
                    if (isMage && pta == 1) if (terrainAdvantage(piece, tile) == 1) {
                        score += goodTerrain * 0.05;
                    }
                }
                for (Tile tile: getTilesInRange(piece.getXPos(), piece.getYPos(), 2)) {
                    if (tile.getPiece() != null && tile.getPiece().getPlayer() != piece.getPlayer() && tile.getPiece().getType() != PieceType.guard) score -= guardProtectionBonus  * ((piece.getSpellProtectedTimer() > 0 || piece.getSpellReflectionTimer() > 0) ? protectedScoreBoost : 2);
                }
            }
            score += guardProtectionBonus * (isGuardProtected ? 1 : -0.5);
        }

        // TODO try to add a mageOpponent distance or something
        Random random = new Random();
        float randomFloat = 0.95f + (random.nextFloat() * (1.05f - 0.95f));
        return score * randomFloat;
    }

    private ArrayList<Tile> getTilesInRange(int xPos, int yPos, int range) {
        ArrayList<Tile> tilesInRange = new ArrayList<>();

        for (int x = Math.max(0, xPos - range); x <= Math.min(board.length - 1, xPos + range); x++) {
            for (int y = Math.max(0, yPos - range); y <= Math.min(board[0].length - 1, yPos + range); y++) {
                if (x == xPos && y == yPos) {
                    continue; // Skip the tile at (xPos, yPos)
                }
                tilesInRange.add(board[x][y]);
            }
        }

        return tilesInRange;
    }

    public ArrayList<int[]> getTilePositionsInRange(int xPos, int yPos, int range) {
        ArrayList<int[]> tilePositionsInRange = new ArrayList<>();

        for (int x = Math.max(0, xPos - range); x <= Math.min(board.length - 1, xPos + range); x++) {
            for (int y = Math.max(0, yPos - range); y <= Math.min(board[0].length - 1, yPos + range); y++) {
                if (x == xPos && y == yPos) {
                    continue; // Skip the tile at (xPos, yPos)
                }
                tilePositionsInRange.add(new int[]{x, y});
            }
        }

        return tilePositionsInRange;
    }


    private int isGameOver() {
        // Win p0 = 0
        // Win p1 = 1
        // Draw = -1
        // game isn't over = 2
        boolean isOnlyP0Spirit = true;
        boolean isOnlyP1Spirit = true;

        for (Piece piece : player0.getPieces()) {
            if (piece.getType() == PieceType.spirit && piece.getXPos() == -1) {
                return 1; // Player 1 wins
            }
            if (piece.getType() != PieceType.spirit && piece.getXPos() != -1) {
                isOnlyP0Spirit = false; // Player 0 has other pieces on the board
            }
        }

        for (Piece piece : player1.getPieces()) {
            if (piece.getType() == PieceType.spirit && piece.getXPos() == -1) {
                return 0; // Player 0 wins
            }
            if (piece.getType() != PieceType.spirit && piece.getXPos() != -1) {
                isOnlyP1Spirit = false; // Player 1 has other pieces on the board
            }
        }

        if (isOnlyP0Spirit && isOnlyP1Spirit) {
            return -1; // Draw
        }

        return 2; // Game isn't over
    }

    public void executeTurn(Turn turn, Player player, Window window) {
        int ms = 2000;
        if (window != null && turn.move1 != null) {
            window.updateWindow();
            waiter(ms);
        }
        doMove(turn.move1, false);
        if (window != null && turn.move2 != null) {
            window.updateWindow();
            waiter(ms);
        }
        doMove(turn.move2, false);
        if (window != null && turn.move3 != null) {
            window.updateWindow();
            waiter(ms);
        }
        doMove(turn.move3, false);
        resetHasMoved(player);
        updateTimers();
        if (window != null && turn.attack != null) {
            window.updateWindow();
            waiter(ms);
        }
        doAttack(turn.attack);
        if (turn.spells != null) for (TurnSpell spell: turn.spells) {
            if (window != null) {
                window.updateWindow();
                waiter(ms);
            }
            castSpell(spell, player);
        }
        updateTimers();
        turnCounter += 0.5;
    }

    private void castSpell(TurnSpell spell, Player player) {
        if (spell != null) {
            if (!isLegalSpell(spell, player)) throw new IllegalArgumentException("The Spell that was provided is not Legal.");
            spellData[spell.spellDataIndex].castEffect(spell, player, board, getPlayer(!board[spell.xFrom][spell.yFrom].getPiece().getPlayer()));
        }
    }

    public boolean isLegalSpell(TurnSpell spell, Player player) {
        SpellData dataOfSpell = spellData[spell.spellDataIndex];
        Piece mage = board[spell.xFrom][spell.yFrom].getPiece();

        if (printDebug) System.out.println("\nChecking legality of spell: " + dataOfSpell.name);
        if (printDebug) System.out.println("Player spell tokens: " + player.getSpellTokens() + ", Spell cost: " + dataOfSpell.cost);

        if (player.getSpellTokens() < dataOfSpell.cost) return false;
        if (mage == null) {
            if (printDebug) System.out.println("No mage found at the starting position.");
            return false;
        } else {
            if (printDebug) System.out.println("Mage found: " + mage.getType());
            if (mage.getType() == PieceType.guard || getPlayer(mage.getPlayer()) != player || mage.getType() != dataOfSpell.mageType || mage.getOvergrownTimer() > 0) {
                if (printDebug) System.out.println("Mage is either a guard, belongs to the wrong player, is not the right type, or is overgrown.");
                return false;
            }
        }

        if (printDebug) System.out.println(dataOfSpell.spellType);
        switch (dataOfSpell.spellType) {
            case offense -> {
                if (printDebug) System.out.println("Offense spell type detected.");
                if (spell.targets.isEmpty()) {
                    if (printDebug) System.out.println("Spell targets are empty. Returning false.");
                    return false;
                }

                int[] target = spell.targets.getFirst();
                Piece targetPiece = board[target[0]][target[1]].getPiece();

                if (printDebug) System.out.println("Offensive spell target coordinates: " + spell.getTargetsString());
                if (printDebug) System.out.println("Target piece: " + (targetPiece != null ? targetPiece : "None"));
                if (printDebug) System.out.println("Target piece player: " + (targetPiece != null ? targetPiece.getPlayer() : "N/A"));
                if (printDebug) System.out.println("Target piece spell protected timer: " + (targetPiece != null ? targetPiece.getSpellProtectedTimer() : "N/A"));

                boolean isLegal = spell.targets.size() == 1 && targetPiece != null && targetPiece.getPlayer() != mage.getPlayer() &&
                        targetPiece.getSpellProtectedTimer() == 0 && isSpellPathFree(spell);

                if (printDebug) System.out.println("Spell legality: " + isLegal);
                return isLegal;
            }
            case defense -> {
                if (printDebug) System.out.println("defense");
                if (spell.targets.isEmpty()) {
                    if (printDebug) System.out.println("empty targets - true");
                    return true;
                }
                int[] target = spell.targets.getFirst();
                if (printDebug) System.out.println("Defensive spell target: " + spell.getTargetsString());
                return spell.targets.size() == 1 && board[target[0]][target[1]].getPiece() != null &&
                        board[target[0]][target[1]].getPiece().getPlayer() == mage.getPlayer();
            }
            case utility -> {
                if (printDebug) System.out.println("utility - " + dataOfSpell.mageType);
                switch (dataOfSpell.mageType) {
                    case fire -> {
                        if (printDebug) System.out.println("Fire utility spell targets: " + spell.getTargetsString());
                        return spell.targets.size() == 3 &&
                                board[spell.targets.getFirst()[0]][spell.targets.getFirst()[1]].getPiece() == null &&
                                board[spell.targets.get(1)[0]][spell.targets.get(1)[1]].getPiece() == null &&
                                board[spell.targets.get(2)[0]][spell.targets.get(2)[1]].getPiece() == null &&
                                (((spell.targets.getFirst()[0] == spell.targets.get(1)[0]) && (spell.targets.getFirst()[0] == spell.targets.get(2)[0])) &&
                                        (spell.targets.getFirst()[1] == spell.targets.get(1)[1] - 1) && (spell.targets.getFirst()[1] == spell.targets.get(1)[1] - 2)) &&
                                (((spell.targets.getFirst()[1] == spell.targets.get(1)[1]) && (spell.targets.getFirst()[0] == spell.targets.get(2)[1])) &&
                                        (spell.targets.getFirst()[0] == spell.targets.get(1)[0] - 1) && (spell.targets.getFirst()[1] == spell.targets.get(1)[0] - 2));
                    }
                    case water -> {
                        if (printDebug) System.out.println("Water utility spell is always legal.");
                        return true;
                    }
                    case earth -> {
                        if (printDebug) System.out.println("Earth utility spell targets: " + spell.getTargetsString());
                        return spell.targets.size() == 4 &&
                                (spell.targets.get(0)[0] == spell.targets.get(2)[0] &&
                                        spell.targets.get(1)[0] == spell.targets.get(3)[0] &&
                                        spell.targets.get(0)[0] == spell.targets.get(1)[0] - 1 &&
                                        spell.targets.get(2)[0] == spell.targets.get(3)[0] - 1) &&
                                (spell.targets.get(0)[1] == spell.targets.get(1)[1] &&
                                        spell.targets.get(2)[1] == spell.targets.get(3)[1] &&
                                        spell.targets.get(0)[1] == spell.targets.get(2)[1] - 1 &&
                                        spell.targets.get(1)[1] == spell.targets.get(3)[1] - 1);
                    }
                    case air -> {
                        if (printDebug) System.out.println("Air utility spell targets: " + spell.getTargetsString());

                        int[] target = spell.targets.getFirst();
                        Piece targetPiece = board[target[0]][target[1]].getPiece();

                        if (printDebug) System.out.println("Target coordinates: " + Arrays.toString(target));
                        if (printDebug) System.out.println("Target piece: " + (targetPiece != null ? targetPiece : "None"));
                        if (printDebug) System.out.println("Distance from mage (x-axis): " + Math.abs(target[0] - spell.xFrom));
                        if (printDebug) System.out.println("Distance from mage (y-axis): " + Math.abs(target[1] - spell.yFrom));

                        boolean isLegal = spell.targets.size() == 1 && targetPiece == null &&
                                Math.abs(target[0] - spell.xFrom) <= 1 && Math.abs(target[1] - spell.yFrom) <= 1;

                        if (printDebug) System.out.println("Spell legality: " + isLegal);
                        return isLegal;
                    }
                    case spirit -> {
                        if (printDebug) System.out.println("Spirit utility spell targets: " + spell.getTargetsString());
                        return spell.targets.size() == 2 && board[spell.targets.getFirst()[0]][spell.targets.getFirst()[1]].getPiece() != null &&
                                board[spell.targets.get(1)[0]][spell.targets.get(1)[1]].getPiece() != null &&
                                board[spell.targets.getFirst()[0]][spell.targets.getFirst()[1]].getPiece().getPlayer() == mage.getPlayer() &&
                                board[spell.targets.get(1)[0]][spell.targets.get(1)[1]].getPiece().getPlayer() == mage.getPlayer();
                    }
                }
            }
        }
        return false;
    }

    private boolean isSpellPathFree(TurnSpell spell) {
        int xFrom = spell.xFrom;
        int yFrom = spell.yFrom;
        int xTo = spell.targets.getFirst()[0];
        int yTo = spell.targets.getFirst()[1];
        int xChange = xTo - xFrom;
        int yChange = yTo - yFrom;
        int absXChange = Math.abs(xChange);
        int absYChange = Math.abs(yChange);
        int signXChange = (int) Math.signum(xChange);
        int signYChange = (int) Math.signum(yChange);

        // Range 1
        if (absXChange <= 1 && absYChange <= 1) {
            return true;
        }

        // Range 2
        if (absXChange <= 2 && absYChange <= 2) {
            if (absXChange == 2 && absYChange == 2) {
                Piece potentialGuard = board[signXChange + xFrom][signYChange + yFrom].getPiece();
                return checkPotentialGuard(potentialGuard, xFrom, yFrom);
            } else if (absYChange == 2) {
                Piece potentialGuard = board[xFrom][signYChange + yFrom].getPiece();
                return checkPotentialGuard(potentialGuard, xFrom, yFrom);
            } else {
                Piece potentialGuard = board[signXChange + xFrom][yFrom].getPiece();
                return checkPotentialGuard(potentialGuard, xFrom, yFrom);
            }
        }

        // Range 3
        if (absXChange <= 3 && absYChange <= 3) {
            if (absXChange == 3 && absYChange == 3 || absXChange == 3 && absYChange == 2 || absXChange == 2) {
                Piece potentialGuard1 = board[signXChange + xFrom][signYChange + yFrom].getPiece();
                Piece potentialGuard2 = board[2 * signXChange + xFrom][2 * signYChange + yFrom].getPiece();
                return checkPotentialGuard(potentialGuard1, xFrom, yFrom) && checkPotentialGuard(potentialGuard2, xFrom, yFrom);
            } else if (absXChange == 0 || absXChange == 1) {
                Piece potentialGuard1 = board[xFrom][signYChange + yFrom].getPiece();
                Piece potentialGuard2 = board[xFrom][2 * signYChange + yFrom].getPiece();
                return checkPotentialGuard(potentialGuard1, xFrom, yFrom) && checkPotentialGuard(potentialGuard2, xFrom, yFrom);
            } else if ((absXChange == 3 && absYChange == 0) || (absXChange == 3 && absYChange == 1)) {
                Piece potentialGuard1 = board[signXChange + xFrom][yFrom].getPiece();
                Piece potentialGuard2 = board[2 * signXChange + xFrom][yFrom].getPiece();
                return checkPotentialGuard(potentialGuard1, xFrom, yFrom) && checkPotentialGuard(potentialGuard2, xFrom, yFrom);
            }
        }

        // Not in the range of the mage
        return false;
    }

    private boolean checkPotentialGuard(Piece potentialGuard, int xFrom, int yFrom) {
        return potentialGuard == null || potentialGuard.getType() != PieceType.guard || potentialGuard.getPlayer() == board[xFrom][yFrom].getPiece().getPlayer();
    }


    public ArrayList<Turn> generatePossibleTurns(Player player) {
        ArrayList<Turn> possibleTurns = new ArrayList<>();

        int tempSize = 0;
        int breakPoint = 5_000_000;

        // x x x
        int[][] position = fetchPositionPieces();
        possibleTurns.add(new Turn(null, null, null, null, null));
        tempSize = logAndResetState("x x x", tempSize, player, possibleTurns, position);

        // x a x
        position = fetchPositionPieces();
        ArrayList<Attack> possibleAttacks = generatePossibleAttacks(player);
        if (!possibleAttacks.isEmpty()) for (Attack attack: possibleAttacks) {
            if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(null, null, null, attack, null));
        }
        tempSize = logAndResetState("x a x", tempSize, player, possibleTurns, position);

        // x x s
        position = fetchPositionPieces();
        ArrayList<ArrayList<TurnSpell>> possibleSpellCombinations = generatePossibleSpellCombinations(player);
        if (!possibleSpellCombinations.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellCombinations) {
            if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(null, null, null, null, spells));
        }
        tempSize = logAndResetState("x x s", tempSize, player, possibleTurns, position);

        // x a s
        position = fetchPositionPieces();
        if (!possibleAttacks.isEmpty()) for (Attack attack: possibleAttacks) {
            Piece piece = storePieceOfAttack(attack);
            Object[] guardPosition = doAttack(attack);
            possibleSpellCombinations = generatePossibleSpellCombinations(player);
            if (!possibleSpellCombinations.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellCombinations) {
                if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(null, null, null, attack, spells));
            }
            undoAttack(attack, piece, guardPosition);
        }
        tempSize = logAndResetState("x a s", tempSize, player, possibleTurns, position);

        // m x x
        position = fetchPositionPieces();
        ArrayList<Move> possibleMoves = generatePossibleMoves(player);
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, null, null, null, null));
        }
        tempSize = logAndResetState("m x x", tempSize, player, possibleTurns, position);

        // m a x
        position = fetchPositionPieces();
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleAttacks = generatePossibleAttacks(player);
            if (!possibleAttacks.isEmpty()) for (Attack attack: possibleAttacks) {
                if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, null, null, attack, null));
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m a x", tempSize, player, possibleTurns, position);

        // m x s
        position = fetchPositionPieces();
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleSpellCombinations = generatePossibleSpellCombinations(player);
            if (!possibleSpellCombinations.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellCombinations) {
                if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, null, null, null, spells));
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m x s", tempSize, player, possibleTurns, position);

        // m a s
        position = fetchPositionPieces();
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleAttacks = generatePossibleAttacks(player);
            if (!possibleAttacks.isEmpty()) for (Attack attack: possibleAttacks) {
                Piece piece2 = storePieceOfAttack(attack);
                Object[] guardPosition = doAttack(attack);
                possibleSpellCombinations = generatePossibleSpellCombinations(player);
                if (!possibleSpellCombinations.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellCombinations) {
                    if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, null, null, attack, spells));
                }
                undoAttack(attack, piece2, guardPosition);
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m a s", tempSize, player, possibleTurns, position);

        // m m x x
        position = fetchPositionPieces();
        ArrayList<Move> possibleMovesAfterMove1;
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleMovesAfterMove1 = generatePossibleMoves(player);
            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
                if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, null, null, null));
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m m x x", tempSize, player, possibleTurns, position);

        // m m a x
        position = fetchPositionPieces();
        ArrayList<Attack> possibleAttacksAfterMove1;
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleMovesAfterMove1 = generatePossibleMoves(player);
            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
                Piece piece2 = board[move2.xFrom][move2.yFrom].getPiece();
                doMove(move2, false);
                possibleAttacksAfterMove1 = generatePossibleAttacks(player);
                if (!possibleAttacksAfterMove1.isEmpty()) for (Attack attack: possibleAttacksAfterMove1) {
                    if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, null, attack, null));
                }
                undoMove(move2, piece2);
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m m a x", tempSize, player, possibleTurns, position);

        // m m x s
        position = fetchPositionPieces();
        ArrayList<ArrayList<TurnSpell>> possibleSpellsAfterMove1;
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleMovesAfterMove1 = generatePossibleMoves(player);
            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
                Piece piece2 = board[move2.xFrom][move2.yFrom].getPiece();
                doMove(move2, false);
                possibleSpellsAfterMove1 = generatePossibleSpellCombinations(player);
                if (!possibleSpellsAfterMove1.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellsAfterMove1) {
                    if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, null, null, spells));
                }
                undoMove(move2, piece2);
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m m x s", tempSize, player, possibleTurns, position);

        // m m a s
        position = fetchPositionPieces();
        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            doMove(move, false);
            possibleMovesAfterMove1 = generatePossibleMoves(player);
            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
                Piece piece2 = board[move2.xFrom][move2.yFrom].getPiece();
                doMove(move2, false);
                possibleAttacksAfterMove1 = generatePossibleAttacks(player);
                if (!possibleAttacksAfterMove1.isEmpty()) for (Attack attack: possibleAttacksAfterMove1) {
                    Piece piece3 = storePieceOfAttack(attack);
                    Object[] guardPosition = doAttack(attack);
                    possibleSpellsAfterMove1 = generatePossibleSpellCombinations(player);
                    if (!possibleSpellsAfterMove1.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellsAfterMove1) {
                        if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, null, attack, spells));
                    }
                    undoAttack(attack, piece3, guardPosition);
                }
                undoMove(move2, piece2);
            }
            undoMove(move, piece);
        }
        tempSize = logAndResetState("m m a s", tempSize, player, possibleTurns, position);

//        // TODO add piece to undoMove when rejoining this bit
//        // m m m x x
//        position = fetchPositionPieces();
//        ArrayList<Move> possibleMovesAfterMove2;
//        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
//            doMove(move, false);
//            possibleMovesAfterMove1 = generatePossibleMoves(player);
//            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
//                doMove(move2, false);
//                possibleMovesAfterMove2 = generatePossibleMoves(player);
//                if (!possibleMovesAfterMove2.isEmpty()) for (Move move3: possibleMovesAfterMove2) {
//                    if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, move3, null, null));
//                }
//                undoMove(move2);
//            }
//            undoMove(move);
//        }
//        tempSize = logAndResetState("m m m x x", tempSize, player, possibleTurns, position);
//
//        // m m m a x
//        position = fetchPositionPieces();
//        ArrayList<Attack> possibleAttacksAfterMove2;
//        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
//            doMove(move, false);
//            possibleMovesAfterMove1 = generatePossibleMoves(player);
//            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
//                doMove(move2, false);
//                possibleMovesAfterMove2 = generatePossibleMoves(player);
//                if (!possibleMovesAfterMove2.isEmpty()) for (Move move3: possibleMovesAfterMove2) {
//                    doMove(move3, false);
//                    possibleAttacksAfterMove2 = generatePossibleAttacks(player);
//                    if (!possibleAttacksAfterMove2.isEmpty()) for (Attack attack: possibleAttacksAfterMove2) {
//                        if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, move3, attack, null));
//                    }
//                    undoMove(move3);
//                }
//                undoMove(move2);
//            }
//            undoMove(move);
//        }
//        tempSize = logAndResetState("m m m a x", tempSize, player, possibleTurns, position);
//
//        // m m m x s
//        position = fetchPositionPieces();
//        ArrayList<ArrayList<TurnSpell>> possibleSpellsAfterMove2;
//        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
//            doMove(move, false);
//            possibleMovesAfterMove1 = generatePossibleMoves(player);
//            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
//                doMove(move2, false);
//                possibleMovesAfterMove2 = generatePossibleMoves(player);
//                if (!possibleMovesAfterMove2.isEmpty()) for (Move move3: possibleMovesAfterMove2) {
//                    doMove(move3, false);
//                    possibleSpellsAfterMove2 = generatePossibleSpellCombinations(player);
//                    if (!possibleSpellsAfterMove2.isEmpty()) for (ArrayList<TurnSpell> spell: possibleSpellsAfterMove2) {
//                        if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, move3, null, spell));
//                    }
//                    undoMove(move3);
//                }
//                undoMove(move2);
//            }
//            undoMove(move);
//        }
//        tempSize = logAndResetState("m m m x s", tempSize, player, possibleTurns, position);
//
//        // m m m a s
//        position = fetchPositionPieces();
//        if (!possibleMoves.isEmpty()) for (Move move: possibleMoves) {
//            doMove(move, false);
//            possibleMovesAfterMove1 = generatePossibleMoves(player);
//            if (!possibleMovesAfterMove1.isEmpty()) for (Move move2: possibleMovesAfterMove1) {
//                doMove(move2, false);
//                possibleMovesAfterMove2 = generatePossibleMoves(player);
//                if (!possibleMovesAfterMove2.isEmpty()) for (Move move3: possibleMovesAfterMove2) {
//                    doMove(move3, false);
//                    possibleAttacksAfterMove2 = generatePossibleAttacks(player);
//                    if (!possibleAttacksAfterMove2.isEmpty()) for (Attack attack: possibleAttacksAfterMove2) {
//                        Piece piece = storePieceOfAttack(attack);
//                        Object[] guardPosition = doAttack(attack);
//                        possibleSpellsAfterMove2 = generatePossibleSpellCombinations(player);
//                        if (!possibleSpellsAfterMove2.isEmpty()) for (ArrayList<TurnSpell> spells: possibleSpellsAfterMove2) {
//                            if (possibleTurns.size() - tempSize <= breakPoint) possibleTurns.add(new Turn(move, move2, move3, attack, spells));
//                        }
//                        undoAttack(attack, piece, guardPosition);
//                    }
//                    undoMove(move3);
//                }
//                undoMove(move2);
//            }
//            undoMove(move);
//        }
//        logAndResetState("m m m a s", tempSize, player, possibleTurns, position);

        return possibleTurns;
    }

    private int[][] fetchPositionPieces() {
        int[][] positions = new int[20][2];

        Piece[] p0p = player0.getPieces();
        Piece[] p1p = player1.getPieces();

        for (int i = 0; i < 10; i++) {
            positions[i][0] = p0p[i].getXPos();
            positions[i][1] = p0p[i].getYPos();
            positions[i + 10][0] = p1p[i].getXPos();
            positions[i + 10][1] = p1p[i].getYPos();
        }

        return positions;
    }


    private int logAndResetState(String text, int tempSize, Player player, ArrayList<Turn> possibleTurns, int[][] position) {
        setPiecesPosition(position);
        resetHasMoved(player);
        System.out.println("(" + text + ") - " + (possibleTurns.size() - tempSize));
        return possibleTurns.size();
    }
    private void setPiecesPosition(int[][] position) {
        Piece[] p0p = player0.getPieces();
        Piece[] p1p = player1.getPieces();

        setPiece(position[0][0], position[0][1], p0p[0]);
        setPiece(position[1][0], position[1][1], p0p[1]);
        setPiece(position[2][0], position[2][1], p0p[2]);
        setPiece(position[3][0], position[3][1], p0p[3]);
        setPiece(position[4][0], position[4][1], p0p[4]);

        setPiece(position[10][0], position[10][1], p1p[0]);
        setPiece(position[11][0], position[11][1], p1p[1]);
        setPiece(position[12][0], position[12][1], p1p[2]);
        setPiece(position[13][0], position[13][1], p1p[3]);
        setPiece(position[14][0], position[14][1], p1p[4]);

        setPiece(position[5][0], position[5][1], p0p[5]);
        setPiece(position[6][0], position[6][1], p0p[6]);
        setPiece(position[7][0], position[7][1], p0p[7]);
        setPiece(position[8][0], position[8][1], p0p[8]);
        setPiece(position[9][0], position[9][1], p0p[9]);

        setPiece(position[15][0], position[15][1], p1p[5]);
        setPiece(position[16][0], position[16][1], p1p[6]);
        setPiece(position[17][0], position[17][1], p1p[7]);
        setPiece(position[18][0], position[18][1], p1p[8]);
        setPiece(position[19][0], position[19][1], p1p[9]);
    }

    public Object[] doAttack(Attack attack) {
        if (attack != null) {
            if (!isLegalAttack(attack)) throw new IllegalArgumentException("The Attack that was provided is not Legal.\nxFrom: " + attack.xFrom + "\nyFrom: " + attack.yFrom + "\nxChange: " + attack.xChange + "\nyChange: " + attack.yChange);
            Piece attackingPiece = board[attack.xFrom][attack.yFrom].getPiece();
            Piece defendingPiece = board[attack.xFrom + attack.xChange][attack.yFrom + attack.yChange].getPiece();
            Piece protectingGuard = (defendingPiece.getType() != PieceType.guard && !guardSkip(attack)) ? findProtectingGuard(attack) : null;
            if (protectingGuard == null) {
                if (attackingPiece.getType() == PieceType.guard || defendingPiece.getType() != PieceType.guard) {
                    defendingPiece.setPosition(-1, -1);
                    setPiece(attack.xFrom + attack.xChange, attack.yFrom + attack.yChange, attackingPiece);
                    setPiece(attack.xFrom, attack.yFrom, null);
                } else if (attackingPiece.getType() != PieceType.guard && defendingPiece.getType() == PieceType.guard) {
                    defendingPiece.setPosition(-1, -1);
                    attackingPiece.setPosition(-1, -1);
                    setPiece(attack.xFrom + attack.xChange, attack.yFrom + attack.yChange, null);
                    setPiece(attack.xFrom, attack.yFrom, null);
                    return new Object[]{attack.xFrom, attack.yFrom, attackingPiece};
                } else {
                    throw new IllegalStateException("How the fuck did we get here...");
                }
            } else {
                setPiece(protectingGuard.getXPos(), protectingGuard.getYPos(), null);
                Object[] guardPosition = {protectingGuard.getXPos(), protectingGuard.getYPos(), protectingGuard};
                protectingGuard.setPosition(-1, -1);
                return guardPosition;
            }
        }
        return new Object[]{-1, -1, null};
    }

    private void undoAttack(Attack attack, Piece piece, Object[] guardPosition) {
        if (guardPosition[0].equals(-1)) {
            Move move = new Move(attack.xFrom + attack.xChange, attack.yFrom + attack.yChange, -attack.xChange, -attack.yChange);
            doMove(move, true);
            setPiece(move.xFrom, move.yFrom, piece);
        } else {
            Piece guardPiece = (Piece) guardPosition[2];
            if (guardPiece.getType() == PieceType.guard) {
                setPiece((Integer) guardPosition[0], (Integer) guardPosition[1], guardPiece);
            } else {
                setPiece((Integer) guardPosition[0], (Integer) guardPosition[1], guardPiece);
                setPiece(attack.xFrom + attack.xChange, attack.yFrom + attack.yChange, piece);
            }
        }
    }

    private Piece storePieceOfAttack(Attack attack) {
        return board[attack.xFrom + attack.xChange][attack.yFrom + attack.yChange].getPiece();
    }

    private void undoMove(Move move, Piece piece) {
        setPiece(move.xFrom, move.yFrom, piece);
        setPiece(move.xFrom + move.xChange, move.yFrom + move.yChange, null);
        piece.setHasMoved(false);
    }

    public ArrayList<ArrayList<TurnSpell>> generatePossibleSpellCombinations(Player player) {
        ArrayList<ArrayList<TurnSpell>> possibleSpellCombinations = new ArrayList<>();
        int maxSpells = getSpellAmount();

        generateSpellCombo(player, maxSpells, possibleSpellCombinations, new ArrayList<>());

        return possibleSpellCombinations;
    }

    private void generateSpellCombo(Player player, int maxSpells, ArrayList<ArrayList<TurnSpell>> possibleSpellCombinations, ArrayList<TurnSpell> currentCombination) {
        int availableTokens = player.getSpellTokens();

        ArrayList<TurnSpell> allVariations;
        for (int spellIndex = 0; spellIndex < spellData.length; spellIndex++) {
            SpellData dataOfSpell = spellData[spellIndex];
            for (Piece piece : player.getPieces()) {
                if (piece.getXPos() == -1 || piece.getType() == PieceType.guard || piece.getOvergrownTimer() > 0) continue;
                if (dataOfSpell.mageType == piece.getType() && dataOfSpell.cost <= availableTokens) {
                    TurnSpell baseVariation = new TurnSpell(spellIndex, piece.getXPos(), piece.getYPos(), new ArrayList<>());
                    if (board[piece.getXPos()][piece.getYPos()].getPiece() == null) {
                        System.out.println("Piece and Board are not adding up - ms" + maxSpells);
                        printBoardPieces();
                        System.out.println("Previous spell:");
                        System.out.println(spellData[currentCombination.getFirst().spellDataIndex].name + " - (" + currentCombination.getFirst().xFrom + ", " + currentCombination.getFirst().yFrom + ") - " + currentCombination.getFirst().getTargetsString());
                        System.out.println("This spell:");
                        System.out.println(spellData[baseVariation.spellDataIndex].name + " - (" + baseVariation.xFrom + ", " + baseVariation.yFrom + ") - " + baseVariation.getTargetsString());
                        System.out.println("\nPlayer0");
                        for (Piece p: getPlayer(false).getPieces()) {
                            System.out.println(p.getType() + "(" + p.getXPos() + ", " + p.getYPos() + ")");
                        }
                        System.out.println("\nPlayer1");
                        for (Piece p: getPlayer(true).getPieces()) {
                            System.out.println(p.getType() + "(" + p.getXPos() + ", " + p.getYPos() + ")");
                        }
                    }
                    allVariations = generateVariations(baseVariation);
                    allVariations.removeIf(variation -> variation.targets.isEmpty());

                    for (TurnSpell variation : allVariations) {
                        ArrayList<TurnSpell> newCombination = new ArrayList<>(currentCombination);
                        newCombination.add(variation);

                        possibleSpellCombinations.add(newCombination);
                        if (maxSpells > 1) {
                            Game gameState = copyGameState();
                            castSpell(variation, player);
                            generateSpellCombo(player, maxSpells - 1, possibleSpellCombinations, newCombination);
                            loadGameState(gameState);
                        }
                    }
                }
            }
        }
    }

    private ArrayList<TurnSpell> generateVariations(TurnSpell baseVariation) {
        ArrayList<TurnSpell> allVariations = new ArrayList<>();
        SpellData dataOfSpell = spellData[baseVariation.spellDataIndex];
        switch (dataOfSpell.spellType) {
            case offense -> {
                for (int[] tilePosition: getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, getSpellRange(baseVariation.xFrom, baseVariation.yFrom))) {
                    Tile tile = board[tilePosition[0]][tilePosition[1]];
                    if (tile.getPiece() == null || tile.getPiece().getPlayer() == board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()) continue;
                    ArrayList<int[]> targets = new ArrayList<>();
                    targets.add(new int[]{tilePosition[0], tilePosition[1]});
                    TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                    if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) allVariations.add(variation);
                }
            }
            case defense -> {
                for (int[] tilePosition: getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, getSpellRange(baseVariation.xFrom, baseVariation.yFrom))) {
                    Tile tile = board[tilePosition[0]][tilePosition[1]];
                    if (tile.getPiece() == null || tile.getPiece().getPlayer() != board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer() || tile.getPiece().getType() != PieceType.guard) continue;
                    ArrayList<int[]> targets = new ArrayList<>();
                    targets.add(new int[]{tilePosition[0], tilePosition[1]});
                    TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                    if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) allVariations.add(variation);
                }
                TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, new ArrayList<>());
                if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) allVariations.add(variation);
            }
            case utility -> {
                switch (dataOfSpell.mageType) {
                    case fire -> {
                        // Horizontal 3x1
                        for (int[] tilePosition : getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, getSpellRange(baseVariation.xFrom, baseVariation.yFrom))) {
                            int x = tilePosition[0];
                            int y = tilePosition[1];
                            if (x + 2 <= 7) {
                                Tile tile1 = board[x][y];
                                Tile tile2 = board[x + 1][y];
                                Tile tile3 = board[x + 2][y];
                                if (tile1.getPiece() == null && tile2.getPiece() == null && tile3.getPiece() == null) {
                                    ArrayList<int[]> targets = new ArrayList<>();
                                    targets.add(new int[]{x, y});
                                    targets.add(new int[]{x + 1, y});
                                    targets.add(new int[]{x + 2, y});
                                    TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                                    if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) {
                                        allVariations.add(variation);
                                    }
                                }
                            }
                        }
                        // Vertical 1x3
                        for (int[] tilePosition : getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, getSpellRange(baseVariation.xFrom, baseVariation.yFrom))) {
                            int x = tilePosition[0];
                            int y = tilePosition[1];
                            if (y + 2 <= 7) {
                                Tile tile1 = board[x][y];
                                Tile tile2 = board[x][y + 1];
                                Tile tile3 = board[x][y + 2];
                                if (tile1.getPiece() == null && tile2.getPiece() == null && tile3.getPiece() == null) {
                                    ArrayList<int[]> targets = new ArrayList<>();
                                    targets.add(new int[]{x, y});
                                    targets.add(new int[]{x, y + 1});
                                    targets.add(new int[]{x, y + 2});
                                    TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                                    if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) {
                                        allVariations.add(variation);
                                    }
                                }
                            }
                        }
                    }
                    case water -> {
                        ArrayList<int[]> targets = new ArrayList<>();
                        for (int[] tilePosition : getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, getSpellRange(baseVariation.xFrom, baseVariation.yFrom) + 2)) {
                            int x = tilePosition[0];
                            int y = tilePosition[1];
                            Piece piece = board[x][y].getPiece();
                            if (piece != null && piece.getPlayer() != board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer() && piece.getSpellProtectedTimer() == 0) {
                                targets.add(new int[]{x, y});
                            }
                        }
                        TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                        if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) {
                            allVariations.add(variation);
                        }
                    }
                    case earth -> {
                        for (int[] tilePosition : getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, getSpellRange(baseVariation.xFrom, baseVariation.yFrom) + 1)) {
                            int x = tilePosition[0];
                            int y = tilePosition[1];
                            if (x + 1 <= 7 && y + 1 <= 7) {
                                boolean hasEnemyPiece = false;
                                ArrayList<int[]> targets = new ArrayList<>();
                                targets.add(new int[]{x, y});
                                targets.add(new int[]{x + 1, y});
                                targets.add(new int[]{x, y + 1});
                                targets.add(new int[]{x + 1, y + 1});
                                for (int[] target : targets) {
                                    Tile tile = board[target[0]][target[1]];
                                    if (tile.getPiece() != null && tile.getPiece().getPlayer() != board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()) {
                                        hasEnemyPiece = true;
                                        break;
                                    }
                                }
                                if (hasEnemyPiece) {
                                    TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                                    if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) {
                                        allVariations.add(variation);
                                    }
                                }
                            }
                        }
                    }
                    case air -> {
                        for (int[] tilePosition: getTilePositionsInRange(baseVariation.xFrom, baseVariation.yFrom, 1)) {
                            Tile tile = board[tilePosition[0]][tilePosition[1]];
                            if (tile.getPiece() != null) continue;
                            ArrayList<int[]> targets = new ArrayList<>();
                            targets.add(new int[]{tilePosition[0], tilePosition[1]});
                            TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                            if (isLegalSpell(variation, getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer()))) allVariations.add(variation);
                        }
                    }
                    case spirit -> {
                        Player player = getPlayer(board[baseVariation.xFrom][baseVariation.yFrom].getPiece().getPlayer());
                        ArrayList<int[]> playerPieces = new ArrayList<>();
                        for (Piece piece : player.getPieces()) {
                            if (piece.getXPos() != -1) {
                                playerPieces.add(new int[]{piece.getXPos(), piece.getYPos()});
                            }
                        }
                        for (int i = 0; i < playerPieces.size(); i++) {
                            for (int j = i + 1; j < playerPieces.size(); j++) {
                                int[] piece1 = playerPieces.get(i);
                                int[] piece2 = playerPieces.get(j);
                                boolean existsInverse = false;
                                for (TurnSpell existingVariation : allVariations) {
                                    if (spellData[existingVariation.spellDataIndex].name.equals(dataOfSpell.name) &&
                                            existingVariation.targets.size() == 2 &&
                                            (Arrays.equals(existingVariation.targets.get(0), piece2) &&
                                            Arrays.equals(existingVariation.targets.get(1), piece1)) ||
                                            (Arrays.equals(existingVariation.targets.get(1), piece2) &&
                                            Arrays.equals(existingVariation.targets.get(0), piece1))) {
                                        existsInverse = true;
                                        break;
                                    }
                                }
                                if (!existsInverse) {
                                    ArrayList<int[]> targets = new ArrayList<>();
                                    targets.add(piece1);
                                    targets.add(piece2);
                                    TurnSpell variation = new TurnSpell(baseVariation.spellDataIndex, baseVariation.xFrom, baseVariation.yFrom, targets);
                                    if (isLegalSpell(variation, player)) {
                                        allVariations.add(variation);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return allVariations;
    }

    private int getSpellAmount() {
        int currentTurnGroup = (int) (turnCounter / 10);
        return 2 + currentTurnGroup;
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
            piece.setOvergrownTimer(Math.max(piece.getOvergrownTimer() - 0.25, 0));
        }
        for (Piece piece: player0.getPieces()) {
            piece.setAttackProtectedTimer(Math.max(piece.getAttackProtectedTimer() - 0.25, 0));
            piece.setSpellProtectedTimer(Math.max(piece.getSpellProtectedTimer() - 0.25, 0));
            piece.setSpellReflectionTimer(Math.max(piece.getSpellReflectionTimer() - 0.25, 0));
            piece.setOvergrownTimer(Math.max(piece.getOvergrownTimer() - 0.25, 0));
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
            if (piece.getXPos() == -1 || piece.getOvergrownTimer() > 0) continue;
            int xPos = piece.getXPos();
            int yPos = piece.getYPos();
            int range = getMovementRange(xPos, yPos);

            for (int xChange = -range; xChange <= range; xChange++) {
                for (int yChange = -range; yChange <= range; yChange++) {
                    if (xChange != 0 || yChange != 0) {
                        int newX = xPos + xChange;
                        int newY = yPos + yChange;
                        if (newX >= 0 && newX <= 7 && newY >= 0 && newY <= 7) {
                            Move move = new Move(xPos, yPos, xChange, yChange);
                            if (isLegalMove(move, false)) {
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
            if (piece.getXPos() == -1 || piece.getOvergrownTimer() > 0) continue;
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

    private int getMovementRange(int xPos, int yPos) {
        return (pieceTerrainAdvantage(xPos, yPos) == 1) ? 2 : 1;
    }

    private int getSpellRange(int xPos, int yPos) {
        int advantage = pieceTerrainAdvantage(xPos, yPos);
        return (advantage == 1) ? 3 : (advantage == 0) ? 2 : 1;
    }

    public void doMove(Move move, boolean debug) {
        if (move != null) {
            if (!isLegalMove(move, debug)) throw new IllegalArgumentException("The Move that was provided is not Legal.\nxFrom: " + move.xFrom + "\nyFrom: " + move.yFrom + "\nxChange: " + move.xChange + "\nyChange: " + move.yChange);
            Piece piece = board[move.xFrom][move.yFrom].getPiece();
            if (board[move.xFrom + move.xChange][move.yFrom + move.yChange].getDeathTimer() > 0) {
                piece.setPosition(-1, -1);
                setPiece(move.xFrom, move.yFrom, null);
            } else {
                setPiece(move.xFrom + move.xChange, move.yFrom + move.yChange, piece);
                setPiece(move.xFrom, move.yFrom, null);
            }
            if (!debug) piece.setHasMoved(true);
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
                if (potentialGuard != null && potentialGuard.getType() == PieceType.guard && potentialGuard.getPlayer() != board[attack.xFrom][attack.yFrom].getPiece().getPlayer()) {
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

    public boolean isLegalAttack(Attack attack) {
        if (attack == null) return true;
        if (board[attack.xFrom][attack.yFrom].getPiece().getOvergrownTimer() > 0) return false;
        int range = getMovementRange(attack.xFrom, attack.yFrom);
        if (range == 1 || Math.abs(attack.xChange) == 1 && Math.abs(attack.yChange) == 1 || Math.abs(attack.xChange) == 0 && Math.abs(attack.yChange) == 1 || Math.abs(attack.xChange) == 1 && Math.abs(attack.yChange) == 0) {
            Piece attackingPiece = board[attack.xFrom][attack.yFrom].getPiece();
            Piece defendingPiece = board[attack.xFrom + attack.xChange][attack.yFrom + attack.yChange].getPiece();
            return defendingPiece != null && defendingPiece.getPlayer() != attackingPiece.getPlayer() && defendingPiece.getAttackProtectedTimer() == 0;
        }
        return false;
    }

    public boolean isLegalMove(Move move, boolean debug) {
        if (move == null) return true;
        if ((board[move.xFrom][move.yFrom].getPiece().hasMoved() || board[move.xFrom][move.yFrom].getPiece().getOvergrownTimer() > 0) && !debug) return false;
        int range = getMovementRange(move.xFrom, move.yFrom);
        if (range == 1 || Math.abs(move.xChange) == 1 && Math.abs(move.yChange) == 1 || Math.abs(move.xChange) == 0 && Math.abs(move.yChange) == 1 || Math.abs(move.xChange) == 1 && Math.abs(move.yChange) == 0) {
            return board[move.xFrom + move.xChange][move.yFrom + move.yChange].getPiece() == null && board[move.xFrom + move.xChange][move.yFrom + move.yChange].getBlockedTimer() == 0;
        } else {
            return Math.abs(move.xChange) <= range && Math.abs(move.yChange) <= range && board[move.xFrom + move.xChange][move.yFrom + move.yChange].getPiece() == null && board[move.xFrom + move.xChange][move.yFrom + move.yChange].getBlockedTimer() == 0 && isNoPieceBetween(move);
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

    public int pieceTerrainAdvantage(int x, int y) {
        if (board[x][y].getPiece() == null) throw new IllegalArgumentException("There is no Piece on x" + x + " y" + y + ".");
        PieceType type = board[x][y].getPiece().getType();
        if (type == PieceType.guard || type == PieceType.spirit) return 0;
        return terrainAdvantage(board[x][y].getPiece(), board[x][y]);
    }

    private int terrainAdvantage(Piece piece, Tile tile) {
        Terrain terrain = tile.getTerrain();
        switch (piece.getType()) {
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
        if (x >= 0 && x < 8 && y >= 0 && y < 8) board[x][y].setPiece(piece);
        if (piece != null) piece.setPosition(x, y);
    }

    public void printBoardPieces() {
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

    public Game copyGameState() {
        Game gameState = new Game();

        // Copy pieces for player 0
        Piece[] player0Pieces = new Piece[10];
        for (int i = 0; i < player0.getPieces().length; i++) {
            Piece copiedPiece = getCopiedPiece(player0, i);
            player0Pieces[i] = copiedPiece;
        }
        Player newPlayer0 = new Player(player0Pieces, player0.getIsHuman());
        newPlayer0.setSpellTokens(player0.getSpellTokens());
        gameState.player0 = newPlayer0;

        // Copy pieces for player 1
        Piece[] player1Pieces = new Piece[10];
        for (int i = 0; i < player1.getPieces().length; i++) {
            Piece copiedPiece = getCopiedPiece(player1, i);
            player1Pieces[i] = copiedPiece;
        }
        Player newPlayer1 = new Player(player1Pieces, player1.getIsHuman());
        newPlayer1.setSpellTokens(player1.getSpellTokens());
        gameState.player1 = newPlayer1;

        // All board tiles
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = new Tile(board[x][y].getTerrain());
                tile.setBlockedTimer(board[x][y].getBlockedTimer());
                tile.setDeathTimer(board[x][y].getDeathTimer());

                Piece originalPiece = board[x][y].getPiece();
                if (originalPiece != null) {
                    Piece copiedPiece;
                    if (originalPiece.getPlayer()) {
                        copiedPiece = findCopiedPiece(player1Pieces, originalPiece);
                    } else {
                        copiedPiece = findCopiedPiece(player0Pieces, originalPiece);
                    }
                    tile.setPiece(copiedPiece);
                }

                gameState.board[x][y] = tile;
            }
        }

        // Current Turn
        gameState.turnCounter = turnCounter;

        return gameState;
    }

    private Piece getCopiedPiece(Player player, int i) {
        Piece originalPiece = player.getPieces()[i];
        Piece copiedPiece = new Piece(originalPiece.getType(), originalPiece.getPlayer());
        copiedPiece.setPosition(originalPiece.getXPos(), originalPiece.getYPos());
        copiedPiece.setHasMoved(originalPiece.hasMoved());
        copiedPiece.setAttackProtectedTimer(originalPiece.getAttackProtectedTimer());
        copiedPiece.setSpellProtectedTimer(originalPiece.getSpellProtectedTimer());
        copiedPiece.setSpellReflectionTimer(originalPiece.getSpellReflectionTimer());
        copiedPiece.setOvergrownTimer(originalPiece.getOvergrownTimer());
        return copiedPiece;
    }

    private Piece findCopiedPiece(Piece[] pieces, Piece originalPiece) {
        for (Piece piece : pieces) {
            if (piece.getType() == originalPiece.getType() &&
                    piece.getXPos() == originalPiece.getXPos() &&
                    piece.getYPos() == originalPiece.getYPos()) {
                return piece;
            }
        }
        return null;
    }

    public void loadGameState(Game gameState) {
        // Load player 0 pieces
        Piece[] player0Pieces = gameState.player0.getPieces();
        for (int i = 0; i < player0Pieces.length; i++) {
            Piece loadedPiece = player0Pieces[i];
            Piece originalPiece = player0.getPieces()[i];
            originalPiece.setPosition(loadedPiece.getXPos(), loadedPiece.getYPos());
            originalPiece.setHasMoved(loadedPiece.hasMoved());
            originalPiece.setAttackProtectedTimer(loadedPiece.getAttackProtectedTimer());
            originalPiece.setSpellProtectedTimer(loadedPiece.getSpellProtectedTimer());
            originalPiece.setSpellReflectionTimer(loadedPiece.getSpellReflectionTimer());
            originalPiece.setOvergrownTimer(loadedPiece.getOvergrownTimer());
        }
        player0.setSpellTokens(gameState.player0.getSpellTokens());

        // Load player 1 pieces
        Piece[] player1Pieces = gameState.player1.getPieces();
        for (int i = 0; i < player1Pieces.length; i++) {
            Piece loadedPiece = player1Pieces[i];
            Piece originalPiece = player1.getPieces()[i];
            originalPiece.setPosition(loadedPiece.getXPos(), loadedPiece.getYPos());
            originalPiece.setHasMoved(loadedPiece.hasMoved());
            originalPiece.setAttackProtectedTimer(loadedPiece.getAttackProtectedTimer());
            originalPiece.setSpellProtectedTimer(loadedPiece.getSpellProtectedTimer());
            originalPiece.setSpellReflectionTimer(loadedPiece.getSpellReflectionTimer());
            originalPiece.setOvergrownTimer(loadedPiece.getOvergrownTimer());
        }
        player1.setSpellTokens(gameState.player1.getSpellTokens());

        // Load board tiles
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Tile tile = board[x][y];
                Tile loadedTile = gameState.board[x][y];

                tile.setBlockedTimer(loadedTile.getBlockedTimer());
                tile.setDeathTimer(loadedTile.getDeathTimer());

                Piece loadedPiece = loadedTile.getPiece();
                if (loadedPiece != null) {
                    Piece originalPiece;
                    if (loadedPiece.getPlayer()) {
                        originalPiece = findCopiedPiece(player1.getPieces(), loadedPiece);
                    } else {
                        originalPiece = findCopiedPiece(player0.getPieces(), loadedPiece);
                    }
                    tile.setPiece(originalPiece);
                } else {
                    tile.setPiece(null);
                }
            }
        }

        // Load current turn counter
        this.turnCounter = gameState.turnCounter;
    }

    public double getTurnCounter() {
        return turnCounter;
    }

    public void setTurnCounter(double turnCounter) {
        this.turnCounter = turnCounter;
    }

    public boolean isWaitingForHuman() {
        return waitingForHuman;
    }

    public Turn getHumanTurn() {
        return humanTurn;
    }

    public void setHumanTurn(Turn turn) {
        humanTurn = turn;
        waitingForHuman = false;
    }

    public void doDBT(Window window, String turnStr) {
        // Fetch player and state turn (ST) from the string
        Pattern playerPattern = Pattern.compile("Player(\\d+) Turn:\\s*\\(ST:\\s*(\\d+)\\)");
        Matcher playerMatcher = playerPattern.matcher(turnStr);
        int playerNum = -1;
        if (playerMatcher.find()) {
            playerNum = Integer.parseInt(playerMatcher.group(1));
        }

        // Fetch moves from the string
        ArrayList<Move> moves = new ArrayList<>();
        Pattern movePattern = Pattern.compile("Move\\((\\d+),\\s*(\\d+),\\s*([-\\d]+),\\s*([-\\d]+)\\)");
        Matcher moveMatcher = movePattern.matcher(turnStr);
        while (moveMatcher.find()) {
            int x = Integer.parseInt(moveMatcher.group(1));
            int y = Integer.parseInt(moveMatcher.group(2));
            int dx = Integer.parseInt(moveMatcher.group(3));
            int dy = Integer.parseInt(moveMatcher.group(4));
            moves.add(new Move(x, y, dx, dy));
        }

        // Fetch attacks from the string
        ArrayList<Attack> attacks = new ArrayList<>();
        Pattern attackPattern = Pattern.compile("Attack\\((\\d+),\\s*(\\d+),\\s*([-\\d]+),\\s*([-\\d]+)\\)");
        Matcher attackMatcher = attackPattern.matcher(turnStr);
        while (attackMatcher.find()) {
            int x = Integer.parseInt(attackMatcher.group(1));
            int y = Integer.parseInt(attackMatcher.group(2));
            int dx = Integer.parseInt(attackMatcher.group(3));
            int dy = Integer.parseInt(attackMatcher.group(4));
            attacks.add(new Attack(x, y, dx, dy));
        }

        // Fetch spells from the string
        ArrayList<TurnSpell> spells = new ArrayList<>();
        Pattern spellPattern = Pattern.compile("TurnSpell\\((\\d+),\\s*(\\d+),\\s*(\\d+),\\s*\\[(.*?)]\\)");
        Matcher spellMatcher = spellPattern.matcher(turnStr);
        while (spellMatcher.find()) {
            int sdi = Integer.parseInt(spellMatcher.group(1));
            int x = Integer.parseInt(spellMatcher.group(2));
            int y = Integer.parseInt(spellMatcher.group(3));
            String targetStr = spellMatcher.group(4);

            ArrayList<int[]> targets = new ArrayList<>();
            Pattern coordPattern = Pattern.compile("\\((\\d+),\\s*(\\d+)\\)");
            Matcher coordMatcher = coordPattern.matcher(targetStr);
            while (coordMatcher.find()) {
                int tx = Integer.parseInt(coordMatcher.group(1));
                int ty = Integer.parseInt(coordMatcher.group(2));
                targets.add(new int[]{tx, ty});
            }

            spells.add(new TurnSpell(sdi, x, y, targets));
        }

        // Create the Turn object
        Turn turn = new Turn(
                !moves.isEmpty() ? moves.getFirst() : null,
                moves.size() > 1 ? moves.get(1) : null,
                moves.size() > 2 ? moves.get(2) : null,
                !attacks.isEmpty() ? attacks.getFirst() : null,
                spells
        );

        // Execute the turn
        Player player = (playerNum == 1) ? getPlayer(true) : getPlayer(false);
        executeTurn(turn, player, window);
        player.setSpellTokens(player.getSpellTokens() + getSpellTokenChange());
    }
}
