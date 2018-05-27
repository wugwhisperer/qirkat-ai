/* Author: Paul N. Hilfinger.  (C) 2008. */

package qirkat;

import org.junit.Test;
import static org.junit.Assert.*;

import static qirkat.Move.*;

/** Test Move creation.
 *  @author Lily Vittayarukskul and P. N. Hilfinger
 */
public class MoveTest {

    @Test
    public void testMove1() {
        Move m = move('a', '3', 'b', '2');
        assertNotNull(m);
        assertFalse("move should not be jump", m.isJump());
    }

    @Test
    public void testMove2() {
        Move m1 = move('a', '3', 'c', '5');
        Move m2 = move('c', '5', 'c', '3');
        Move expected = move('a', '3', 'c', '5',
                move('c', '5', 'c', '3'));
        Move actual = move(m1, m2);
        assertEquals(expected, actual);
    }

    @Test
    public void testMove3() {
        Move m1 = move('a', '3', 'c', '5',
                move('c', '5', 'c', '3'));
        Move m2 = move('c', '3', 'c', '1');
        Move expected = move('a', '3', 'c', '5',
                move('c', '5', 'c', '3',
                        move('c', '3', 'c', '1')));
        Move actual = move(m1, m2);
        assertEquals(expected, actual);
    }

    @Test
    public void testMove4() {
        Move m1 = move('a', '3', 'c', '5',
                move('c', '5', 'c', '3'));
        Move m2 = move('c', '3', 'c', '1',
                move('c', '1', 'e', '1'));
        Move expected = move('a', '3', 'c', '5',
                move('c', '5', 'c', '3',
                        move('c', '3', 'c', '1',
                                move('c', '1', 'e', '1'))));
        Move actual = move(m1, m2);
        assertEquals(expected, actual);
    }

    @Test
    public void testMove5() {
        Move m1 = move('c', '5', 'c', '5');
        Move m2 = move('c', '5', 'c', '3');
        Move expected = move('c', '5', 'c', '3');
        Move actual = move(m1, m2);
        assertEquals(expected, actual);
    }

    @Test
    public void testMove6() {
        Move m1 = move('c', '5', 'c', '5');
        Move m2 = move('c', '5', 'c', '3',
                move('c', '3', 'c', '1'));
        Move expected = move('c', '5', 'c', '3',
                move('c', '3', 'c', '1'));
        Move actual = move(m1, m2);
        assertEquals(expected, actual);
    }

    @Test
    public void testJump1() {
        Move m = move('a', '3', 'a', '5');
        assertNotNull(m);
        assertTrue("move should be jump", m.isJump());
    }

    @Test
    public void testString() {
        assertEquals("a3-b2", move('a', '3', 'b', '2').toString());
        assertEquals("a3-a5", move('a', '3', 'a', '5').toString());
        assertEquals("a3-a5-c3", move('a', '3', 'a', '5',
                                      move('a', '5', 'c', '3')).toString());
    }

    @Test
    public void testParseString() {
        assertEquals("a3-b2", parseMove("a3-b2").toString());
        assertEquals("a3-a5", parseMove("a3-a5").toString());
        assertEquals("a3-a5-c3", parseMove("a3-a5-c3").toString());
        assertEquals("a3-a5-c3-e1", parseMove("a3-a5-c3-e1").toString());
    }

    @Test
    public void testJumpedRow() {
        Move m1 = move('a', '3', 'a', '5');
        Move m2 = move('c', '3', 'c', '1');
        Move m3 = move('c', '3', 'c', '2');
        assertEquals('4', m1.jumpedRow());
        assertEquals('2', m2.jumpedRow());
        assertEquals('2', m3.jumpedRow());
    }

    @Test
    public void testJumpedCol() {
        Move m1 = move('a', '3', 'a', '5');
        Move m2 = move('c', '3', 'a', '1');
        Move m3 = move('c', '3', 'e', '3');
        assertEquals('a', m1.jumpedCol());
        assertEquals('b', m2.jumpedCol());
        assertEquals('d', m3.jumpedCol());
    }
}
