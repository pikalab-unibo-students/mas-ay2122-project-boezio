package clpfd.reflection

import clpfd.BaseTest
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdVarTest: BaseTest() {

    @Test
    fun testFdVarExist() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_var(X)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdVarNotExist() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_var(Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdVarNotVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_var(1)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isHalt)
    }

    @Test
    fun testFdVarNotIntVar() {

        val goal = termParser.parseStruct(
            "{X > 1 }, fd_var(X)"
        )

        val solver = getFdQRSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testInvalidArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_var(1)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }
}