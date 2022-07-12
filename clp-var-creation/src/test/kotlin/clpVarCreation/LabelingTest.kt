package clpVarCreation

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

internal class LabelingTest {

    private val theory = ClausesParser.withDefaultOperators().parseTheory(
        """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
    )

    private val parser = TermParser.withDefaultOperators()

    // VariableSelectionStrategy tests

    @Test
    fun testLeftMost() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([leftmost],[X,Y])"
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
    fun testFirstFail() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([ff],[X,Y])"
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

    @Test @Ignore
    fun testDomOverWDeg() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([ffc],[X,Y])"
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
    fun testMin() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([min],[X,Y])"
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
    fun testMax() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([max],[X,Y])"
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

    // ValueOrder tests

    @Test
    fun testUp() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([up],[X,Y])"
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
    fun testDown() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([down],[X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(10),
                    varOf("Y") to intOf(9)
                ),
                solution.substitution
            )
        }
    }

    // Optimization tests

    @Test @Ignore
    fun testMinimize() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([min(X)],[X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(10),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
            )
        }
    }

    @Test
    fun testMaximize() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling([max('-'(X, Y))],[X, Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)

        parser.scope.with {

            assertEquals(
                Substitution.unifier(
                    varOf("X") to intOf(10),
                    varOf("Y") to intOf(1)
                ),
                solution.substitution
            )
        }
    }




}