package clpVarCreation

import it.unibo.tuprolog.core.Atom
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector

enum class ValueOrder {
    UP, DOWN;

    fun toValueSelector(): IntValueSelector {
        return when (this) {
            UP -> IntDomainMin()
            DOWN -> IntDomainMax()
        }
    }

    companion object {
        fun fromAtom(atom: Atom): ValueOrder? = when (atom.value) {
            "up" -> UP
            "down" -> DOWN
            else -> null
        }
    }
}