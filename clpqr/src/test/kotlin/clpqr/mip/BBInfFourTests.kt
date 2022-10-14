package clpqr.mip

import clpqr.BaseTest
import clpqr.assertSolutionAssigns
import it.unibo.tuprolog.core.List
import it.unibo.tuprolog.core.Real
import org.junit.jupiter.api.Test

class BBInfFourTests: BaseTest() {

    @Test
    fun testBBInfFourVariable(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([Y,Z],X,Inf,Vertex)."
        )

        val solver = getSolver()

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