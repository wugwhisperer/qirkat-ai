
package qirkat;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Observable;
import java.util.Observer;

import static qirkat.PieceColor.*;
import static qirkat.Move.*;

/** A Qirkat board.   The squares are labeled by column (a char value between
 *  'a' and 'e') and row (a char value between '1' and '5'.
 *
 *  For some purposes, it is useful to refer to squares using a single
 *  integer, which we call its "linearized index".  This is simply the
 *  number of the square in row-major order (with row 0 being the bottom row)
 *  counting from 0).
 *
 *  Moves on this board are denoted by Moves.
 *  @author Lily Vittayarukskul
 */
class Board extends Observable {

    /** A new, cleared board at the start of the game. */
    Board() {
        _contents = new PieceColor[Move.SIDE][Move.SIDE];
        clear();
        _validMoves = new ValidMoves();
    }

    /** A copy of B. */
    Board(Board b) {
        internalCopy(b);
    }

    /** Return a constant view of me (allows any access method, but no
     *  method that modifies it). */
    Board constantView() {
        return this.new ConstantBoard();
    }

    /** Clear me to my starting state, with pieces in their initial
     *  positions. */
    void clear() {
        _whoseMove = WHITE;
        _gameOver = false;

        setPieces("w w w w w "
                + "w w w w w "
                + "b b - w w "
                + "b b b b b "
                + "b b b b b " , WHITE);

        setChanged();
        notifyObservers();
    }



    /** Copy B into me. */
    void copy(Board b) {
        internalCopy(b);
    }

    /** Copy B into me. */
    private void internalCopy(Board b) {
        _contents = new PieceColor[Move.SIDE][Move.SIDE];

        for (int i = 0; i <= MAX_INDEX; i++) {
            switch (b.get(i)) {
            case WHITE:
                set(i, WHITE);
                break;
            case BLACK:
                set(i, BLACK);
                break;
            case EMPTY:
                set(i, EMPTY);
                break;
            default:
                break;
            }
        }
        switch (b.whoseMove()) {
        case WHITE:
            _whoseMove = WHITE;
            break;
        case BLACK:
            _whoseMove = BLACK;
            break;
        default:
            break;
        }
        if (b.gameOver()) {
            _gameOver = true;
        } else {
            _gameOver = false;
        }
    }

    /** Set my contents as defined by STR.  STR consists of 25 characters,
     *  each of which is b, w, or -, optionally interspersed with whitespace.
     *  These give the contents of the Board in row-major order, starting
     *  with the bottom row (row 1) and left column (column a). All squares
     *  are initialized to allow horizontal movement in either direction.
     *  NEXTMOVE indicates whose move it is.
     */
    void setPieces(String str, PieceColor nextMove) {
        if (nextMove == EMPTY || nextMove == null) {
            throw new IllegalArgumentException("bad player color");
        }
        str = str.replaceAll("\\s", "");
        if (!str.matches("[bw-]{25}")) {
            throw new IllegalArgumentException("bad board description");
        }

        for (int k = 0; k < str.length(); k += 1) {
            switch (str.charAt(k)) {
            case '-':
                set(k, EMPTY);
                break;
            case 'b': case 'B':
                set(k, BLACK);
                break;
            case 'w': case 'W':
                set(k, WHITE);
                break;
            default:
                break;
            }
        }

        _whoseMove = nextMove;
        setChanged();
        notifyObservers();
    }

    /** Return true iff the game is over: i.e., if the current player has
     *  no moves. */
    boolean gameOver() {
        return _gameOver;
    }

    /** Return the current contents of the square at linearized index K. */
    PieceColor get(int k) {
        assert validSquare(k);
        return _contents[k % Move.SIDE][k / Move.SIDE];
    }

    /** Set get(C, R) to V, where 'a' <= C <= 'e', and
     *  '1' <= R <= '5'. */
    private void set(char c, char r, PieceColor v) {
        assert validSquare(c, r);
        set(index(c, r), v);
    }

    /** Set get(K) to V, where K is the linearized index of a square. */
    private void set(int k, PieceColor v) {
        assert validSquare(k);
        _contents[k % Move.SIDE][k / Move.SIDE] = v;
    }

    /** Return all valid moves hashmaps.*/
    public ValidMoves validMoves() {
        return _validMoves;
    }

