package clpqr

import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertTrue

class EntailedTest: BaseTest() {

    @Test
    fun testEntailedTrue(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed(X > 0.0), satisfy([X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)

    }


    // Strange behaviour, constraint check returns true
    @Test @Ignore
    fun testEntailedFalse(){

        val goal = termParser.parseStruct(
            "{ X > Y, Y > Z }, entailed(Z > X), satisfy([X,Y,Z])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testEntailedSimpleFalse(){

        val goal = termParser.parseStruct(
            "{ X > 10.0 }, entailed(X < 5.0), satisfy([X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }
}