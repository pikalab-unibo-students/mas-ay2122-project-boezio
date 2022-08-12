package clpqr.search

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
}