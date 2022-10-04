package clpfd

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test

class ReificationOpTest: BaseTest() {

    @Test
    fun testOr() {

        val goal = termParser.parseStruct(
            "in(X, '..'(1,10)),  #\\/('#<'(X,3), '#>'(X,5)) , label([X])"
        )

        val solver = Solver.prolog.solverOf(
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }
}