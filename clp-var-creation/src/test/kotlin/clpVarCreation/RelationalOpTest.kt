package clpVarCreation

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

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
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(2),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
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
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(1),
                    varOf("Y") to intOf(2)
                ),
                solution.substitution
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
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(1),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
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
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(1),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
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
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(1),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
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
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(1),
                    varOf("Y") to intOf(2)
                ),
                solution.substitution
            )
        }
    }
}