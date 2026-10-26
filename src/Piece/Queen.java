package Piece;

import main.Board;

import java.awt.image.BufferedImage;

public class Queen extends Piece {

    public Queen(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.setCol(col);
        this.setRow(row);
        this.setXPos(col * board.getTileSize());
        this.setYPos(row * board.getTileSize());

        this.setWhite(isWhite);
        this.setName("Queen");

        this.setSprite(
                getSheet().getSubimage(getSheetScale(), isWhite ? 0 : getSheetScale(), getSheetScale(), getSheetScale())
                        .getScaledInstance(board.getTileSize(), board.getTileSize(), BufferedImage.SCALE_SMOOTH)
        );
    }

    public boolean isValidMovement(int col, int row) {
        return this.getCol() == col || this.getRow() == row || Math.abs(this.getCol() - col) == Math.abs(this.getRow() - row);
    }

    public boolean moveCollidesWithPiece(int col, int row) {

        if (this.getCol() == col || this.getRow() == row) {

            // left
            if (this.getCol() > col)
                for (int c = this.getCol() - 1; c > col; c--)
                    if (getBoard().getPiece(c, this.getRow()) != null)
                        return true;

            // right
            if (this.getCol() < col)
                for (int c = this.getCol() + 1; c < col; c++)
                    if (getBoard().getPiece(c, this.getRow()) != null)
                        return true;

            // up
            if (this.getRow() > row)
                for (int r = this.getRow() - 1; r > row; r--)
                    if (getBoard().getPiece(this.getCol(), r) != null)
                        return true;

            // down
            if (this.getRow() < row)
                for (int r = this.getRow() + 1; r < row; r++)
                    if (getBoard().getPiece(this.getCol(), r) != null)
                        return true;

        } else {

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
        }

        return false;
    }
}