    /** Return true iff MOV is legal on the current board. */
    boolean legalMove(Move mov) {

        if (!mov.isJump()) {
            if (jumpPossible()) {
                return false;
            } else {
                Move lastMove = validMoves().getLastMove(whoseMove());
                if (lastMove != null) {
                    if ((lastMove.fromIndex() == mov.toIndex())
                            && ((lastMove.toIndex() == mov.fromIndex()))) {
                        return false;
                    }
                }
                ArrayList<Move> moves = new ArrayList<>();
                getMoves(moves, mov.fromIndex());
                for (Move move:moves) {
                    if (move.equals(mov)) {
                        return true;
                    }
                }
            }
        } else {
            if (mov.isJump()) {
                ArrayList<Move> jumpMoves = new ArrayList<>();
                getJumps(jumpMoves, mov.fromIndex());
                for (Move move:jumpMoves) {
                    if (move.equals(mov)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Return a list of all legal moves from the current position. */
    ArrayList<Move> getMoves() {
        ArrayList<Move> result = new ArrayList<>();
        getMoves(result);
        return result;
    }

    /** Add all legal moves from the current position to MOVES. */
    void getMoves(ArrayList<Move> moves) {
        if (gameOver()) {
            return;
        }
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                getJumps(moves, k);
            }
        }
        if (moves.isEmpty()) {
            for (int k = 0; k <= MAX_INDEX; k += 1) {
                getMoves(moves, k);
            }
        }
    }

    /** Add all legal non-capturing moves from the position
     *  with linearized index K to MOVES. */
    private void getMoves(ArrayList<Move> moves, int k) {
        if (whoseMove() == WHITE) {
            int[] movesList = validMoves().get(
                    validMoves().nonJumpMovesWHITE(), k);
            if (get(k).equals(whoseMove())) {
                for (int i = 0; i < movesList.length; i++) {
                    if (get(movesList[i]) == EMPTY) {
                        moves.add(move(col(k), row(k), col(movesList[i]),
                                row(movesList[i])));
                    }
                }
            }
        }
        if (whoseMove() == BLACK) {
            int[] movesList = validMoves().get(
                    validMoves().nonJumpMovesBLACK(), k);
            if (get(k).equals(whoseMove())) {

                for (int i = 0; i < movesList.length; i++) {
                    if (get(movesList[i]) == EMPTY) {
                        moves.add(move(col(k), row(k), col(movesList[i]),
                                row(movesList[i])));
                    }
                }
            }
        }
    }

    /** Add all legal captures from the position with linearized index K
     *  to MOVES. */
    private void getJumps(ArrayList<Move> moves, int k) {
        Move movePath = null;
        getJumpsH(k, k, movePath, moves);
    }

    /** Returns a unique move PATH from current linearized position K,
     * using PREVK and PATH to update MOVES. */
    private void getJumpsH(int prevK, int k, Move path, ArrayList<Move> moves) {
        Move movePath = move(path, move(col(prevK),
                row(prevK), col(k), row(k)));
            if (!jumpPossible(k)) {
            moves.add(movePath);
            set(k, EMPTY);
            preJumpBoard(movePath);
            return;
        } else {
            int[] movesList = validMoves().get(
                    validMoves().jumpMoves(), k);
            for (int i = 0; i < movesList.length; i += 2) {
                if ((get(k).equals(whoseMove()))
                        && (get(movesList[i]).equals(whoseMove().opposite()))
                        && (get(movesList[i + 1]) == EMPTY)) {
                    set(k, EMPTY);
                    set(movesList[i], EMPTY);
                    set(movesList[i + 1], whoseMove());
                    getJumpsH(k, movesList[i + 1], movePath, moves);
                }
            }
        }
    }

    /** Restore pieces that were jumped over in a MOVE.*/
    private void preJumpBoard(Move move) {
        set(move.fromIndex(), whoseMove());
        Move nextMov = move;
        while (nextMov.isJump()) {
            set(nextMov.jumpedIndex(), whoseMove().opposite());
            if (nextMov.jumpTail() != null) {
                nextMov = nextMov.jumpTail();
            } else {
                break;
            }
        }
    }

    /** Return true iff MOV is a valid jump sequence on the current board.
     *  MOV must be a jump or null.  If ALLOWPARTIAL, allow jumps that
     *  could be continued and are valid as far as they go.  */
    boolean checkJump(Move mov, boolean allowPartial) {
        if (mov == null) {
            return true;
        }
        return false;
    }

    /** Return true iff a jump is possible for a piece at position C R. */
    boolean jumpPossible(char c, char r) {
        return jumpPossible(index(c, r));
    }

    /** Return true iff a jump is possible for a piece at position with
     *  linearized index K. */
    boolean jumpPossible(int k) {
        int[] movesList = validMoves().get(
                validMoves().jumpMoves(), k);
        for (int i = 0; i < movesList.length; i += 2) {
            if ((get(k).equals(whoseMove()))
                    && (get(movesList[i]).equals(whoseMove().opposite()))
                    && (get(movesList[i + 1]) == EMPTY)) {
                return true;
            }
        }
        return false;
    }

    /** Return true iff a jump is possible from the current board. */
    boolean jumpPossible() {
        for (int k = 0; k <= MAX_INDEX; k += 1) {
            if (jumpPossible(k)) {
                return true;
            }
        }
        return false;
    }

    /** Return the color of the player who has the next move.  The
     *  value is arbitrary if gameOver(). */
    PieceColor whoseMove() {
        return _whoseMove;
    }

    /** Perform the move C0R0-C1R1, or pass if C0 is '-'.  For moves
     *  other than pass, assumes that legalMove(C0, R0, C1, R1). */
    void makeMove(char c0, char r0, char c1, char r1) {
        makeMove(Move.move(c0, r0, c1, r1, null));
    }

    /** Make the multi-jump C0 R0-C1 R1..., where NEXT is C1R1....
     *  Assumes the result is legal. */
    void makeMove(char c0, char r0, char c1, char r1, Move next) {
        makeMove(Move.move(c0, r0, c1, r1, next));
    }

    /** Make the Move MOV on this Board, assuming it is legal. */
    void makeMove(Move mov) {
        assert legalMove(mov);
        validMoves().setLastMove(whoseMove(), mov);
        if (mov.jumpTail() == null) {
            set(mov.toIndex(), whoseMove());
            set(mov.fromIndex(), EMPTY);
            if (mov.jumpedIndex() != mov.toIndex()) {
                set(mov.jumpedIndex(), EMPTY);
            }
        } else {
            Move nextMov = mov;
            while (nextMov != null) {
                set(nextMov.toIndex(), whoseMove());
                set(nextMov.jumpedIndex(), EMPTY);
                set(nextMov.fromIndex(), EMPTY);
                nextMov = nextMov.jumpTail();
            }
        }

        setChanged();
        notifyObservers();
        _whoseMove = whoseMove().opposite();
    }

    /** Return true if game is over.*/
    boolean postcheckGameOver() {
        if (getMoves().isEmpty()) {
            _gameOver = true;
            return true;
        } else {
            return false;
        }
    }

    /** Undo the last move, if any. */
    void undo() {
        Move lastMove = validMoves().getLastMove(
                whoseMove().opposite());
        if (lastMove.isJump()) {
            int i = 0;
        } else {
            while (lastMove != null) {
                int i = 0;
            }
        }


        setChanged();
        notifyObservers();
    }

    /** Create the contents of the board. */
    private PieceColor[][] _contents;

    /** Return CONTENTS of board. */
    PieceColor[][] contents() {
        return _contents;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    /** Return a text depiction of the board.  If LEGEND, supply row and
     *  column numbers around the edges. */
    String toString(boolean legend) {
        Formatter out = new Formatter();
        String outString = "";
        for (int i = Move.SIDE - 1; i >= 0; i--) {
            if (legend) {
                outString += i + 1;
            }
            outString += " ";
            for (int j = 0; j < Move.SIDE; j++) {
                outString += " " + _contents[j][i].shortName();
            }
            if (i == 0) {
                if (legend) {
                    outString += "\n   a b c d e";
                }
            } else {
                outString += "\n";
            }
        }
        out.format(outString);
        return out.toString();
    }


    /** Return true iff there is a move for the current player. */
    private boolean isMove() {
        return false;
    }

    /** The class that contains hashmaps of all valid capture moves
     * and non capture moves for white and black. */
    private static ValidMoves _validMoves;

    /** Player that is on move. */
    private PieceColor _whoseMove;

    /** Set true when game ends. */
    private boolean _gameOver;

    /** Convenience value giving values of pieces at each ordinal position. */
    static final PieceColor[] PIECE_VALUES = PieceColor.values();

    /** One cannot create arrays of ArrayList<Move>, so we introduce
     *  a specialized private list type for this purpose. */
    private static class MoveList extends ArrayList<Move> {
    }

    /** A read-only view of a Board. */
    private class ConstantBoard extends Board implements Observer {
        /** A constant view of this Board. */
        ConstantBoard() {
            super(Board.this);
            Board.this.addObserver(this);
        }

        @Override
        void copy(Board b) {
            assert false;
        }

        @Override
        void clear() {
            assert false;
        }

        @Override
        void makeMove(Move move) {
            assert false;
        }

        /** Undo the last move. */
        @Override
        void undo() {
            assert false;
        }

        @Override
        public void update(Observable obs, Object arg) {
            super.copy((Board) obs);
            setChanged();
            notifyObservers(arg);
        }
    }
}
