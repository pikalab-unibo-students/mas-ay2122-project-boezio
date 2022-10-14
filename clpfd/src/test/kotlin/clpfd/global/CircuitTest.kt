package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test

class CircuitTest: BaseTest() {

    @Test
    fun testCircuit() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(0, 2)), 
                circuit([X,Y,Z]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(2),
                varOf("Z") to intOf(0)
            )
        }
    }

    @Test
    fun testCircuitWithInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Z) :- 
                ins([X,Z], '..'(0, 2)), 
                circuit([X,2,Z]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Z),label([X,Z])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Z") to intOf(0)
            )
        }
    }
}