package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test


class CumulativeTwoTest: BaseTest() {

    @Test
    fun testCumulativeAllDurationVariables() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1, D2], 2),
                cumulative([task(S1,D1,E1,H1,_), task(S2,D2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }

    @Test
    fun testCumulativeAllDurationIntegers() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L], 2),
                cumulative([task(S1,2,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }

    @Test
    fun testCumulativeMixedDuration() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("E1") to intOf(3),
                varOf("E2") to intOf(5)
            )
        }
    }

    @Test
    fun testCumulativeInvalidFirstArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative(a, [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testCumulativeInvalidSecondArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testCumulativeInvalidFunctorTask() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative([invalid(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.PREDICATE_PROPERTY)
    }

    @Test
    fun testCumulativeInvalidTaskArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative([task(a,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testCumulativeInvalidOption() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(L), invalid(L)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.PREDICATE_PROPERTY)
    }

    @Test
    fun testCumulativeInvalidLimitArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(E1, E2) :- 
                ins([E1, E2, S1, S2], '..'(1, 10)), 
                ins([H1], 1), 
                ins([H2, L, D1], 2),
                cumulative([task(S1,D1,E1,H1, _), task(S2,2,E2,H2,_)], [limit(a)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(E1,E2),label([E1,E2])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }
}