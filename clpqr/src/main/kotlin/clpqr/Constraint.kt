package clpqr

import clpCore.chocoModel
import clpCore.getOuterVariable
import clpCore.setChocoModel
import clpqr.utils.*
import clpqr.utils.ConstraintImposer
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate


object Constraint: UnaryPredicate.NonBacktrackable<ExecutionContext>("{}") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        if (first.let { it is Atom || it is Integer }) {
            throw TypeError.forArgument(context, signature, TypeError.Expected.COMPOUND, first, 0)
        }
        val chocoModel = chocoModel
        val equations = equations
        val subContext = context.substitution
        val modelVarNames = chocoModel.vars.map { it.name }
        // Creation of new variables (simplified version)
        val vars = first.variables.distinct().toList().map { it.getOuterVariable(subContext) }
        val precision = ((context.flags[Precision] ?: Precision.defaultValue) as Real).decimalValue.toDouble()
        for (variable in vars) {
            if (modelVarNames.none { it == variable.completeName }) {
                chocoModel.realVar(variable.completeName, Double.MIN_VALUE, Double.MAX_VALUE, precision)
            }
        }
        // Storing constraint using 2p-Kt classes
        val constraints = constraints
        if(first is Tuple){
            constraints.addAll(first.args)
        }else{
            constraints.add(first)
        }
        // Imposing constraints
        first.accept(ConstraintImposer(chocoModel, context))
        // checking equality constraints
        val eqDetector = EquationDetector(context.substitution, equations)
        val updatedEquations = first.accept(eqDetector)
        return replySuccess {
            setChocoModel(chocoModel)
            setConstraints(constraints)
            setEquations(updatedEquations)
        }
    }
}

