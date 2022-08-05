package clpVarCreation.globalConstraints

import clpVarCreation.BaseTest
import clpVarCreation.ClpFdLibrary
import clpVarCreation.assertSolutionAssigns
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test

class ChainTest {

    private val termParser = TermParser.withStandardOperators()
    private val theoryParser = ClausesParser.withStandardOperators()

    @Test
    fun testChain() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 10)), 
                chain([X,Y,Z], '#>').
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
                varOf("X") to intOf(3),
                varOf("Y") to intOf(2),
                varOf("Z") to intOf(1)
            )
        }
    }
}