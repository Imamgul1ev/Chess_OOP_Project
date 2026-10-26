package main;

import Piece.Piece;

public class Move {

    int oldCol;
    int oldRow;
    int newCol;
    int newRow;

    Piece piece;
    Piece capture;

    public Move(Board board, Piece piece, int newCol, int newRow){
        this.oldCol = piece.getCol();
        this.oldRow = piece.getRow();
        this.newCol = newCol;
        this.newRow = newRow;

        this.piece = piece;
        this.capture = board.getPiece(newCol, newRow);
    }

    // In Move.java (public methods)
    public void setCapture(Piece piece) {
        this.capture = piece;
    }

    public Piece getCapture() {
        return this.capture;
    }

}
