package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test

class AllDistinctTest: BaseTest() {

    @Test
    fun testAllDistinct() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                all_distinct([X,Y]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(2)
            )
        }
    }

    @Test
    fun testAllDistinctVarAndInt() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 2)), 
                all_distinct([X,2]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }
}