package clpb.utils

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
import org.chocosolver.solver.Model

private const val TAUT_MODEL = "tautModel"

val Solve.Request<ExecutionContext>.tautModel
    get() = if (TAUT_MODEL !in context.customData.durable) {
        Model()
    } else {
        context.customData.durable[TAUT_MODEL] as Model
    }

fun SideEffectsBuilder.setTautModel(chocoModel: Model) {
    setDurableData(TAUT_MODEL, chocoModel)
}

internal val bound
    get() = Int.MAX_VALUE / 2