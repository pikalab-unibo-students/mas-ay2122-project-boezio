package clpqr

import clpCore.chocoModel
import clpCore.setChocoModel
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate



object Constraint: UnaryPredicate.NonBacktrackable<ExecutionContext>("{}") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        val chocoModel = chocoModel
        val modelVarNames = chocoModel.vars.map { it.name }
        // Creation of new variables (simplified version)
        val vars = first.variables
        val newVars = mutableListOf<Var>()
        for (variable in vars) {
            if (modelVarNames.none { it == variable.name }) {
                newVars.add(variable)
            }
        }
        for (variable in newVars) {
            chocoModel.realVar(variable.name, Double.MIN_VALUE, Double.MAX_VALUE, chocoModel.precision)
        }

        // Imposing constraints
        imposeConstraint(first, chocoModel)


        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}

