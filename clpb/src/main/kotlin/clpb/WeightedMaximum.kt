package clpb

import clpCore.chocoModel
import clpCore.flip
import clpCore.solutions
import clpCore.variablesMap
import clpb.utils.bound
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.BoolVar
import it.unibo.tuprolog.core.Substitution as Substitution

object WeightedMaximum: TernaryRelation.NonBacktrackable<ExecutionContext>("weighted_maximum") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val weights = first.castToList().toList()
        require(weights.all{ it is Integer}){
            "$weights is not a list of integers"
        }
        ensuringArgumentIsList(1)
        var vars = second.castToList().toList()
        require(vars.all { it is Var }){
            "$vars is not a list of variables"
        }
        vars = vars.map { it.castToVar() }.toMutableList()
        val weightsSize = weights.size
        val varsSize = vars.size
        require(weightsSize == varsSize){
            "length of weights and length of variables are different: $weightsSize != $varsSize"
        }
        ensuringArgumentIsVariable(2)
        val maximum = third.castToVar()

        val model = chocoModel
        val varsMap = model.variablesMap(vars).toMutableMap()
        // check for new variables
        val newVars = (vars.toSet() subtract varsMap.values.toSet()).toList()
        for(variable in newVars){
            chocoModel.boolVar(variable.completeName)
        }
        // updating the map
        val newVarsMap = chocoModel.variablesMap(newVars)
        varsMap.putAll(newVarsMap)
        // creation of integer variables and coefficients to post scalar constraint
        val intVars = varsMap.values.map { model.intVar("${it.completeName}Int",0,1) }.toTypedArray()
        // impose equality among boolean and integer variables
        val pairs = varsMap.keys.map { it as BoolVar } zip intVars
        pairs.forEach { (a,b) ->  a.eq(b).decompose().post() }
        val coeffs = weights.map { it.castToInteger().value.toInt() }.toIntArray()
        val scalarValue = model.intVar(maximum.completeName, -bound, bound)
        // update the map
        varsMap[scalarValue] = maximum
        // post scalar constraints
        model.scalar(intVars, coeffs, "=", scalarValue).post()
        // generate solution
        model.setObjective(Model.MAXIMIZE, varsMap.flip()[maximum])
        val solver = model.solver
        return replyWith(solver.solutions(varsMap).last())
    }
}