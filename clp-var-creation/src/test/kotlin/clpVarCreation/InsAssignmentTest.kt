package clpVarCreation

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InsAssignmentTest: BaseTest() {

    @Test
    fun testInsAssignment() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 3),
                in(Y, '..'(1,5)),
                '#<'(X,Y).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3),
                varOf("Y") to intOf(4)
            )
        }
    }

    @Test
    fun testSecondArgumentNotCompliant() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, a(1,5)),
                in(Y, '..'(1,5)),
                '#<'(X,Y).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        assertException(solver, goal)
    }

    @Test
    fun testVarAlreadyDefinedStruct() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1,5)),
                in(X, '..'(2,5)),
                '#<'(X,3).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2)
            )
        }
    }

    @Test
    fun testVarAlreadyDefinedInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, 1),
                in(X, 2),
                '#<'(X,3).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2)
            )
        }
    }

}