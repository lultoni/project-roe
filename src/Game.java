public class Game {
    private Tile[][] board;

    public Game() {
        board = new Tile[8][8];
        loadMap("FPPP/FLLL/MLFP/MMMF");
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

            board[i / 8][i % 8] = new Tile(terrain);
        }
    }

    public void printBoardTerrain() {
        System.out.println("BoardTerrain:");
        for (int x = 0; x < 8; x++) {
            String out = "";
            for (int y = 0; y < 8; y++) {
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
}
