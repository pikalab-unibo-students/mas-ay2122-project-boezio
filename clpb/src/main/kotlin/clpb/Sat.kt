package clpb

import clpCore.chocoModel
import clpCore.flip
import clpCore.setChocoModel
import clpCore.variablesMap
import clpb.utils.ExpressionParser
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object Sat: UnaryPredicate.NonBacktrackable<ExecutionContext>("sat") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val chocoModel = chocoModel
        val modelVarNames = chocoModel.vars.map { it.name }
        // Creation of new variables
        val vars = first.variables.distinct().toList()
        for (variable in vars) {
            if (modelVarNames.none { it == variable.completeName }) {
                chocoModel.boolVar(variable.completeName)
            }
        }
        val varsMap = chocoModel.variablesMap(first.variables.distinct().toList())
        // Imposing constraints
        val expression = first.accept(ExpressionParser(chocoModel, varsMap.flip())) as LogOp
        chocoModel.addClauses(expression)

        // positive outcome if there is at least a solution
        val solver = chocoModel.solver

        return if(solver.solve()){
            replySuccess {
                setChocoModel(chocoModel)
            }
        }else{
            replyFail {  }
        }
    }
}