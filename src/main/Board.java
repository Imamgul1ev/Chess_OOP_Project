package main;

import Piece.Piece;
import Piece.Knight;
import Piece.Pawn;
import Piece.King;
import Piece.Queen;
import Piece.Rook;
import Piece.Bishop;
import ai.ChessAI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;
import java.util.stream.Collectors;
import javax.swing.*;

public class Board extends JPanel {

    private ChessAI ai;
    private final Stack<FakeMoveData> fakeMoveStack = new Stack<>();

    private final int tileSize = 85; // Now private and final (encapsulation!)
    private final int cols = 8;
    private final int rows = 8;

    private final int timerBlockWidth = 100;
    private final int timerMargin = 40;

    private final ArrayList<Piece> pieceList = new ArrayList<>();
    private Piece selectedPiece;
    private final Input input = new Input(this);
    public final CheckScanner checkScanner = new CheckScanner(this); // you may want to make this private and add a getter

    private int enPassantTile = -1;
    private boolean isWhiteToMove = true;
    private boolean isGameOver = false;

    private ChessTimer whiteTimer;
    private ChessTimer blackTimer;
    private String timeUpMessage = "";

    private boolean promotionPending = false;
    private Piece promotingPawn = null;
    private int promotionCol = -1, promotionRow = -1;
    private Move pendingPromotionMove = null;

    private boolean aiEnabled = true;
    private JButton aiToggleButton;

    // Panel for AI button on right side
    private JPanel rightPanel;

    // ============ Encapsulation Getters/Setters ===============
    public int getTileSize() { return tileSize; }
    public int getCols() { return cols; }
    public int getRows() { return rows; }
    public int getTimerBlockWidth() { return timerBlockWidth; }
    public int getTimerMargin() { return timerMargin; }
    public boolean isGameOver() { return isGameOver; }
    public ChessAI getAI() { return ai; }
    public boolean isAIEnabled() { return aiEnabled; }
    public Piece getSelectedPiece() { return selectedPiece; }
    public void setSelectedPiece(Piece p) { selectedPiece = p; }
    public int getEnPassantTile() { return enPassantTile; }
    public void setEnPassantTile(int enPassantTile) { this.enPassantTile = enPassantTile; }
    public boolean isWhiteToMove() {
        return isWhiteToMove;
    }
    public void setIsWhiteToMove(boolean turn) { this.isWhiteToMove = turn; }
    public ArrayList<Piece> getPieceList() { return pieceList; }
    public boolean isPromotionPending() { return promotionPending; }
    public Piece getPromotingPawn() { return promotingPawn; }
    public int getPromotionCol() { return promotionCol; }
    public int getPromotionRow() { return promotionRow; }
    public JPanel getRightPanel() { return rightPanel; }
    public JButton getAiToggleButton() { return aiToggleButton; }
    public void setAIEnabled(boolean enabled) {
        this.aiEnabled = enabled;
        if (aiToggleButton != null) {
            aiToggleButton.setText(aiEnabled ? "AI: ON" : "AI: OFF");
        }
    }
    public void setGameOver(boolean over) {
        whiteTimer.stop();
        blackTimer.stop();
        this.isGameOver = over;
    }
    public void setTimeUpMessage(String message) {
        this.timeUpMessage = message;
    }
    public void setWhiteToMove(boolean isWhiteToMove) { this.isWhiteToMove = isWhiteToMove; }

    // ============= End Encapsulation ===============

