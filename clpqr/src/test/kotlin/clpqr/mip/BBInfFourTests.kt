package clpqr.mip

import clpqr.BaseTest
import clpqr.assertSolutionAssigns
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
        val vertexExpected = termParser.parseStruct(
            "[1.0, 1.0]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(xExpected),
                varOf("Vertex") to vertexExpected
            )
        }
    }

}