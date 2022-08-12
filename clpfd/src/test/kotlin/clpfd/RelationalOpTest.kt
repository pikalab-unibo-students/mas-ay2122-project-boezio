package clpfd

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test

class RelationalOpTest {

    private val parser = TermParser.withDefaultOperators()

    private val goal = parser.parseStruct(
        "problem(X,Y),label([X,Y])"
    )

    @Test
    fun testGreaterThan() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testLessThan() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#<'(X, Y).
            """.trimIndent()
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(2)
            )
        }

    }

    @Test
    fun testGreaterEquals() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>='(X, Y).
            """.trimIndent()
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testLessEquals() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#=<'(X, Y).
            """.trimIndent()
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testEquals() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#='(X, Y).
            """.trimIndent()
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testNotEquals() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#\\='(X, Y).
            """.trimIndent()
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(2)
            )
        }
    }
}