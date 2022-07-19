package clpVarCreation.globalConstraints

import clpVarCreation.ClpFdLibrary
import clpVarCreation.assertSolutionAssigns
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test

class LexChainTest {

    private val termParser = TermParser.withDefaultOperators()
    private val theoryParser = ClausesParser.withDefaultOperators()

    @Test
    fun testLexChain() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex_chain([[X,Y],[Z,W]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
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
                varOf("Z") to intOf(1),
                varOf("W") to intOf(1)
            )
        }
    }
}