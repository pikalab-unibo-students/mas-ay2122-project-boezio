package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.flags.NotableFlag
import org.chocosolver.solver.Model

object Precision : NotableFlag {
    override val admissibleValues: Sequence<Term>
        get() = throw NotImplementedError("Cannot enumerate all real numbers")

    override fun isAdmissibleValue(value: Term): Boolean {
        return value is Real
    }

    override val defaultValue: Term
        get() = Real.of(Model().precision)

    override val name: String
        get() = "precision"
}