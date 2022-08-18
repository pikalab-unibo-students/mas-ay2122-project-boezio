package clpqr.optimization


import clpCore.chocoModel
import clpCore.variablesMap
import clpqr.createChocoSolver
import clpqr.optimalSolution
import clpqr.search.Configuration
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

abstract class Optimum(operator: String): QuaternaryRelation.NonBacktrackable<ExecutionContext>(operator) {

    protected abstract val problemType: ProblemType

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term, fourth: Term): Solve.Response {
        ensuringArgumentIsStruct(0)
        val expressionVars = first.variables.distinct().toList()
        ensuringArgumentIsVariable(1)
        val inf = second.castToVar()
        ensuringArgumentIsList(3)
        require(third.castToList().args.all { it is Var }){
            "Vector is not a list of variables"
        }
        val vector = third.variables.distinct().toList()
        ensuringArgumentIsVariable(4)
        val vertex = fourth.castToVar()
        val varsMap = chocoModel.variablesMap(expressionVars)
        val config = Configuration(problemType = problemType, objective = first)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        return replyWith(solver.optimalSolution(varsMap, first, inf, vector, vertex).last())
    }

}