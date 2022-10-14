package clpfd.reflection

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdSupTest: BaseTest() {

    @Test
    fun testFdSupBaseWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_sup(X,Sup)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Sup") to intOf(10)
            )
        }
    }

    @Test
    fun testFdSupBaseWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_sup(X,10)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdSupWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#<'(X,10), fd_sup(X,Sup)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Sup") to intOf(9)
            )
        }
    }

    @Test
    fun testFdSupWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #<(X,10), fd_sup(X,9)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

}