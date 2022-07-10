package clpVarCreation

import it.unibo.tuprolog.core.Struct
import org.chocosolver.solver.Model as ChocoModel

enum class ProblemType {
    SATISFY, MAXIMIZE, MINIMIZE;

    fun toChoco(): Boolean? =
        when (this) {
            SATISFY -> null
            MAXIMIZE -> ChocoModel.MAXIMIZE
            MINIMIZE -> ChocoModel.MINIMIZE
        }

    companion object {
        fun fromTerm(struct: Struct): ProblemType {
            return when (struct.functor) {
                "max" -> {
                    require(struct.arity <= 1)
                    MAXIMIZE
                }
                "min" -> {
                    require(struct.arity <= 1)
                    MINIMIZE
                }
                else -> SATISFY
            }
        }
    }
}