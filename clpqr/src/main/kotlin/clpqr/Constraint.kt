package clpqr

import clpCore.chocoModel
import clpCore.setChocoModel
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate


object Constraint: UnaryPredicate.NonBacktrackable<ExecutionContext>("{}") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        if (first !is Struct) {
            throw TypeError.forArgument(context, signature, TypeError.Expected.COMPOUND, first, 0)
        }
        val chocoModel = chocoModel
        val modelVarNames = chocoModel.vars.map { it.name }
        // Creation of new variables (simplified version)
        val vars = first.variables.distinct().toList()
        val precision = ((context.flags[Precision] ?: Precision.defaultValue) as Real).decimalValue.toDouble()
        for (variable in vars) {
            if (modelVarNames.none { it == variable.completeName }) {
                chocoModel.realVar(variable.completeName, Double.MIN_VALUE, Double.MAX_VALUE, precision)
            }
        }

        // Imposing constraints
        first.accept(ConstraintImposer(chocoModel))

        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}

