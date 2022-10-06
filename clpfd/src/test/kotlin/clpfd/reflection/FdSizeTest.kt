package clpfd.reflection

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdSizeTest: BaseTest() {

    @Test
    fun testFdSizeBaseWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_size(X,Size)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdSizeWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#>'(X,1), fd_size(X,Size)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

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

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }
}