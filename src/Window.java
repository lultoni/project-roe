import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Window extends JFrame {
    private Game game;
    JTextField move1 = new JTextField();
    JTextField move2 = new JTextField();
    JTextField move3 = new JTextField();
    JTextField attack = new JTextField();
    JTextField spells = new JTextField();
    JLabel[][] boardDisplay = new JLabel[8][8];

    public Window(Game game) {
        this.game = game;
        setTitle("project-roe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(10, 10, 1000, 600);
        init();
        setVisible(true);
    }

    private void init() {
        setLayout(new GridLayout(1, 0));

        JPanel boardPanel = new JPanel(new GridLayout(8, 8));

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                String terrainLocation = "";
                Terrain tileType = game.board[x][y].getTerrain();

                switch (tileType) {
                    case lake -> terrainLocation = "LakeSprite.png";
                    case mountain -> terrainLocation = "MountainSprite.png";
                    case forest -> terrainLocation = "ForrestSprite.png";
                    case plains -> terrainLocation = "PlainsSprite.png";
                }

                Image before_ter = loadImage(terrainLocation);
                int width = 70;
                int height = 70;
                if (getWidth() > 0 && getHeight() > 0) {
                    double dif = 1;
                    width = (int) (((getWidth() / 2) / 8) * dif);
                    height = (int) ((getHeight() / 8) * dif);
                }

                BufferedImage combinedImage;
                if (game.board[x][y].getPiece() != null) {
                    Image before_pie = getPieceSprite(game.board[x][y].getPiece());
                    combinedImage = combineImages(before_ter, before_pie, width, height);
                } else {
                    combinedImage = resizeImage(before_ter, width, height);
                }

                boardDisplay[x][y] = new JLabel(new ImageIcon(combinedImage));
                boardPanel.add(boardDisplay[x][y]);
            }
        }

        JPanel turnPanel = new JPanel(new GridLayout(1, 5));

        JPanel m1P = new JPanel(new GridLayout(0, 1));
        m1P.add(new JLabel("Move 1:"));
        m1P.add(move1);
        JPanel m2P = new JPanel(new GridLayout(0, 1));
        m2P.add(new JLabel("Move 2:"));
        m2P.add(move2);
        JPanel m3P = new JPanel(new GridLayout(0, 1));
        m3P.add(new JLabel("Move 3:"));
        m3P.add(move3);
        JPanel aP = new JPanel(new GridLayout(0, 1));
        aP.add(new JLabel("Attack:"));
        aP.add(attack);
        JPanel sP = new JPanel(new GridLayout(0, 1));
        sP.add(new JLabel("Spells:"));
        sP.add(spells);
        JButton sendButton = new JButton("Send");
        turnPanel.add(m1P);
        turnPanel.add(m2P);
        turnPanel.add(m3P);
        turnPanel.add(aP);
        turnPanel.add(sP);
        turnPanel.add(sendButton);

        sendButton.addActionListener(_ -> {
            if (game.isWaitingForHuman()) {
                String move1Text = move1.getText();
                String move2Text = move2.getText();
                String move3Text = move3.getText();
                String attackText = attack.getText();
                String spellsText = spells.getText();

                sendMoves(move1Text, move2Text, move3Text, attackText, spellsText);
                updateWindow();
            }
        });

        add(boardPanel);
        add(turnPanel);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void sendMoves(String move1Text, String move2Text, String move3Text, String attackText, String spellText) {
        Move m1 = parseMove(move1Text);
        Move m2 = parseMove(move2Text);
        Move m3 = parseMove(move3Text);
        Attack a = parseAttack(attackText);
        ArrayList<TurnSpell> s = parseSpells(spellText);
        Turn turn = new Turn(m1, m2, m3, a, s);
        game.setHumanTurn(turn);
    }

    private Move parseMove(String moveStr) {
        Pattern movePattern = Pattern.compile("(\\d+),\\s*(\\d+),\\s*([-\\d]+),\\s*([-\\d]+)");
        Matcher matcher = movePattern.matcher(moveStr);
        if (matcher.matches()) {
            int x = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(5));
            int y = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : Integer.parseInt(matcher.group(6));
            int dx = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : Integer.parseInt(matcher.group(7));
            int dy = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : Integer.parseInt(matcher.group(8));
            return new Move(x, y, dx, dy);
        }
        return null;
    }

    private Attack parseAttack(String attackStr) {
        Pattern attackPattern = Pattern.compile("(\\d+),\\s*(\\d+),\\s*([-\\d]+),\\s*([-\\d]+)");
        Matcher matcher = attackPattern.matcher(attackStr);
        if (matcher.matches()) {
            int x = matcher.group(1) != null ? Integer.parseInt(matcher.group(1)) : Integer.parseInt(matcher.group(5));
            int y = matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : Integer.parseInt(matcher.group(6));
            int dx = matcher.group(3) != null ? Integer.parseInt(matcher.group(3)) : Integer.parseInt(matcher.group(7));
            int dy = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : Integer.parseInt(matcher.group(8));
            return new Attack(x, y, dx, dy);
        }
        return null;
    }

    private ArrayList<TurnSpell> parseSpells(String spellStr) {
        ArrayList<TurnSpell> turnSpells = new ArrayList<>();

        // Regex to match the entire TurnSpell component
        Pattern turnSpellPattern = Pattern.compile("\\((\\d+),\\s*(\\d+),\\s*(\\d+),\\s*\\[(.*?)\\]\\)");
        Matcher matcher = turnSpellPattern.matcher(spellStr);

        while (matcher.find()) {
            // Parse the main components
            int sdi = Integer.parseInt(matcher.group(1));
            int x = Integer.parseInt(matcher.group(2));
            int y = Integer.parseInt(matcher.group(3));

            // Parse the list of coordinate pairs
            String targetStr = matcher.group(4);
            ArrayList<int[]> targets = new ArrayList<>();

            // Regex to match each coordinate pair
            Pattern coordPattern = Pattern.compile("\\((\\d+),\\s*(\\d+)\\)");
            Matcher coordMatcher = coordPattern.matcher(targetStr);

            while (coordMatcher.find()) {
                int tx = Integer.parseInt(coordMatcher.group(1));
                int ty = Integer.parseInt(coordMatcher.group(2));
                targets.add(new int[]{tx, ty});
            }

            turnSpells.add(new TurnSpell(sdi, x, y, targets));
        }

        return turnSpells;
    }


    private Image loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage resizeImage(Image img, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(img, 0, 0, width, height, null);
        g2d.dispose();
        return resizedImage;
    }

    private BufferedImage combineImages(Image baseImage, Image overlayImage, int width, int height) {
        BufferedImage combinedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = combinedImage.createGraphics();
        g2d.drawImage(baseImage, 0, 0, width, height, null);
        g2d.drawImage(overlayImage, 0, 0, width, height, null);
        g2d.dispose();
        return combinedImage;
    }

    private Image getPieceSprite(Piece piece) {
        String location = "";
        switch (piece.getType()) {
            case guard -> location = (!piece.getPlayer()) ? "BlueGuard.png" : "RedGuard.png";
            case air -> location = (!piece.getPlayer()) ? "BlueAirMage.png" : "RedAirMage.png";
            case fire -> location = (!piece.getPlayer()) ? "BlueFireMage.png" : "RedFireMage.png";
            case earth -> location = (!piece.getPlayer()) ? "BlueEarthMage.png" : "RedEarthMage.png";
            case water -> location = (!piece.getPlayer()) ? "BlueWaterMage.png" : "RedWaterMage.png";
            case spirit -> location = (!piece.getPlayer()) ? "BlueSpiritMage.png" : "RedSpiritMage.png";
        }
        return new ImageIcon(location).getImage();
    }

    public void updateWindow() {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                String terrainLocation = "";
                Terrain tileType = game.board[x][y].getTerrain();

                switch (tileType) {
                    case lake -> terrainLocation = "LakeSprite.png";
                    case mountain -> terrainLocation = "MountainSprite.png";
                    case forest -> terrainLocation = "ForrestSprite.png";
                    case plains -> terrainLocation = "PlainsSprite.png";
                }

                Image before_ter = loadImage(terrainLocation);
                int width = 70;
                int height = 70;
                if (getWidth() > 0 && getHeight() > 0) {
                    double dif = 1;
                    width = (int) (((getWidth() / 2) / 8) * dif);
                    height = (int) ((getHeight() / 8) * dif);
                }

                BufferedImage combinedImage;
                if (game.board[x][y].getPiece() != null) {
                    Image before_pie = getPieceSprite(game.board[x][y].getPiece());
                    combinedImage = combineImages(before_ter, before_pie, width, height);
                } else {
                    combinedImage = resizeImage(before_ter, width, height);
                }

                boardDisplay[x][y].setIcon(new ImageIcon(combinedImage));
            }
        }
        revalidate();
        repaint();
    }
}
