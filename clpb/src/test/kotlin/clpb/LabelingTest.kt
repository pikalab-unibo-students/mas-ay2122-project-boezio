package clpb

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

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
                varOf("X") to intOf(0)
            )
        }
    }

    @Test
    fun testLabelingOr(){

        val goal = termParser.parseStruct(
            "sat(X+Y),labeling([X,Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(0)
            )
        }
    }

    @Test
    fun testLabelingVarOrBool(){

        val goal = termParser.parseStruct(
            "sat(X+1),labeling([X])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testLabelingSatFalse(){

        val goal = termParser.parseStruct(
            "sat(X*0),labeling([X])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testLabelingVarNotPreviouslyDefined(){

        val goal = termParser.parseStruct(
            "sat(X),labeling([X,Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(0),
                varOf("Y") to intOf(0)
            )
        }
    }
}