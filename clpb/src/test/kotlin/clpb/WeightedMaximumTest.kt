package clpb

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test

class WeightedMaximumTest: BaseTest() {

    @Test
    fun testWeightedMaximumExample(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([1,2,1], [A,B,C], Maximum)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("A") to intOf(0),
                varOf("B") to intOf(1),
                varOf("C") to intOf(1),
                varOf("Maximum") to intOf(3),
            )
        }
    }
}