package clpqr.optimization

import clpqr.BaseTest
import clpqr.ClpQRLibrary
import clpqr.Precision
import clpqr.assertSolutionAssignsDouble
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test

class MinimizeTest: BaseTest() {

    @Test
    fun testMinimizeVariable(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                { 2*X+Y >= 16, X+2*Y >= 11,
                  X+3*Y >= 15, Z = 30*X+50*Y
                }.
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),minimize(Z)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        val xExpected = "6.6000961909180020"
        val yExpected = "2.8000366441592393"
        val zExpected = "338.0012825455731300"

        termParser.scope.with {
            solution.assertSolutionAssignsDouble(
                precision,
                varOf("X") to realOf(xExpected),
                varOf("Y") to realOf(yExpected),
                varOf("Z") to realOf(zExpected)

            )
        }
    }
}