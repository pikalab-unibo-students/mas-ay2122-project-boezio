package clpqr.mip

import clpqr.BaseTest
import clpqr.assertSolutionAssigns
import org.junit.jupiter.api.Test

class BBInfThreeTest: BaseTest() {

    @Test
    fun testBBInfFiveVariable(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([Y,Z],X,Inf)."
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "2.0"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(xExpected)
            )
        }
    }

}