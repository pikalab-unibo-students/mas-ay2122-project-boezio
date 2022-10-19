package clpfd.reflection

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdSizeTest: BaseTest() {

    @Test
    fun testFdSizeBaseWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_size(X,Size)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Size") to intOf(10)
            )
        }
    }

    @Test
    fun testFdSizeBaseWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_size(X,10)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdSizeWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#>'(X,1), fd_size(X,Size)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Size") to intOf(9)
            )
        }
    }

    @Test
    fun testFdSizeWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#>'(X,1), fd_size(X,9)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdSizeWithIncorrectSize() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#>'(X,1), fd_size(X,10)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdSizeInvalidVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_size(Y,8)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdSizeNotIntVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), { Y =:= 3 }, fd_size(Y,1)"
        )

        val solver = getFdQRSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testInvalidFirstArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_size(a,Size)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testInvalidSecondArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_size(X,a)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }
}