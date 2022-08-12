import clpqr.Precision
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore

fun main(args: Array<String>) {
    Solver.prolog.solverWithDefaultBuiltins(
        flags = FlagStore.DEFAULT + (Precision to Real.of("0.0000001"))
    )
}
