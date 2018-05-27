package qirkat;

import java.util.ArrayList;
import static qirkat.PieceColor.*;

/** A Player that computes its own moves.
 *  @author Lily Vittayarukskul
 */
class AI extends Player {

    /** Maximum minimax search depth before going to static evaluation. */
    private static final int MAX_DEPTH = 5;
    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI for GAME that will play MYCOLOR. */
    AI(Game game, PieceColor myColor) {
        super(game, myColor);
    }

    @Override
    Move myMove() {
        Main.startTiming();
        findMove();
        Move move = _lastFoundMove;
        System.out.println(myColor() + " moves "
                + move.toString() + ".");
        Main.endTiming();
        return move;
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (myColor() == WHITE) {
            findMove(b, MAX_DEPTH, 1, -INFTY, INFTY);
        } else {
            findMove(b, MAX_DEPTH, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, int sense,
                         int alpha, int beta) {

        if ((depth == 0) || board.gameOver()) {
            return staticScore(board);
        }
        if (sense == 1) {
            int tempAlpha = -INFTY;
            ArrayList<Move> moves = board.getMoves();
            for (Move move : moves) {
                if (board.legalMove(move)) {
                    board.makeMove(move);
                    Board b = new Board(board);
                    tempAlpha = findMove(b, depth - 1, -1, alpha, beta);
                }
                if (tempAlpha >= alpha) {
                    _lastFoundMove = move;
                    alpha = tempAlpha;
                }
                if (tempAlpha >= beta) {
                    break;
                }
            }
            return alpha;
        }

        if (sense == -1) {
            int tempBeta = INFTY;
            ArrayList<Move> moves = board.getMoves();
            for (Move move : moves) {
                if (board.legalMove(move)) {
                    board.makeMove(move);
                    Board b = new Board(board);
                    tempBeta = findMove(b, depth - 1, 1, alpha, beta);
                }
                if (tempBeta <= beta) {
                    _lastFoundMove = move;
                    beta = tempBeta;
                }
                if (tempBeta <= alpha) {
                    break;
                }
            }
            return beta;
        }
        return staticScore(board);
    }

    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        int score = 0;
        if (board.gameOver()) {
            if (board.whoseMove().opposite().equals(myColor())) {
                return WINNING_VALUE;
            } else {
                return -WINNING_VALUE;
            }
        }

        if (myColor().equals(PieceColor.WHITE)) {
            for (int i = 0; i < Move.MAX_INDEX; i++) {
                if (board.get(i).equals(PieceColor.WHITE)) {
                    score += 1;
                }
                if (board.get(i).equals(PieceColor.BLACK)) {
                    score += -1;
                }
            }
        } else {
            for (int i = 0; i < Move.MAX_INDEX; i++) {
                if (board.get(i).equals(PieceColor.BLACK)) {
                    score += 1;
                }
                if (board.get(i).equals(PieceColor.WHITE)) {
                    score += -1;
                }
            }
        }
        return score;
    }
}
