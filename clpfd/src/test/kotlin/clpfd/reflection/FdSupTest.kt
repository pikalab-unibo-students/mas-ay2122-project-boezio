package clpfd.reflection

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertTrue

class FdSupTest: BaseTest() {

    @Test
    fun testFdSupBaseWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_sup(X,Sup)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdSupWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#<'(X,10), fd_sup(X,Sup)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

}