package clpVarCreation

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

internal class LabelingTest {

    @Test @Ignore
    fun testGreaterThan() {

        val theory = ClausesParser.withStandardOperators.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val goal = TermParser.withDefaultOperators.parseClause(
            "problem(X,Y),labeling([],[X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpLibrary)
        )

        val solution = solver.solveOnce(goal)
        assertEquals("{X_1=2}, {X_2=1}", solution.substitution.toString())

    }
}