package clpVarCreation

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test

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

}