    public Board() {
        ai = new ChessAI(this);

        this.setPreferredSize(new Dimension(timerBlockWidth + cols * tileSize + 100, rows * tileSize + 2 * timerMargin));
        this.addMouseListener(input);
        this.addMouseMotionListener(input);
        addPieces();

        int startSeconds = 600;

        whiteTimer = new ChessTimer(startSeconds, () -> {
            timeUpMessage = "Black wins by time!";
            isGameOver = true;
            blackTimer.stop();
            whiteTimer.stop();
            repaint();
        }, this::repaint);

        blackTimer = new ChessTimer(startSeconds, () -> {
            timeUpMessage = "White wins by time!";
            isGameOver = true;
            whiteTimer.stop();
            blackTimer.stop();
            repaint();
        }, this::repaint);

        whiteTimer.start();

        // --- AI toggle button and right panel setup ---
        aiToggleButton = new JButton("AI: ON");
        aiToggleButton.setPreferredSize(new Dimension(90, 30));
        aiToggleButton.addActionListener(e -> {
            setAIEnabled(!aiEnabled);
        });

        rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setPreferredSize(new Dimension(100, rows * tileSize + 2 * timerMargin));
        rightPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 30));
        rightPanel.add(aiToggleButton);

        this.setLayout(null);
        this.add(rightPanel);

        rightPanel.setBounds(timerBlockWidth + cols * tileSize, 0, 100, rows * tileSize + 2 * timerMargin);
    }

    public Piece getPiece(int col, int row) {
        for (Piece piece : pieceList) {
            if (piece.getCol() == col && piece.getRow() == row) {
                return piece;
            }
        }
        return null;
    }

    public void makeMove(Move move) {
        if (move.piece.getName().equals("Pawn")) {
            movePawn(move);
        } else if (move.piece.getName().equals("King")) {
            moveKing(move);
        } else {
            move.piece.setCol(move.newCol);
            move.piece.setRow(move.newRow);
            move.piece.setXPos(move.newCol * tileSize + timerBlockWidth);
            move.piece.setYPos(move.newRow * tileSize + timerMargin);
            move.piece.setFirstMove(false);
            capture(move);
        }

        if (!promotionPending) {
            isWhiteToMove = !isWhiteToMove;
            updateGameState();

            whiteTimer.stop();
            blackTimer.stop();

            if (!isGameOver) {
                if (isWhiteToMove) {
                    whiteTimer.start();
                } else {
                    blackTimer.start();
                    if (aiEnabled && ai != null) {
                        javax.swing.SwingUtilities.invokeLater(() -> ai.makeAIMove());
                    }
                }
            }
        }
    }

    private static class FakeMoveData {
        private final Piece movedPiece;
        private final int oldCol, oldRow;
        private final int oldX, oldY;
        private final boolean oldFirstMove;
        private final Piece capturedPiece;
        private final int capturedCol, capturedRow;
        private final int capturedX, capturedY;

        FakeMoveData(Piece movedPiece, int oldCol, int oldRow, int oldX, int oldY, boolean oldFirstMove,
                     Piece capturedPiece, int capturedCol, int capturedRow, int capturedX, int capturedY) {
            this.movedPiece = movedPiece;
            this.oldCol = oldCol;
            this.oldRow = oldRow;
            this.oldX = oldX;
            this.oldY = oldY;
            this.oldFirstMove = oldFirstMove;
            this.capturedPiece = capturedPiece;
            this.capturedCol = capturedCol;
            this.capturedRow = capturedRow;
            this.capturedX = capturedX;
            this.capturedY = capturedY;
        }
    }

    public void makeFakeMove(Move move) {
        Piece p = move.piece;
        int oldCol = p.getCol();
        int oldRow = p.getRow();
        int oldX = p.getXPos();
        int oldY = p.getYPos();
        boolean oldFirstMove = p.isFirstMove();

        Piece captured = move.capture;
        int capCol = -1, capRow = -1, capX = -1, capY = -1;
        if (captured != null) {
            capCol = captured.getCol();
            capRow = captured.getRow();
            capX = captured.getXPos();
            capY = captured.getYPos();
            pieceList.remove(captured);
        }

        p.setCol(move.newCol);
        p.setRow(move.newRow);
        p.setXPos(move.newCol * tileSize + timerBlockWidth);
        p.setYPos(move.newRow * tileSize + timerMargin);
        p.setFirstMove(false);

        fakeMoveStack.push(new FakeMoveData(p, oldCol, oldRow, oldX, oldY, oldFirstMove, captured, capCol, capRow, capX, capY));
    }

    public void undoFakeMove(Move move) {
        if (fakeMoveStack.isEmpty()) return;
        FakeMoveData data = fakeMoveStack.pop();

        Piece p = data.movedPiece;
        p.setCol(data.oldCol);
        p.setRow(data.oldRow);
        p.setXPos(data.oldX);
        p.setYPos(data.oldY);
        p.setFirstMove(data.oldFirstMove);

        if (data.capturedPiece != null) {
            data.capturedPiece.setCol(data.capturedCol);
            data.capturedPiece.setRow(data.capturedRow);
            data.capturedPiece.setXPos(data.capturedX);
            data.capturedPiece.setYPos(data.capturedY);
            if (!pieceList.contains(data.capturedPiece))
                pieceList.add(data.capturedPiece);
        }
    }

    private void moveKing(Move move) {
        if (Math.abs(move.piece.getCol() - move.newCol) == 2) {
            Piece rook;
            if (move.piece.getCol() < move.newCol) {
                rook = getPiece(7, move.piece.getRow());
                if (rook != null) {
                    rook.setCol(5);
                    rook.setRow(move.piece.getRow());
                    rook.setXPos(rook.getCol() * tileSize + timerBlockWidth);
                    rook.setYPos(rook.getRow() * tileSize + timerMargin);
                }
            } else {
                rook = getPiece(0, move.piece.getRow());
                if (rook != null) {
                    rook.setCol(3);
                    rook.setRow(move.piece.getRow());
                    rook.setXPos(rook.getCol() * tileSize + timerBlockWidth);
                    rook.setYPos(rook.getRow() * tileSize + timerMargin);
                }
            }
        }
        move.piece.setCol(move.newCol);
        move.piece.setRow(move.newRow);
        move.piece.setXPos(move.newCol * tileSize + timerBlockWidth);
        move.piece.setYPos(move.newRow * tileSize + timerMargin);
        move.piece.setFirstMove(false);
        capture(move);
    }

    private void movePawn(Move move) {
        int colorIndex = move.piece.isWhite() ? -1 : 1;
        move.capture = null;

        if (getTileNum(move.newCol, move.newRow) == enPassantTile) {
            move.capture = getPiece(move.newCol, move.newRow - colorIndex);
        }
        else if (Math.abs(move.newCol - move.oldCol) == 1 && getPiece(move.newCol, move.newRow) != null) {
            move.capture = getPiece(move.newCol, move.newRow);
        }

        if (Math.abs(move.piece.getRow() - move.newRow) == 2) {
            enPassantTile = getTileNum(move.newCol, move.newRow + colorIndex);
        } else {
            enPassantTile = -1;
        }

        int promotionRowTarget = move.piece.isWhite() ? 0 : 7;
        if (move.newRow == promotionRowTarget) {
            promotionPending = true;
            promotingPawn = move.piece;
            promotionCol = move.newCol;
            promotionRow = move.newRow;
            promotingPawn.setCol(promotionCol);
            promotingPawn.setRow(promotionRow);
            promotingPawn.setXPos(promotionCol * tileSize + timerBlockWidth);
            promotingPawn.setYPos(promotionRow * tileSize + timerMargin);
            capture(move);
            repaint();
        } else {
            move.piece.setCol(move.newCol);
            move.piece.setRow(move.newRow);
            move.piece.setXPos(move.newCol * tileSize + timerBlockWidth);
            move.piece.setYPos(move.newRow * tileSize + timerMargin);
            move.piece.setFirstMove(false);
            capture(move);
        }
    }

    public void capture(Move move) {
        if (move.capture != null) {
            pieceList.remove(move.capture);
        }
    }

    public void promotePawnTo(String pieceType) {
        pieceList.remove(promotingPawn);

        Piece newPiece = null;
        if (pieceType.equals("Queen")) {
            newPiece = new Queen(this, promotionCol, promotionRow, promotingPawn.isWhite());
        } else if (pieceType.equals("Rook")) {
            newPiece = new Rook(this, promotionCol, promotionRow, promotingPawn.isWhite());
        } else if (pieceType.equals("Bishop")) {
            newPiece = new Bishop(this, promotionCol, promotionRow, promotingPawn.isWhite());
        } else if (pieceType.equals("Knight")) {
            newPiece = new Knight(this, promotionCol, promotionRow, promotingPawn.isWhite());
        }
        if (newPiece != null) {
            newPiece.setXPos(promotionCol * tileSize + timerBlockWidth);
            newPiece.setYPos(promotionRow * tileSize + timerMargin);
            pieceList.add(newPiece);
        }
        promotionPending = false;
        promotingPawn = null;
        promotionCol = -1;
        promotionRow = -1;
        pendingPromotionMove = null;

        isWhiteToMove = !isWhiteToMove;
        updateGameState();

        whiteTimer.stop();
        blackTimer.stop();
        if (!isGameOver) {
            if (isWhiteToMove) {
                whiteTimer.start();
            } else {
                blackTimer.start();
                if (aiEnabled && ai != null) {
                    javax.swing.SwingUtilities.invokeLater(() -> ai.makeAIMove());
                }
            }
        }
        repaint();
    }

    private void promotePawn(Move move) {
        promotionPending = true;
        promotingPawn = move.piece;
        promotionCol = move.newCol;
        promotionRow = move.newRow;
        pendingPromotionMove = move;
        repaint();
    }

    public boolean isValidMove(Move move) {
        if (isGameOver) return false;
        if (!move.piece.isAvailable()) return false;
        if (move.piece.isWhite() != isWhiteToMove) return false;
        if (sameTeam(move.piece, move.capture)) return false;
        if (!move.piece.isValidMovement(move.newCol, move.newRow)) return false;
        if (move.piece.moveCollidesWithPiece(move.newCol, move.newRow)) return false;

        makeFakeMove(move);

        Piece king = findKing(move.piece.isWhite());
        boolean kingStillInCheck = (king != null) && checkScanner.isKingChecked(
                new Move(this, king, king.getCol(), king.getRow())
        );

        undoFakeMove(move);

        if (kingStillInCheck) return false;
        return true;
    }

    public boolean sameTeam(Piece p1, Piece p2) {
        if (p1 == null || p2 == null) {
            return false;
        }
        return p1.isWhite() == p2.isWhite();
    }

    public int getTileNum(int col, int row) {
        return row * cols + col;
    }

    public Piece findKing(boolean isWhite) {
        for (Piece piece : pieceList) {
            if (isWhite == piece.isWhite() && piece.getName().equals("King")) {
                return piece;
            }
        }
        return null;
    }

    public boolean hasAnyLegalMove(boolean isWhite) {
        for (Piece piece : getPieceList()) {
            if (piece.isWhite() == isWhite && piece.isAvailable()) {
                for (int r = 0; r < 8; r++) {
                    for (int c = 0; c < 8; c++) {
                        Move move = new Move(this, piece, c, r);
                        if (isValidMove(move)) {
                            makeFakeMove(move);
                            Piece king = findKing(isWhite);
                            boolean kingSafe = (king != null) && !checkScanner.isKingChecked(
                                    new Move(this, king, king.getCol(), king.getRow())
                            );
                            undoFakeMove(move);
                            if (kingSafe) return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void addPieces() {
        pieceList.add(new Rook(this, 0, 0, false));
        pieceList.add(new Knight(this, 1, 0, false));
        pieceList.add(new Bishop(this, 2, 0, false));
        pieceList.add(new Queen(this, 3, 0, false));
        pieceList.add(new King(this, 4, 0, false));
        pieceList.add(new Bishop(this, 5, 0, false));
        pieceList.add(new Knight(this, 6, 0, false));
        pieceList.add(new Rook(this, 7, 0, false));

        pieceList.add(new Rook(this, 0, 7, true));
        pieceList.add(new Knight(this, 1, 7, true));
        pieceList.add(new Bishop(this, 2, 7, true));
        pieceList.add(new Queen(this, 3, 7, true));
        pieceList.add(new King(this, 4, 7, true));
        pieceList.add(new Bishop(this, 5, 7, true));
        pieceList.add(new Knight(this, 6, 7, true));
        pieceList.add(new Rook(this, 7, 7, true));

        pieceList.add(new Pawn(this, 0, 1, false));
        pieceList.add(new Pawn(this, 1, 1, false));
        pieceList.add(new Pawn(this, 2, 1, false));
        pieceList.add(new Pawn(this, 3, 1, false));
        pieceList.add(new Pawn(this, 4, 1, false));
        pieceList.add(new Pawn(this, 5, 1, false));
        pieceList.add(new Pawn(this, 6, 1, false));
        pieceList.add(new Pawn(this, 7, 1, false));

        pieceList.add(new Pawn(this, 0, 6, true));
        pieceList.add(new Pawn(this, 1, 6, true));
        pieceList.add(new Pawn(this, 2, 6, true));
        pieceList.add(new Pawn(this, 3, 6, true));
        pieceList.add(new Pawn(this, 4, 6, true));
        pieceList.add(new Pawn(this, 5, 6, true));
        pieceList.add(new Pawn(this, 6, 6, true));
        pieceList.add(new Pawn(this, 7, 6, true));

        for (Piece p : pieceList) {
            p.setXPos(p.getCol() * tileSize + timerBlockWidth);
            p.setYPos(p.getRow() * tileSize + timerMargin);
        }
    }

    private void updateGameState() {
        Piece king = findKing(isWhiteToMove);
        boolean inCheck = checkScanner.isKingChecked(new Move(this, king, king.getCol(), king.getRow()));
        boolean hasLegalMove = ai.getAllLegalMoves(isWhiteToMove).size() > 0;

        if (!hasLegalMove) {
            if (inCheck) {
                timeUpMessage = (isWhiteToMove ? "Black" : "White") + " wins by checkmate!";
                System.out.println(timeUpMessage);
            } else {
                timeUpMessage = "Stalemate!";
                System.out.println(timeUpMessage);
            }
            isGameOver = true;
            whiteTimer.stop();
            blackTimer.stop();
        } else if (insufficientMaterial(true) && insufficientMaterial(false)) {
            timeUpMessage = "Insufficient Material!";
            System.out.println(timeUpMessage);
            isGameOver = true;
            whiteTimer.stop();
            blackTimer.stop();
        }
    }

    private boolean insufficientMaterial(boolean isWhite) {
        ArrayList<String> names = pieceList.stream()
                .filter(p -> p.isWhite() == isWhite)
                .map(Piece::getName)
                .collect(Collectors.toCollection(ArrayList::new));
        if (names.contains("Queen") || names.contains("Rook") || names.contains("Pawn")) {
            return false;
        }
        return names.size() < 3;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, timerBlockWidth, rows * tileSize + 2 * timerMargin);

        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, timerBlockWidth, rows * tileSize + 2 * timerMargin);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                g2d.setColor((c + r) % 2 == 0
                        ? new Color(227, 198, 181)
                        : new Color(157, 105, 53));
                g2d.fillRect(timerBlockWidth + c * tileSize, r * tileSize + timerMargin, tileSize, tileSize);
            }
        }

        if (selectedPiece != null)
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++) {
                    if (isValidMove(new Move(this, selectedPiece, c, r))) {
                        g2d.setColor(new Color(68, 180, 57, 190));
                        g2d.fillRect(timerBlockWidth + c * tileSize, r * tileSize + timerMargin, tileSize, tileSize);
                    }
                }

        for (Piece piece : pieceList) {
            piece.paint(g2d);
        }

        if (promotionPending && promotingPawn != null) {
            int popupX = timerBlockWidth + promotionCol * tileSize;
            int popupY = promotionRow * tileSize + timerMargin;

            g2d.setColor(new Color(240, 240, 240, 230));
            g2d.fillRect(popupX, popupY, tileSize * 2, tileSize * 2);

            Piece[] options = {
                    new Queen(this, promotionCol, promotionRow, promotingPawn.isWhite()),
                    new Rook(this, promotionCol, promotionRow, promotingPawn.isWhite()),
                    new Bishop(this, promotionCol, promotionRow, promotingPawn.isWhite()),
                    new Knight(this, promotionCol, promotionRow, promotingPawn.isWhite())
            };
            for (int i = 0; i < 4; i++) {
                int optX = popupX + (i % 2) * tileSize;
                int optY = popupY + (i / 2) * tileSize;
                options[i].setXPos(optX);
                options[i].setYPos(optY);
                options[i].paint(g2d);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(optX, optY, tileSize, tileSize);
            }
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.setColor(Color.RED);

        int timerTextX = timerBlockWidth / 2 - 35;
        int timerTextY = 30;
        g2d.drawString(whiteTimer.getTimeText(), timerTextX, timerTextY);

        int blackTimerTextY = rows * tileSize + timerMargin + 20;
        g2d.drawString(blackTimer.getTimeText(), timerTextX, blackTimerTextY);

        if (!timeUpMessage.isEmpty()) {
            g2d.setFont(new Font("Arial", Font.BOLD, 36));
            g2d.setColor(Color.RED);
            int strWidth = g2d.getFontMetrics().stringWidth(timeUpMessage);
            g2d.drawString(timeUpMessage, (getWidth() - strWidth) / 2, getHeight() / 2);
        }
    }
}

