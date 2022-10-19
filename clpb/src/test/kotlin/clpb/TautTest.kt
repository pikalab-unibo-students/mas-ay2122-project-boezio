package clpb

import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TautTest: BaseTest() {

    @Test
    fun testTautTrueValue(){

        val goal = termParser.parseStruct(
            "taut(1,T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1)
            )
        }
    }

    @Test
    fun testTautFalseValue(){

        val goal = termParser.parseStruct(
            "taut(0,T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(0)
            )
        }
    }

    @Test
    fun testTautTrueProposition(){

        val goal = termParser.parseStruct(
            "taut(1*1,T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1)
            )
        }
    }

    @Test
    fun testTautFalseProposition(){

        val goal = termParser.parseStruct(
            "taut(1*0,T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(0)
            )
        }
    }

    @Test
    fun testTautVar(){

        val goal = termParser.parseStruct(
            "taut(X,T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testTautIsTaut(){

        val goal = termParser.parseStruct(
            "taut('+'(X,'~'(X)),T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1)
            )
        }
    }

    @Test
    fun testTautIsNotTaut(){

        val goal = termParser.parseStruct(
            "taut('*'(X,'~'(X)),T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(0)
            )
        }
    }

    @Test
    fun testTautExpressionIsSat(){

        val goal = termParser.parseStruct(
            "taut(X * 1,T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testTautWithSat(){

        val goal = termParser.parseStruct(
            "sat(X),taut('+'(X,'~'(X)),T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1)
            )
        }
    }

    @Test
    fun testTautWithSatReversed(){

        val goal = termParser.parseStruct(
            "taut('+'(X,'~'(X)),T),sat(X)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1)
            )
        }
    }

    @Test
    fun testTautTwoSatAndTaut(){

        val goal = termParser.parseStruct(
            "sat(X =< Y), sat(Y =< Z), taut(X =< Z, T)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1)
            )
        }
    }

    @Test
    fun testInvalidSecondArgument(){

        val goal = termParser.parseStruct(
            "taut(X + 1, a)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

}