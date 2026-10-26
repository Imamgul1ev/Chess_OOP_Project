package Piece;

import main.Board;

import java.awt.image.BufferedImage;

public class Pawn extends Piece {
    public Pawn(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.setCol(col);
        this.setRow(row);
        this.setXPos(col * board.getTileSize());
        this.setYPos(row * board.getTileSize());

        this.setWhite(isWhite);
        this.setName("Pawn");

        // Pawn sprites are in the 6th column (index 5), not 7th (index 6)
        this.setSprite(
                getSheet()
                        .getSubimage(
                                5 * getSheetScale(),
                                isWhite ? 0 : getSheetScale(),
                                getSheetScale(),
                                getSheetScale()
                        )
                        .getScaledInstance(
                                board.getTileSize(),
                                board.getTileSize(),
                                BufferedImage.SCALE_SMOOTH
                        )
        );
    }

    public boolean isValidMovement(int col, int row) {
        int colorIndex = isWhite() ? -1 : 1; // FIXED: should be -1 for white, 1 for black

        // push pawn 1
        if (this.getCol() == col && row == this.getRow() + colorIndex && getBoard().getPiece(col, row) == null)
            return true;

        // push pawn 2
        if (isFirstMove() && this.getCol() == col && row == this.getRow() + colorIndex * 2 &&
                getBoard().getPiece(col, row) == null && getBoard().getPiece(col, row - colorIndex) == null)
            return true;

        // capture left
        if (col == this.getCol() - 1 && row == this.getRow() + colorIndex && getBoard().getPiece(col, row) != null)
            return true;

        // capture right
        if (col == this.getCol() + 1 && row == this.getRow() + colorIndex && getBoard().getPiece(col, row) != null)
            return true;

        // en passant left
        if (getBoard().getTileNum(col, row) == getBoard().getEnPassantTile() && col == this.getCol() - 1 && row == this.getRow() + colorIndex && getBoard().getPiece(col, row - colorIndex) != null) {
            return true;
        }

        // en passant right
        if (getBoard().getTileNum(col, row) == getBoard().getEnPassantTile() && col == this.getCol() + 1 && row == this.getRow() + colorIndex && getBoard().getPiece(col, row - colorIndex) != null) {
            return true;
        }

        return false;
    }
}
