package clpb

import clpCore.chocoModel
import clpCore.flip
import clpCore.setChocoModel
import clpCore.variablesMap
import clpb.utils.BoolExprEvaluator
import clpb.utils.ExpressionParser
import clpb.utils.setTautModel
import clpb.utils.tautModel
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object Sat: UnaryPredicate.NonBacktrackable<ExecutionContext>("sat") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {
        val chocoModel = chocoModel
        val modelVarNames = chocoModel.vars.map { it.name }
        // model to use for checking a tautology
        val tautModel = tautModel
        // Creation of new variables
        val vars = first.variables.distinct().toList()
        // In some cases the expression could contain only a variable or anyone
        if(vars.isEmpty()){ // e.g sat(1 + 0)
            val evaluator = BoolExprEvaluator()
            val result = first.accept(evaluator)
            return if (result){
                replySuccess()
            } else{
                replyFail()
            }
        }else{
            for (variable in vars) {
                if (modelVarNames.none { it == variable.completeName }) {
                    val varName = variable.completeName
                    chocoModel.boolVar(varName)
                    tautModel.boolVar(varName)
                }
            }
            if(first is Struct){ // if it is not, e.g. sat(X), it does not make sense to add constraints
                val varsMap = chocoModel.variablesMap(vars)
                val tautMap = tautModel.variablesMap(vars)
                // Imposing constraints
                val expression = first.accept(ExpressionParser(chocoModel, varsMap.flip())) as LogOp
                val tautExpression = first.accept(ExpressionParser(tautModel, tautMap.flip())) as LogOp
                chocoModel.addClauses(expression)
                tautModel.addClauses(tautExpression)
            }
            // positive outcome if there is at least a solution
            val solver = chocoModel.solver

            return if(solver.solve()){
                replySuccess {
                    setChocoModel(chocoModel)
                    setTautModel(tautModel)
                    // to avoid unexpected results in other predicates
                    solver.hardReset()
                }
            }else{
                replyFail {  }
            }
        }
    }
}