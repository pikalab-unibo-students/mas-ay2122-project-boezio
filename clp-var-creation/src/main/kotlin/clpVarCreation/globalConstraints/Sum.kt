package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.chocosolver.solver.variables.IntVar

object Sum : TernaryRelation.NonBacktrackable<ExecutionContext>("sum") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val logicVars = first.castToList().toList().filterIsInstance<Var>().distinct().toList()
        ensuringArgumentIsAtom(1)
        val operator = second.asAtom()
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars)
        val varsFirstTerm = varsMap.keys.map { it as IntVar }.toTypedArray()
        val expParser = ExpressionParser(chocoModel, varsMap.flip())
        val expression = third.accept(expParser).intVar()
        val operatorPrologChoco = mapOf(
            Atom.of("#=") to "=",
            Atom.of("#\\=") to "!=",
            Atom.of("#>") to ">",
            Atom.of("#<") to "<",
            Atom.of("#>=") to ">=",
            Atom.of("#=<") to "<="
        )
        return replySuccess {
            chocoModel.sum(varsFirstTerm, operatorPrologChoco.get(operator), expression)
        }
    }
}