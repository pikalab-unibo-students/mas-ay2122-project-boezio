package clpVarCreation

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LabelingTest {



    @Test
    fun testMinimize() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#<'(X, Y).
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
                    varOf("X") to intOf(1),
                    varOf("Y") to intOf(2)
                ),
                solution.substitution
            )
        }
    }




}