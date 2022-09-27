package clpb

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test

class LabelingTest: BaseTest() {

    @Test
    fun testLabelingVariable(){

        val goal = termParser.parseStruct(
            "sat(X),labeling([X])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf("1")
            )
        }
    }

    @Test
    fun testLabelingAnd(){

        val goal = termParser.parseStruct(
            "sat(X+Y),labeling([X,Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf("0"),
                varOf("Y") to intOf(1)
            )
        }
    }
}