package clpVarCreation.globalConstraints

import clpVarCreation.ClpFdLibrary
import clpVarCreation.assertSolutionAssigns
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test

class GlobalCardinalityTest {

    private val termParser = TermParser.withDefaultOperators()
    private val theoryParser = ClausesParser.withDefaultOperators()

    @Test
    fun testGlobalCardinality() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 3)), 
                in(W, '..'(2, 2)),
                in(V, '..'(1, 1)),
                global_cardinality([X,Y,Z],['-'(1,W), '-'(2,V)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(2)
            )
        }
    }
}