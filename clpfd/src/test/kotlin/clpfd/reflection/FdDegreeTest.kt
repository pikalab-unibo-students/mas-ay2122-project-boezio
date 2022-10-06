package clpfd.reflection

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdDegreeTest: BaseTest() {

    @Test
    fun testFdDegreeZeroWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(X,Degree)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(0)
            )
        }
    }

    @Test
    fun testFdDegreeZeroWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(X,0)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdDegreeOneWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), fd_degree(X,Degree)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(1)
            )
        }
    }

    @Test
    fun testFdDegreeTwoWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_degree(X,Degree)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(2)
            )
        }
    }

    @Test
    fun testFdDegreeTwoWithVarFalseOutcome() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_degree(X,3)"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }
}