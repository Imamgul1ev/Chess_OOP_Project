package Piece;

import main.Board;

import java.awt.image.BufferedImage;

public class Bishop extends Piece {

    public Bishop(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.setCol(col);
        this.setRow(row);
        this.setXPos(col * board.getTileSize());
        this.setYPos(row * board.getTileSize());

        this.setWhite(isWhite);
        this.setName("Bishop");

        this.setSprite(
                getSheet().getSubimage(2 * getSheetScale(), isWhite ? 0 : getSheetScale(), getSheetScale(), getSheetScale())
                        .getScaledInstance(board.getTileSize(), board.getTileSize(), BufferedImage.SCALE_SMOOTH)
        );
    }

    public boolean isValidMovement(int col, int row) {
        return Math.abs(this.getCol() - col) == Math.abs(this.getRow() - row);
    }

    public boolean moveCollidesWithPiece(int col, int row) {
        // up left
        if (this.getCol() > col && this.getRow() > row)
            for (int i = 1; i < Math.abs(this.getCol() - col); i++)
                if (getBoard().getPiece(this.getCol() - i, this.getRow() - i) != null)
                    return true;

        // up right
        if (this.getCol() < col && this.getRow() > row)
            for (int i = 1; i < Math.abs(this.getCol() - col); i++)
                if (getBoard().getPiece(this.getCol() + i, this.getRow() - i) != null)
                    return true;

        // down left
        if (this.getCol() > col && this.getRow() < row)
            for (int i = 1; i < Math.abs(this.getCol() - col); i++)
                if (getBoard().getPiece(this.getCol() - i, this.getRow() + i) != null)
                    return true;

        // down right
        if (this.getCol() < col && this.getRow() < row)
            for (int i = 1; i < Math.abs(this.getCol() - col); i++)
                if (getBoard().getPiece(this.getCol() + i, this.getRow() + i) != null)
                    return true;

        return false;
    }

}
