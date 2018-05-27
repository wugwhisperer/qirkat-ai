package qirkat;

import static qirkat.PieceColor.*;
import static qirkat.Command.Type.*;

/** A Player that receives its moves from its Game's getMoveCmnd method.
 *  @author Lily Vittayarukskul
 */
class Manual extends Player {

    /** A Player that will play MYCOLOR on GAME, taking its moves from
     *  GAME. */
    Manual(Game game, PieceColor myColor) {
        super(game, myColor);
        _prompt = myColor + ": ";

    }

    @Override
    Move myMove() {
        Command input = game().getMoveCmnd(_prompt);
        if (input == null) {
            return  null;
        } else {
            return Move.parseMove(input.operands()[0]);
        }
    }

    /** Identifies the player serving as a source of input commands. */
    private String _prompt;
}

