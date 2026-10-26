package Piece;

import main.Board;

import java.awt.image.BufferedImage;

public class Knight extends Piece{

    public Knight(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.setCol(col);
        this.setRow(row);
        this.setXPos(col * board.getTileSize());
        this.setYPos(row * board.getTileSize());

        this.setWhite(isWhite);
        this.setName("Knight");

        this.setSprite(
                getSheet().getSubimage(3 * getSheetScale(), isWhite ? 0 : getSheetScale(), getSheetScale(), getSheetScale())
                        .getScaledInstance(board.getTileSize(), board.getTileSize(), BufferedImage.SCALE_SMOOTH)
        );
    }

    public boolean isValidMovement(int col, int row){
        return Math.abs(col - this.getCol()) * Math.abs(row - this.getRow()) == 2;
    }

}
