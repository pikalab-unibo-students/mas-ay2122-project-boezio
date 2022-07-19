package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.flip
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsAtom
import it.unibo.tuprolog.solve.primitive.PrimitiveWrapper.Companion.ensuringArgumentIsList
import it.unibo.tuprolog.core.Integer as LogicInteger
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import org.chocosolver.solver.variables.IntVar

object ScalarProduct : QuaternaryRelation.NonBacktrackable<ExecutionContext>("scalar_product") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term, fourth: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val listVars = first.castToList().toList().filterIsInstance<Var>().distinct().toSet()
        ensuringArgumentIsList(1)
        // this predicate is more limited than SWI Prolog one because it accepts only integer coefficients
        val listCoeffs = second.castToList().toList().filterIsInstance<LogicInteger>().toSet()
        if (listVars.size != listCoeffs.size)
            throw IllegalStateException()
        val coeffs = listCoeffs.map { it.value as Int } as IntArray
        ensuringArgumentIsAtom(2)
        val operator = third.asAtom()
        val exprVars = fourth.variables.toSet()
        val logicVars = listVars.union(exprVars)
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars)
        val varsFirstTerm = chocoModel.variablesMap(listVars).keys.map { it as IntVar }.toTypedArray()
        val expParser = ExpressionParser(chocoModel, varsMap.flip())
        val expression = third.accept(expParser).intVar()
        return replySuccess {
            chocoModel.scalar(varsFirstTerm, coeffs, operatorsMap[operator], expression).post()
        }
    }
}