package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test

class AllDistinctExcept0Test: BaseTest() {

    @Test
    fun testAllDistinctExcept0(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(0, 10)), 
                in(Y, '..'(0, 10)), 
                all_distinct_except_0([X,Y]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(0),
                varOf("Y") to intOf(0)
            )
        }
    }

}