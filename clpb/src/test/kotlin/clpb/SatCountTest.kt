package clpb

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class SatCountTest: BaseTest() {

    @Test
    fun testSatCountTrue(){

        val goal = termParser.parseStruct(
            "sat_count(1,Count)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Count") to intOf(1)
            )
        }
    }

    @Test
    fun testSatCountFalse(){

        val goal = termParser.parseStruct(
            "sat_count(0,Count)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Count") to intOf(0)
            )
        }
    }

    @Test
    fun testSatCountVar(){

        val goal = termParser.parseStruct(
            "sat_count(X,Count)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Count") to intOf(1)
            )
        }
    }

    @Test
    fun testSatCountExpression(){

        val goal = termParser.parseStruct(
            "sat_count(A =< B, Count)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Count") to intOf(3)
            )
        }
    }

    @Test @Ignore
    fun testSatCountWithSat(){

        val goal = termParser.parseStruct(
            "sat(A =< B), sat_count(+[1,A,B], Count)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Count") to intOf(3)
            )
        }
    }

}