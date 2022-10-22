package clpfd.global

import clpCore.*
import clpfd.*
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object ScalarProduct : QuaternaryRelation.NonBacktrackable<ExecutionContext>("scalar_product") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term, fourth: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listCoeffs = first.castToList().toList()
        val listIntegerCoeffs = listCoeffs.filterIsInstance<LogicInteger>().toSet()
        for(elem in listCoeffs){
            if(elem is Var)
                throw ExistenceError.of(
                    context,
                    ExistenceError.ObjectType.RESOURCE,
                    elem,
                    "Variable coefficients are still not supported"
                )
            else if(elem !is LogicInteger)
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
        }
        ensuringArgumentIsList(1)
        val secondTerms = second.castToList().toList()
        val listVars = secondTerms.filterIsInstance<Var>().distinct().toSet()
        val coeffs = listIntegerCoeffs.map { it.value.toInt() }.toIntArray()
        ensuringArgumentIsAtom(2)
        val operator = third.asAtom()
        val chocoOperator = operatorsMap[operator] ?: throw DomainError.forArgument(
            context, signature, DomainError.Expected.ATOM_PROPERTY, second
        )
        if(fourth.let { it is Atom || it is Real })
            throw TypeError.forArgument(context, signature, TypeError.Expected.EVALUABLE, fourth)
        val exprVars = fourth.variables.toSet()
        val logicVars = listVars.union(exprVars)
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars, context.substitution).flip()
        val vs = mutableListOf<IntVar>()
        for(elem in secondTerms){
            if(!(elem.let { it is Var || it is LogicInteger })){
                throw TypeError.forArgument(context, signature, TypeError.Expected.INTEGER, elem)
            }
            vs.add(getAsIntVar(elem, varsMap, context.substitution))
        }
        val expParser = ExpressionParser(chocoModel, varsMap, context.substitution, context, signature)
        val expression = fourth.accept(expParser).intVar()
        chocoModel.scalar(vs.toTypedArray(), coeffs, chocoOperator, expression).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}