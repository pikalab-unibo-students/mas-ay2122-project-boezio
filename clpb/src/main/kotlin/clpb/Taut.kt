package clpb

import clpCore.chocoModel
import clpCore.flip
import clpCore.setChocoModel
import clpCore.variablesMap
import clpb.utils.BoolExprEvaluator
import clpb.utils.ExpressionParser
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object Taut: BinaryRelation.NonBacktrackable<ExecutionContext>("taut") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(1)
        val truthValue = second.castToVar()
        val chocoModel = chocoModel
        val modelVarNames = chocoModel.vars.map { it.name }
        // Creation of new variables
        val vars = first.variables.distinct().toList()
        // In some cases the expression could contain only a variable or anyone
        if(vars.isEmpty()){ // e.g sat(1 + 0)
            val evaluator = BoolExprEvaluator()
            val result = first.accept(evaluator)
            return if (result){
                replyWith(Substitution.of(truthValue to Integer.of(1)))
            } else{
                replyWith(Substitution.of(truthValue to Integer.of(0)))
            }
        }else{
            for (variable in vars) {
                if (modelVarNames.none { it == variable.completeName }) {
                    chocoModel.boolVar(variable.completeName)
                }
            }
            return if(first is Struct){ // if it is not, e.g. sat(X), it does not make sense to add constraints
                val varsMap = chocoModel.variablesMap(first.variables.distinct().toList())
                // Imposing constraints
                val expression = first.accept(ExpressionParser(chocoModel, varsMap.flip())) as LogOp
                // it is faster to check whether the negated proposition is satisfiable
                chocoModel.addClauses(LogOp.nand(expression))
                val solver = chocoModel.solver
                if(solver.solve())
                    replyWith(Substitution.of(truthValue to Integer.of(0)))
                else
                    replyWith(Substitution.of(truthValue to Integer.of(1)))
            }else{
                replyFail {  }
            }
        }
    }
}