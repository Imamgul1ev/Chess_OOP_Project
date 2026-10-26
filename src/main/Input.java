package main;

import Piece.Piece;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Input extends MouseAdapter {

    Board board;

    public Input(Board board) {
        this.board = board;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // --- Handle promotion popup click ---
        if (board.isPromotionPending()) {
            int x = e.getX();
            int y = e.getY();

            int tileSize = board.getTileSize();
            int timerBlockWidth = board.getTimerBlockWidth();
            int timerMargin = board.getTimerMargin();
            int col = board.getPromotionCol();
            int row = board.getPromotionRow();

            // Popup's position on screen (matches paintComponent)
            int popupX = timerBlockWidth + col * tileSize;
            int popupY = row * tileSize + timerMargin;

            // Check which option is clicked in the 2x2 grid
            for (int i = 0; i < 4; i++) {
                int optX = popupX + (i % 2) * tileSize;
                int optY = popupY + (i / 2) * tileSize;
                if (x >= optX && x < optX + tileSize && y >= optY && y < optY + tileSize) {
                    String[] names = {"Queen", "Rook", "Bishop", "Knight"};
                    String chosen = names[i];
                    board.promotePawnTo(chosen);
                    return; // Do not process other clicks
                }
            }
            return; // Only promotion selection allowed
        }

        // --- Normal piece selection ---
        int col = (e.getX() - board.getTimerBlockWidth()) / board.getTileSize();
        int row = (e.getY() - board.getTimerMargin()) / board.getTileSize();
        Piece pieceXY = board.getPiece(col, row);

        if (pieceXY != null) {
            board.setSelectedPiece(pieceXY);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (board.getSelectedPiece() != null) {
            board.getSelectedPiece().setXPos(e.getX() - board.getTileSize() / 2);
            board.getSelectedPiece().setYPos(e.getY() - board.getTileSize() / 2);
        }
        board.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int col = (e.getX() - board.getTimerBlockWidth()) / board.getTileSize();
        int row = (e.getY() - board.getTimerMargin()) / board.getTileSize();

        if (board.getSelectedPiece() != null) {
            Move move = new Move(board, board.getSelectedPiece(), col, row);
            if (board.isValidMove(move)) {
                // Simulate the move (like you do in the AI)
                board.makeFakeMove(move);
                Piece king = board.findKing(board.getSelectedPiece().isWhite());
                boolean kingSafe = !board.checkScanner.isKingChecked(
                        new Move(board, king, king.getCol(), king.getRow())
                );
                board.undoFakeMove(move);
                if (kingSafe) {
                    board.makeMove(move);
                } else {
                    // Not a legal move: snap back
                    board.getSelectedPiece().setXPos(board.getSelectedPiece().getCol() * board.getTileSize() + board.getTimerBlockWidth());
                    board.getSelectedPiece().setYPos(board.getSelectedPiece().getRow() * board.getTileSize() + board.getTimerMargin());
                }
            } else {
                // Not a legal move: snap back
                board.getSelectedPiece().setXPos(board.getSelectedPiece().getCol() * board.getTileSize() + board.getTimerBlockWidth());
                board.getSelectedPiece().setYPos(board.getSelectedPiece().getRow() * board.getTileSize() + board.getTimerMargin());
            }
        }
        board.setSelectedPiece(null);
        board.repaint();
    }

}
