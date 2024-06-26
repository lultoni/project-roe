public class Game {
    private Tile[][] board;
    private Player player0;
    private Player player1;

    public Game() {
        board = new Tile[8][8];
        loadMap("FPPP/FLLL/MLFP/MMMF");

        boolean p = false;
        Piece[] p0p = new Piece[]{
                new Piece(PieceType.fire, p),
                new Piece(PieceType.water, p),
                new Piece(PieceType.spirit, p),
                new Piece(PieceType.earth, p),
                new Piece(PieceType.air, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p)};
        p = true;
        Piece[] p1p = new Piece[]{
                new Piece(PieceType.fire, p),
                new Piece(PieceType.water, p),
                new Piece(PieceType.spirit, p),
                new Piece(PieceType.earth, p),
                new Piece(PieceType.air, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p),
                new Piece(PieceType.guard, p)};
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
            Terrain terrain;
            switch (cellType) {
                case 'P':
                    terrain = Terrain.plains;
                    break;
                case 'F':
                    terrain = Terrain.forest;
                    break;
                case 'M':
                    terrain = Terrain.mountain;
                    break;
                case 'L':
                    terrain = Terrain.lake;
                    break;
                default:
                    throw new IllegalArgumentException("Invalid terrain symbol: " + cellType);
            }

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

    private void setPiece(int x, int y, Piece piece) {
        board[x][y].setPiece(piece);
        piece.setPosition(x, y);
    }

    public void printBoardTerrain() {
        System.out.println("BoardTerrain:");
        for (int y = 0; y < 8; y++) {
            String out = "";
            for (int x = 0; x < 8; x++) {
                String addition = " - ";
                switch (board[x][y].getTerrain()) {
                    case Terrain.forest:
                        addition = " F ";
                        break;
                    case Terrain.plains:
                        addition = " P ";
                        break;
                    case Terrain.mountain:
                        addition = " M ";
                        break;
                    case Terrain.lake:
                        addition = " L ";
                        break;
                }
                out += addition;
            }
            System.out.println(out);
        }
    }

    public void printBoardCoordinates() {
        System.out.println("BoardTerrain:");
        for (int y = 0; y < 8; y++) {
            String out = "";
            for (int x = 0; x < 8; x++) {
                out += " x" + x + "y" + y + " ";
            }
            System.out.println(out);
        }
    }

    public void printBoardPieces(boolean player) {
        System.out.println("BoardPieces of " + (player ? "1" : "0") + ":");
        for (int y = 0; y < 8; y++) {
            String out = "";
            for (int x = 0; x < 8; x++) {
                String addition = " - ";
                if (board[x][y].getPiece() != null && board[x][y].getPiece().getPlayer() == player) switch (board[x][y].getPiece().getType()) {
                    case PieceType.air:
                        addition = " A ";
                        break;
                    case PieceType.spirit:
                        addition = " S ";
                        break;
                    case PieceType.earth:
                        addition = " E ";
                        break;
                    case PieceType.fire:
                        addition = " F ";
                        break;
                    case PieceType.guard:
                        addition = " G ";
                        break;
                    case PieceType.water:
                        addition = " W ";
                        break;
                }
                out += addition;
            }
            System.out.println(out);
        }
    }
}
