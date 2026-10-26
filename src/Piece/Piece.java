package Piece;

import main.Board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {
    // All fields are private
    private int col, row;
    private int xPos, yPos;
    private boolean isWhite;
    private String name;
    private int value;
    private boolean isFirstMove = true;
    private boolean captured = false;

    private BufferedImage sheet;
    private int sheetScale;
    private Image sprite;
    private Board board;

    // Constructor
    public Piece(Board board) {
        this.board = board;
        try {
            sheet = ImageIO.read(ClassLoader.getSystemResourceAsStream("Chess_Pieces.png"));
            sheetScale = sheet.getWidth() / 6;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters

    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }

    public int getXPos() {
        return xPos;
    }
    public void setXPos(int xPos) {
        this.xPos = xPos;
    }

    public int getYPos() {
        return yPos;
    }
    public void setYPos(int yPos) {
        this.yPos = yPos;
    }

    public boolean isWhite() {
        return isWhite;
    }
    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }

    public boolean isFirstMove() {
        return isFirstMove;
    }
    public void setFirstMove(boolean firstMove) {
        this.isFirstMove = firstMove;
    }

    public boolean isCaptured() { return captured; }
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    public Image getSprite() {
        return sprite;
    }
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Board getBoard() {
        return board;
    }
    public void setBoard(Board board) {
        this.board = board;
    }

    public int getSheetScale() {
        return sheetScale;
    }
    public BufferedImage getSheet() {
        return sheet;
    }

    // Game logic methods
    public boolean isAvailable() {

        return !captured;
    }

    public boolean isValidMovement(int col, int row) {

        return true;
    }

    public boolean moveCollidesWithPiece(int col, int row) {

        return false;
    }

    public void paint(Graphics2D g2d) {
        g2d.drawImage(sprite, xPos, yPos, null);
    }
}
