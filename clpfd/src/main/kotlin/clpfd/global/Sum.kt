package clpfd.global

import clpfd.*
import clpCore.*
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.chocosolver.solver.variables.IntVar

object Sum : TernaryRelation.NonBacktrackable<ExecutionContext>("sum") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listTerms = first.castToList().toList()
        val listVars = listTerms.filterIsInstance<Var>().distinct().toSet()
        ensuringArgumentIsAtom(1)
        val operator = second.asAtom()
        val chocoOperator = operatorsMap[operator] ?: throw DomainError.forArgument(
            context, signature, DomainError.Expected.ATOM_PROPERTY, second
        )
        if(third.let { it is Atom || it is Real})
            throw TypeError.forArgument(context, signature, TypeError.Expected.EVALUABLE, third)
        val exprVars = third.variables.toSet()
        val logicVars = listVars.union(exprVars)
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val varsFirstTerm = mutableListOf<IntVar>()
        for(elem in listTerms){
            if(!(elem.let { it is Var || it is Integer }))
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            varsFirstTerm.add(getAsIntVar(elem, varsMap, context.substitution))
        }
        val expParser = ExpressionParser(chocoModel, varsMap, context.substitution, context, signature)
        val expression = third.accept(expParser).intVar()
        chocoModel.sum(varsFirstTerm.toTypedArray(), chocoOperator, expression).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}