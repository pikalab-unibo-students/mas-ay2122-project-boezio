package clpb

import clpCore.flip
import clpCore.solutions
import clpCore.variablesMap
import clpb.utils.BoolExprEvaluator
import clpb.utils.ExpressionParser
import clpb.utils.tautModel
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import kotlin.math.pow

object Taut: BinaryRelation.NonBacktrackable<ExecutionContext>("taut") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(1)
        val truthValue = second.castToVar()
        val chocoModel = tautModel
        val modelVarNames = chocoModel.vars.map { it.name }
        // Creation of new variables
        val vars = first.variables.distinct().toList()
        // In some cases the expression could contain only a variable or anyone
        if(vars.isEmpty()){ // e.g sat(1 + 0)
            val evaluator = BoolExprEvaluator(context, signature)
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
            return if(first is Struct){
                val varsMap = chocoModel.variablesMap(vars, context.substitution)
                // Imposing constraints
                val expression = first.accept(ExpressionParser(chocoModel, varsMap.flip())) as LogOp
                chocoModel.addClauses(expression)
                val numPossibleAssignments = 2.0.pow(vars.size).toInt()
                val solver = chocoModel.solver
                val solutions = solver.solutions(varsMap).toList()
                val numSolutions = solutions.size
                return if(numSolutions == numPossibleAssignments){
                    replyWith(Substitution.of(truthValue to Integer.of(1)))
                }else if(numSolutions == 1 && solutions[0] is Substitution.Fail){
                    replyWith(Substitution.of(truthValue to Integer.of(0)))
                }else{
                    replyFail()
                }
            }else{
                replyFail()
            }
        }
    }
}