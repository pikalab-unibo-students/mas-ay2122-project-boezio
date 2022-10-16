package clpqr.optimization

import clpqr.*
import org.junit.jupiter.api.Test

class MaximizeTest: BaseTest() {

    @Test
    fun testMaximizeVariable(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                { 2*X+Y =< 16, X+2*Y =< 11,
                  X+3*Y =< 15, Z = 30*X+50*Y
                }.
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),maximize(Z)"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        val xExpected = "7.0001079596553750"
        val yExpected = "2.0007557175876030"
        val zExpected = "309.9924428241242000"

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
    fun testMaximizeVariableGoalVarLeft(){

        val goal = termParser.parseStruct(
            "{ 2*X+Y =< 16, X+2*Y =< 11, X+3*Y =< 15, Z = 30*X+50*Y }, maximize(Z)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "7.0001079596553750"
        val yExpected = "2.0007557175876030"
        val zExpected = "309.9924428241242000"

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
    fun testMaximizeVariableGoalVarRight(){

        val goal = termParser.parseStruct(
            "{ 2*X+Y =< 16, X+2*Y =< 11, X+3*Y =< 15, 30*X+50*Y = Z }, maximize(Z)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "7.0001079596553750"
        val yExpected = "2.0007557175876030"
        val zExpected = "309.9924428241242000"

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
    fun testMaximizeExpressionGoal(){

        val goal = termParser.parseStruct(
            "{ 2*X+Y =< 16, X+2*Y =< 11, X+3*Y =< 15 }, maximize(30*X+50*Y)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "7.0001079596553750"
        val yExpected = "2.0007557175876030"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("X") to realOf(xExpected),
                varOf("Y") to realOf(yExpected)
            )
        }
    }
}