package clpfd.reflection

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
    fun testFdDomWithInteger() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_dom(X,5)"
        )

        val solver = getSolver()

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
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

}