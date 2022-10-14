package clpqr.mip

import clpqr.*
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.List as LogicList
import org.junit.jupiter.api.Test

class BBInfFiveTest: BaseTest() {

    @Test
    fun testBBInfFiveVariable(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([Y,Z],X,Inf,Vertex,0.0)."
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        val xExpected = "2.0"
        val elem = Real.of(1.0)
        val vertexExpected = LogicList.of(elem, elem)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(xExpected),
                varOf("Vertex") to vertexExpected
            )
        }
    }

    @Test
    fun testBBInfFiveExpression(){

        val goal = termParser.parseStruct(
            "{2*X+Y >= 16, X+2*Y >= 11,X+3*Y >= 15}, bb_inf([X,Y], 30*X+50*Y, Inf, Vertex, 0.0)."
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        val exprExpected = "360.0"
        val xExpected = Real.of(7.0)
        val yExpected = Real.of(3.0)
        val vertexExpected = LogicList.of(xExpected, yExpected)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(exprExpected),
                varOf("Vertex") to vertexExpected
            )
        }
    }

}