package clpb

import clpCore.*
import clpb.utils.bound
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.BoolVar

object WeightedMaximum: TernaryRelation.NonBacktrackable<ExecutionContext>("weighted_maximum") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val weights = first.castToList().toList()
        for(weight in weights){
            if (weight !is Integer)
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, weight)
        }
        ensuringArgumentIsList(1)
        var vars = second.castToList().toList()
        for(variable in vars){
            if(variable !is Var)
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, variable)
        }
        vars = vars.map { it.castToVar() }.toMutableList()
        val weightsSize = weights.size
        val varsSize = vars.size
        if(weightsSize != varsSize)
            throw DomainError.forArgument(context, signature, DomainError.Expected.ATOM_PROPERTY, Integer.of(varsSize))
        ensuringArgumentIsVariable(2)
        val maximum = third.castToVar()

        val model = chocoModel
        val varsMap = model.variablesMap(vars, context.substitution).toMutableMap()
        // check for new variables
        val newVars = (vars.toSet() subtract varsMap.values.toSet()).toList()
        for(variable in newVars){
            model.boolVar(variable.completeName)
        }
        // updating the map
        val newVarsMap = model.variablesMap(newVars, context.substitution)
        varsMap.putAll(newVarsMap)
        // creation of integer variables and coefficients to post scalar constraint
        val intVars = varsMap.values.map { model.intVar("${it.completeName}Int",0,1) }.toTypedArray()
        // impose equality among boolean and integer variables
        val pairs = varsMap.keys.map { it as BoolVar } zip intVars
        pairs.forEach { (a,b) ->  a.eq(b).decompose().post() }
        val coeffs = weights.map { it.castToInteger().value.toInt() }.toIntArray()
        val maximumOuterVar = maximum.getOuterVariable(context.substitution)
        val scalarValue = model.intVar(maximumOuterVar.completeName, -bound, bound)
        // update the map
        varsMap[scalarValue] = maximumOuterVar
        // post scalar constraints
        model.scalar(intVars, coeffs, "=", scalarValue).post()
        // generate solution
        model.setObjective(Model.MAXIMIZE, varsMap.flip()[maximumOuterVar])
        val solver = model.solver
        return replySuccess(solver.solutions(varsMap).last().castToUnifier()) {
            // this is a trick to allow to use the property isInstantiated in labeling
            solver.hardReset()
            solver.solve()
            setChocoModel(model)
        }
    }
}