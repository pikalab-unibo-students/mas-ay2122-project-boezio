package clpb

import clpCore.chocoModel
import clpCore.flip
import clpCore.solutions
import clpCore.variablesMap
import clpb.utils.BoolExprEvaluator
import clpb.utils.ExpressionParser
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.constraints.nary.cnf.LogOp

object SatCount: BinaryRelation.NonBacktrackable<ExecutionContext>("sat_count") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(1)
        val count = second.castToVar()
        val chocoModel = chocoModel
        val vars = first.variables.distinct().toList()
        // expression contains only truth values
        if(vars.isEmpty()){
            val evaluator = BoolExprEvaluator()
            val result = first.accept(evaluator)
            // if the expression is true the count is 1 otherwise 0
            return if (result){
                replyWith(Substitution.of(count to Integer.of(1)))
            } else{
                replyWith(Substitution.of(count to Integer.of(0)))
            }
        }
        // if there is only a variable there is only one possible assignment
        if(first is Var){
            return replyWith(Substitution.of(count to Integer.of(1)))
        }
        val varsMap = chocoModel.variablesMap(vars).toMutableMap()
        // set of variables not contained in the model
        val newVars = (vars.toSet() subtract varsMap.values.toSet()).toList()
        for(variable in newVars){
            chocoModel.boolVar(variable.completeName)
        }
        // update map with new variables
        val newVarsMap = chocoModel.variablesMap(newVars)
        varsMap.putAll(newVarsMap)
        // adding new constraints
        val expression = first.accept(ExpressionParser(chocoModel, varsMap.flip())) as LogOp
        chocoModel.addClauses(expression)
        val solver = chocoModel.solver
        val numSolutions = solver.solutions(varsMap).toList().size
        return replyWith(Substitution.of(count to Integer.of(numSolutions)))
    }
}