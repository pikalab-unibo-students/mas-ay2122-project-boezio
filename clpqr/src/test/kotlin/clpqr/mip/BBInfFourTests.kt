package clpqr.mip

import clpqr.BaseTest
import clpqr.ClpQRLibrary
import clpqr.Precision
import clpqr.assertSolutionAssigns
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import  it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class BBInfFourTests: BaseTest() {

    @Test
    fun testBBInfFourVariable(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([Y,Z],X,Inf,Vertex)."
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpQRLibrary.toRuntime(),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
        )

        val solution = solver.solveOnce(goal)

        val xExpected = "2.0"
        val elem = Real.of(1.0)
        val vertexExpected = List.of(elem, elem)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(xExpected),
                varOf("Vertex") to vertexExpected
            )
        }
    }

}