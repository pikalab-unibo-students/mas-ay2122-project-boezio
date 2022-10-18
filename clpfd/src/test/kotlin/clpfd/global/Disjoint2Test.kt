package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test

class Disjoint2Test: BaseTest() {

    @Test
    fun testDisjoint2() {

        val theory = theoryParser.parseTheory(
            """
            problem(X1, Y1, X2, Y2) :- 
                ins([X1], 1),
                ins([X2, W1], 3),
                ins([W2], 2),
                ins([Y1, Y2], '..'(1, 10)),
                ins([H1,H2], '..'(1, 6)),
                disjoint2([f(X1,W1,Y1,H1),g(X2,W2,Y2,H2)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X1, Y1, X2, Y2),label([X1,Y1, X2, Y2])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X1") to intOf(1),
                varOf("Y1") to intOf(1),
                varOf("X2") to intOf(3),
                varOf("Y2") to intOf(2),
            )
        }
    }

    @Test
    fun testInvalidArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X1, Y1, X2, Y2) :- 
                ins([X1], 1),
                ins([X2, W1], 3),
                ins([W2], 2),
                ins([Y1, Y2], '..'(1, 10)),
                ins([H1,H2], '..'(1, 6)),
                disjoint2(a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X1, Y1, X2, Y2),label([X1,Y1, X2, Y2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidRectangleStruct() {

        val theory = theoryParser.parseTheory(
            """
            problem(X1, Y1, X2, Y2) :- 
                ins([X1], 1),
                ins([X2, W1], 3),
                ins([W2], 2),
                ins([Y1, Y2], '..'(1, 10)),
                ins([H2], '..'(1, 6)),
                disjoint2([f(X1,W1,Y1),g(X2,W2,Y2,H2)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X1, Y1, X2, Y2),label([X1,Y1, X2, Y2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.PREDICATE_PROPERTY)
    }

    @Test
    fun testInvalidRectangleArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X1, Y1, X2, Y2) :- 
                ins([X1], 1),
                ins([X2, W1], 3),
                ins([W2], 2),
                ins([Y1, Y2], '..'(1, 10)),
                ins([H1,H2], '..'(1, 6)),
                disjoint2([f(a,W1,Y1,H1),g(X2,W2,Y2,H2)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X1, Y1, X2, Y2),label([X1,Y1, X2, Y2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }
}