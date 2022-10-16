package clpqr.optimization

import clpqr.*
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

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        val xExpected = "6.6000961909180020"
        val yExpected = "2.8000366441592393"
        val zExpected = "338.0012825455731300"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf(xExpected),
                varOf("Y") to realOf(yExpected),
                varOf("Z") to realOf(zExpected)

            )
        }
    }

    @Test
    fun testMinimizeVariableLeft(){

        val goal = termParser.parseStruct(
            "{ 2*X+Y >= 16, X+2*Y >= 11, X+3*Y >= 15, Z = 30*X+50*Y },minimize(Z)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "6.6000961909180020"
        val yExpected = "2.8000366441592393"
        val zExpected = "338.0012825455731300"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf(xExpected),
                varOf("Y") to realOf(yExpected),
                varOf("Z") to realOf(zExpected)

            )
        }
    }

    @Test
    fun testMinimizeVariableRight(){

        val goal = termParser.parseStruct(
            "{ 2*X+Y >= 16, X+2*Y >= 11, X+3*Y >= 15, 30*X+50*Y = Z },minimize(Z)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "6.6000961909180020"
        val yExpected = "2.8000366441592393"
        val zExpected = "338.0012825455731300"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf(xExpected),
                varOf("Y") to realOf(yExpected),
                varOf("Z") to realOf(zExpected)

            )
        }
    }

    @Test
    fun testMinimizeExpressionGoal(){

        val goal = termParser.parseStruct(
            "{ 2*X+Y >= 16, X+2*Y >= 11, X+3*Y >= 15 },minimize(30*X+50*Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "6.6000961909180020"
        val yExpected = "2.8000366441592393"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf(xExpected),
                varOf("Y") to realOf(yExpected)
            )
        }
    }
}