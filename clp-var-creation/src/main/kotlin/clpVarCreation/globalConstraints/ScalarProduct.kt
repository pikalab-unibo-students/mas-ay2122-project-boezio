package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object ScalarProduct : QuaternaryRelation.NonBacktrackable<ExecutionContext>("scalar_product") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term, fourth: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listCoeffs = first.castToList().toList().filterIsInstance<LogicInteger>().toSet()
        ensuringArgumentIsList(1)
        val secondTerms = second.castToList().toList()
        val listVars = secondTerms.filterIsInstance<Var>().distinct().toSet()
        if (secondTerms.size != listCoeffs.size)
            throw IllegalStateException()
        val coeffs = listCoeffs.map { it.value.toInt() }.toIntArray()
        ensuringArgumentIsAtom(2)
        val operator = third.asAtom()
        val exprVars = fourth.variables.toSet()
        val logicVars = listVars.union(exprVars)
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val Vs = mutableListOf<IntVar>()
        for(elem in secondTerms){
            Vs.add(getAsIntVar(elem, varsMap))
        }
        val expParser = ExpressionParser(chocoModel, varsMap)
        val expression = fourth.accept(expParser).intVar()
        chocoModel.scalar(Vs.toTypedArray(), coeffs, operatorsMap[operator], expression).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}