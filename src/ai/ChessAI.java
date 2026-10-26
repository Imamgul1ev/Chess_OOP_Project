package ai;

import Piece.Piece;
import main.Board;
import main.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChessAI {
    private final Board board;
    private final Random random = new Random();

    public ChessAI(Board board) {
        this.board = board;
    }

    // Get all legal moves for the specified color
    public List<Move> getAllLegalMoves(boolean isWhite) {
        List<Move> moves = new ArrayList<>();
        boolean prevTurn = board.isWhiteToMove();

        board.setIsWhiteToMove(isWhite); // Temporarily set turn for isValidMove

        for (Piece piece : new ArrayList<>(board.getPieceList())) {
            if (piece.isWhite() == isWhite && piece.isAvailable()) {
                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        Move move = new Move(board, piece, c, r);

                        // --- Set the capture manually for simulation ---
                        // For en passant: check the special rule!
                        if (piece.getName().equals("Pawn")) {
                            if (board.getPiece(c, r) != null && !board.sameTeam(piece, board.getPiece(c, r))) {
                                move.setCapture(board.getPiece(c, r));
                            }
                            // For en passant, you may need extra logic to check the target square!
                            // Example (simplified, adapt for your codebase!):
                            if (board.getEnPassantTile() == board.getTileNum(c, r) && board.getPiece(c, r) == null) {
                                int dir = piece.isWhite() ? 1 : -1;
                                move.setCapture(board.getPiece(c, r + dir));
                            }
                        } else if (board.getPiece(c, r) != null && !board.sameTeam(piece, board.getPiece(c, r))) {
                            move.setCapture(board.getPiece(c, r));
                        }

                        if (board.isValidMove(move)) {
                            // Simulate move
                            board.makeFakeMove(move);
                            Piece king = board.findKing(isWhite);
                            boolean kingSafe = (king != null) && !board.checkScanner.isKingChecked(
                                    new Move(board, king, king.getCol(), king.getRow())
                            );
                            board.undoFakeMove(move);
                            if (kingSafe) {
                                moves.add(move);
                            }
                        }
                    }
                }
            }
        }


        board.setIsWhiteToMove(prevTurn); // Restore turn
        return moves;
    }

    // Main AI method: makes a move for Black (AI)
    public void makeAIMove() {
        List<Move> moves = getAllLegalMoves(false); // Black to move
        if (moves.isEmpty()) {
            Piece king = board.findKing(false);
            boolean isCheck = false;
            if (king != null) {
                isCheck = board.checkScanner.isKingChecked(new Move(board, king, king.getCol(), king.getRow()));
            }
            board.setGameOver(true);
            if (isCheck) {
                board.setTimeUpMessage("White wins by checkmate!");
            } else {
                board.setTimeUpMessage("Stalemate! It's a draw.");
            }
            board.repaint();
            return;
        }
        // Pick a random legal move
        Move move = moves.get(random.nextInt(moves.size()));
        board.makeMove(move);
    }

    // Utility: can the king of color move to any safe square?
    public boolean kingHasSafeMove(boolean isWhite) {
        Piece king = board.findKing(isWhite);
        if (king == null) return false;
        int[] dCol = {-1, -1, -1, 0, 0, 1, 1, 1};
        int[] dRow = {-1, 0, 1, -1, 1, -1, 0, 1};
        for (int dir = 0; dir < 8; dir++) {
            int newCol = king.getCol() + dCol[dir];
            int newRow = king.getRow() + dRow[dir];
            if (newCol < 0 || newCol > 7 || newRow < 0 || newRow > 7) continue;
            Piece destPiece = board.getPiece(newCol, newRow);
            if (destPiece != null && destPiece.isWhite() == isWhite) continue;
            Move kingMove = new Move(board, king, newCol, newRow);
            board.makeFakeMove(kingMove);
            boolean safe = !board.checkScanner.isKingChecked(
                    new Move(board, king, newCol, newRow)
            );
            board.undoFakeMove(kingMove);
            if (safe) return true;
        }
        return false;
    }

    // For logic, just use board.hasAnyLegalMove(isWhite) if needed.

}
