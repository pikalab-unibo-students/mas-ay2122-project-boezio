package clpVarCreation

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser

fun main() {

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
}