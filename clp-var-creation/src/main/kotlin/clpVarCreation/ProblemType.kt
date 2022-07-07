package clpVarCreation

import it.unibo.tuprolog.core.Struct
import org.chocosolver.solver.Model as ChocoModel

enum class ProblemType {
    SATISFY, MAXIMISE, MINIMISE;

    fun toChoco(): Boolean? =
        when (this) {
            SATISFY -> null
            MAXIMISE -> ChocoModel.MAXIMIZE
            MINIMISE -> ChocoModel.MINIMIZE
        }

    companion object {
        fun fromTerm(struct: Struct): ProblemType {
            return when (struct.functor) {
                "max" -> {
                    require(struct.arity <= 1)
                    MAXIMISE
                }
                "min" -> {
                    require(struct.arity <= 1)
                    MINIMISE
                }
                else -> SATISFY
            }
        }
    }
}