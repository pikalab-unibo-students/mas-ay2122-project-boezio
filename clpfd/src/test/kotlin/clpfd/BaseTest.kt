package clpfd

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser


abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()
    protected val theoryParser = ClausesParser.withDefaultOperators()

    // returns a solver for clpfd problems
    protected fun getSolver(theory: Theory = Theory.empty()): Solver =
        Solver.prolog.solverWithDefaultBuiltins(
            staticKb = theory,
            otherLibraries =  ClpFdLibrary.toRuntime()
        )

}