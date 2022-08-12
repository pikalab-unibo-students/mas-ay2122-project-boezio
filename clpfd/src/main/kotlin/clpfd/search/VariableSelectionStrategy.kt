package clpfd.search

import it.unibo.tuprolog.core.Atom
import org.chocosolver.solver.Model
import org.chocosolver.solver.search.strategy.selectors.variables.*
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable

enum class VariableSelectionStrategy {
    LEFTMOST, FIRST_FAIL, FFC, MIN, MAX;

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Variable> toVariableSelector(
        chocoModel: Model,
        variables: Iterable<Variable>,
        seed: Long = DEFAULT_RANDOM_SEED
    ): VariableSelector<T> = when (this) {
        LEFTMOST -> InputOrder(chocoModel)
        FIRST_FAIL -> {
            require(T::class == IntVar::class)
            FirstFail(chocoModel) as VariableSelector<T>
        }
        FFC -> {
            val intVariables = variables.filterIsInstance<IntVar>().toTypedArray()
            DomOverWDeg(intVariables, seed) as VariableSelector<T>
        }
        MIN -> {
            require(T::class == IntVar::class)
            Smallest() as VariableSelector<T>
        }
        MAX -> {
            require(T::class == IntVar::class)
            Largest() as VariableSelector<T>
        }
    }

    companion object {

        const val DEFAULT_RANDOM_SEED: Long = 1

        fun fromAtom(atom: Atom): VariableSelectionStrategy? = when (atom.value) {
            "ff" -> FIRST_FAIL
            "ffc" -> FFC
            "min" -> MIN
            "max" -> MAX
            else -> null
        }
    }
}