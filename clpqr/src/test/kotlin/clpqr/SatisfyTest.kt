package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.Ignore
import kotlin.test.assertNotNull

class SatisfyTest: BaseTest() {

    @Test
    fun testSatisfyTuple(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- { X >= Y, X + Y = 10.0 }.
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X, Y),satisfy([X, Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssignsDouble(
                 precision,
                varOf("X") to realOf("8.362656249999997"),
                varOf("Y") to realOf("1.7153125")
            )
        }
    }

    @Test
    fun testSatisfyStruct(){

        val goal = termParser.parseStruct(
            "{ X + Y = 10.0 }, satisfy([X, Y])",
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision))
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssignsDouble(
                 precision,
                varOf("X") to realOf("3.4375"),
                varOf("Y") to realOf("6.630625000000003")
            )
        }

    }
}