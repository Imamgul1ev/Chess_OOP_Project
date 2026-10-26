package Piece;

import main.Board;
import main.Move;

import java.awt.image.BufferedImage;

public class King extends Piece{
    public King(Board board, int col, int row, boolean isWhite) {
        super(board);
        this.setCol(col);
        this.setRow(row);
        this.setXPos(col * board.getTileSize());
        this.setYPos(row * board.getTileSize());

        this.setWhite(isWhite);
        this.setName("King");

        this.setSprite(
                getSheet().getSubimage(0, isWhite ? 0 : getSheetScale(), getSheetScale(), getSheetScale())
                        .getScaledInstance(board.getTileSize(), board.getTileSize(), BufferedImage.SCALE_SMOOTH)
        );
    }

    public boolean isValidMovement(int col, int row){
        return Math.abs((col - this.getCol()) * (row - this.getRow())) == 1
                || Math.abs(col - this.getCol()) + Math.abs(row - this.getRow()) == 1
                || canCastle(col, row);
    }

    private boolean canCastle(int col, int row) {

        if (this.getRow() == row) {
            if (col == 6) {
                Piece rook = getBoard().getPiece(7, row);
                if (rook != null  && rook.isFirstMove() && this.isFirstMove()){
                    return getBoard().getPiece(5, row) == null &&
                            getBoard().getPiece(6, row) == null &&
                            !getBoard().checkScanner.isKingChecked(new Move(getBoard(), this, 5, row ));
                }
            } else if (col == 2) {
                Piece rook = getBoard().getPiece(0, row); // FIXED index from 70 to 0
                if (rook != null  && rook.isFirstMove() && this.isFirstMove()){
                    return getBoard().getPiece(3, row) == null &&
                            getBoard().getPiece(2, row) == null &&
                            getBoard().getPiece(1, row) == null &&
                            !getBoard().checkScanner.isKingChecked(new Move(getBoard(), this, 3, row ));
                }
            }
        }
        return false;
    }

}
