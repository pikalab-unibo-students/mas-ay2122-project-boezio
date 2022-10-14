package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class ScalarProductTest: BaseTest() {

    @Test
    fun testScalarProductInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([1, 2], [X,Y], #=, 11).
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
                varOf("Y") to intOf(5)
            )
        }
    }

    @Test
    fun testScalarProductVariable() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)),
                in(Z, '..'(1,20)),
                scalar_product([1, 2], [X,Y], #=, Z).
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
                varOf("Y") to intOf(1),
            varOf("Z") to intOf(3)
            )
        }
    }

    @Test
    fun testScalarProductExpression() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)),
                in(Z, '..'(1,20)),
                scalar_product([1, 2], [X,Y], #=, Z + 1).
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
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(2)
            )
        }
    }

    @Test
    fun testScalarProductMixedVs() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 10)),
                scalar_product([1,2], [X,5], #=, 11).
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

    @Test
    fun testInvalidCs() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 10)),
                scalar_product([a,2], [X,5], #=, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }

    @Test
    fun testInvalidVs() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 10)),
                scalar_product([1,2], [a,5], #=, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }

}