package clpfd.reflection

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdDomTest: BaseTest() {

    private val domFunctor = ".."

    @Test
    fun testFdDomBaseWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_dom(X,Dom)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Dom") to structOf(domFunctor, intOf(1), intOf(10))
            )
        }
    }

    @Test
    fun testFdDomBaseWithStruct() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_dom(X,'..'(1,10))"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertTrue(solution.isYes)
    }

    @Test
    fun testFdDomWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), fd_dom(X,Dom)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Dom") to structOf(domFunctor, intOf(2), intOf(10))
            )
        }
    }

    @Test
    fun testFdDomWithStruct() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_dom(X,'..'(2,9))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdDomFalse() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_dom(X,'..'(1,10))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdDomInvalidVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_dom(Y,'..'(3,3))"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdDomNotIntVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), { Y > 3 }, fd_dom(Y,'..'(3,3))"
        )

        val solver = getFdQRSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testInvalidFirstArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_dom(a,Dom)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testInvalidSecondArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_dom(X,a)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.COMPOUND)
    }

    @Test
    fun testInvalidDomainElem() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_dom(X,'..'(1,a))"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

}