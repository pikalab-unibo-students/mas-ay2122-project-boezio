package clpb

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.theory.Theory

abstract class BaseTest {

    protected val termParser = TermParser.withDefaultOperators()

    protected fun getSolver(theory: Theory = Theory.empty()): Solver =
        Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime(),
            staticKb = theory
        )

}