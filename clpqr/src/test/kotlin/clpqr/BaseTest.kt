package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser

abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()
    protected val theoryParser = ClausesParser.withDefaultOperators()
    protected val precision = 0.1

    protected fun getSolver(theory: Theory = Theory.empty()): Solver =
        Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpQRLibrary.toRuntime(),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

}