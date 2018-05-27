
package qirkat;

import java.util.HashMap;

/** All ValidMoves relative to player type and move type.
 *  @author Lily Vittayarukskul
 */
public class ValidMoves {

    /**Max number of indices.*/
    private static final int NUM_INDICES = 25;

    /**All possible capture moves per linearized index key.*/
    private static final int[][] JUMP_MOVES_LIST = {
            {5, 10, 6, 12, 1, 2},
            {6, 11, 2, 3},
            {1, 0, 6, 10, 7, 12, 8, 14, 3, 4},
            {2, 1, 8, 13},
            {9, 14, 8, 12, 3, 2},
            {10, 15, 6, 7},
            {11, 16, 12, 18, 7, 8},
            {6, 5, 12, 17, 8, 9 },
            {7, 6, 12, 16, 13, 18},
            {8, 7, 14, 19},
            {5, 0, 6, 2, 11, 12, 16, 22, 15, 20},
            {6, 1, 12, 13, 16, 21},
            {6, 0, 11, 10, 16, 20, 17, 22,
            18, 24, 13, 14, 8, 4, 7, 2},
            {8, 3, 12, 11, 18, 23},
            {9, 4, 8, 2, 13, 12, 18, 22, 19, 24},
            {10, 5, 16, 17},
            {11, 6, 12, 8, 17, 18},
            {16, 15, 12, 7, 18, 19},
            {17, 16, 12, 6, 13, 8},
            {18, 17, 14, 9},
            {15, 10, 16, 12, 21, 22},
            {16, 11, 22, 23},
            {21, 20, 16, 10, 17, 12, 18, 14, 23, 24},
            {22, 21, 18, 13},
            {23, 22, 18, 12, 19, 14}
    };

    /**All possible non-capture moves per
     * linearized index key for BLACK player.*/
    private static final int[][] BLACK_MOVES = {
            {},
            {},
            {},
            {},
            {},
            {0, 6},
            {5, 1, 7, 0, 2},
            {6, 2, 8},
            {7, 3, 9, 2, 4},
            {8, 4},
            {5, 11, 6},
            {10, 6, 12},
            {11, 7, 13, 6, 8},
            {12, 8, 14},
            {13, 9, 8},
            {10, 16},
            {15, 11, 17, 10, 12},
            {16, 12, 18},
            {17, 13, 19, 12, 14},
            {18, 14},
            {15, 21, 16},
            {20, 16, 22},
            {21, 17, 23, 16, 18},
            {22, 18, 24},
            {23, 19, 18}
    };

    /**All possible non-capture moves per
     * linearized index key for WHITE player.*/
    private static final int[][] WHITE_MOVES = {
            {5, 1, 6},
            {0, 6, 2},
            {1, 7, 3, 6, 8},
            {2, 8, 4},
            {3, 9, 8},
            {10, 6},
            {5, 11, 7, 10, 12},
            {6, 12, 8},
            {7, 13, 9, 12, 14},
            {8, 14},
            {15, 11, 16},
            {10, 16, 12},
            {11, 17, 13, 16, 18},
            {12, 18, 14},
            {13, 19, 18},
            {20, 16},
            {15, 21, 17, 20, 22},
            {16, 22, 18},
            {17, 23, 19, 22, 24},
            {18, 24},
            {},
            {},
            {},
            {},
            {}
    };

    /** Fill in all the HashMaps.*/
    ValidMoves() {
        getJumpmoves(jumpMoves);
        getBlackmoves(nonJumpMovesBLACK);
        getWhitemoves(nonJumpMovesWHITE);
    }

    /** Set index 0 to be WHITE last move and 1 as
     * BLACK last move.*/
    private static Move[] lastMoves = new Move[2];

    /** Return last move of the current player given COLOR.*/
    public Move getLastMove(PieceColor color) {
        if (color.equals(PieceColor.WHITE)) {
            return lastMoves[0];
        } else {
            return lastMoves[1];
        }
    }

    /** Set last move of current player given COLOR AND MOVE.*/
    public void setLastMove(PieceColor color, Move move) {
        if (color.equals(PieceColor.WHITE)) {
            lastMoves[0] = move;
        } else {
            lastMoves[1] = move;
        }
    }

    /** Return an integer array referring to all possible moves
     * at a linearized INDEX key given a HASHMAP.*/
    public int[] get(HashMap<Integer, int[]> hashMap, int index) {
        int[] moves = (int[]) hashMap.get(index);
        return moves;
    }

    /** Return a hashmap for all possible captures.*/
    public HashMap<Integer, int[]> jumpMoves() {
        return jumpMoves;
    }

    /** Return a hashmap for all possible non-capture moves per
     * linearized index key for BLACK player.*/
    public HashMap<Integer, int[]> nonJumpMovesBLACK() {
        return nonJumpMovesBLACK;
    }

    /** Return a hashmap for all possible non-capture moves per
     * linearized index key for WHITE player.*/
    public HashMap<Integer, int[]> nonJumpMovesWHITE() {
        return nonJumpMovesWHITE;
    }

    /** Create a HASHMAP for all possible jumps, where key is
     * linearized index and value is in format of opponent
     * index and index of index after jump. For example,
     * for key value 0, the opponent to be jumped is
     * at value 5, followed by position to jump to, value 10.*/
    private void getJumpmoves(HashMap<Integer, int[]> hashMap) {
        int index;
        for (index = 0; index < NUM_INDICES; index++) {
            hashMap.put(index, JUMP_MOVES_LIST[index]);
        }

    }

    /** Create a HASHMAP for all possible non-capture moves per
     * linearized index key for BLACK player.*/
    public void getBlackmoves(HashMap<Integer, int[]> hashMap) {
        int index;
        for (index = 0; index < NUM_INDICES; index++) {
            hashMap.put(index, BLACK_MOVES[index]);
        }
    }

    /** Create a HASHMAP for all possible non-capture moves per
     * linearized index key for WHITE player.*/
    public void getWhitemoves(HashMap<Integer, int[]> hashMap) {
        int index;
        for (index = 0; index < NUM_INDICES; index++) {
            hashMap.put(index, WHITE_MOVES[index]);
        }
    }

    /** A hashmap for all possible jumps, where key is
     * linearized index and value is in format of opponent
     * index and index of index after jump. For example,
     * for key value 0, the opponent to be jumped is
     * at value 5, followed by position to jump to, value 10.*/
    private HashMap<Integer, int[]> jumpMoves = new HashMap<>();

    /** A hashmap for all possible non-capture moves per
     * linearized index key for WHITE player.*/
    private HashMap<Integer, int[]> nonJumpMovesWHITE = new HashMap<>();

    /** A hashmap for all possible non-capture moves per
     * linearized index key for BLACK player.*/
    private HashMap<Integer, int[]> nonJumpMovesBLACK =
            new HashMap<>();
}
