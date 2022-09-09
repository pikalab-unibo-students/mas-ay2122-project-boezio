package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertNotNull

class SatisfyTest: BaseTest() {

    @Test
//    @Ignore
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

//        for (solution in solver.solve(goal)) {
//            println(solution.substitution)
//        }

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to realOf("5.000000000000002"),
                varOf("Y") to realOf("5.000000000000003")
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
            solution.assertSolutionAssigns(
                varOf("X") to realOf("3.359375"),
                varOf("Y") to realOf("6.718749999999998")
            )
        }

    }
}