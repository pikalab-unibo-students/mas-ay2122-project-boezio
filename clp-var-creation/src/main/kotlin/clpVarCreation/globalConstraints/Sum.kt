package clpVarCreation.globalConstraints

import clpVarCreation.*
import clpVarCreation.chocoModel
import clpVarCreation.variablesMap
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
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
        val exprVars = third.variables.toSet()
        val logicVars = listVars.union(exprVars)
        val chocoModel = chocoModel
        val varsMap = chocoModel.variablesMap(logicVars).flip()
        val varsFirstTerm = mutableListOf<IntVar>()
        for(elem in listTerms){
            require(elem.let { it is Var || it is Integer }){
                "$elem is neither a variable nor an integer"
            }
            varsFirstTerm.add(getAsIntVar(elem, varsMap))
        }
        val expParser = ExpressionParser(chocoModel, varsMap)
        val expression = third.accept(expParser).intVar()
        chocoModel.sum(varsFirstTerm.toTypedArray(), operatorsMap[operator], expression).post()
        return replySuccess {
            setChocoModel(chocoModel)
        }
    }
}