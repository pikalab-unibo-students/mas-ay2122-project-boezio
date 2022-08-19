package clpqr.optimization


import clpCore.chocoModel
import clpCore.variablesMap
import clpqr.calculateExpression
import clpqr.createChocoSolver
import clpqr.getVectorValue
import clpqr.search.Configuration
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.QuaternaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.core.List as LogicList

abstract class Optimum(operator: String): QuaternaryRelation.NonBacktrackable<ExecutionContext>(operator) {

    protected abstract val problemType: ProblemType

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term, fourth: Term): Solve.Response {
        val expressionVars = first.variables.distinct().toList()
        ensuringArgumentIsVariable(1)
        val optimum = second.castToVar()
        ensuringArgumentIsList(2)
        require(third.castToList().toList().all { it is Var }){
            "Vector is not a list of variables"
        }
        val vector = third.variables.distinct().toList()
        ensuringArgumentIsVariable(3)
        val vertex = fourth.castToVar()
        val varsMap = chocoModel.variablesMap(expressionVars.toSet().union(vector.toSet()))
        val config = Configuration(problemType = problemType, objective = first)
        val solver = createChocoSolver(chocoModel, config, varsMap)
        // Substitution for Vertex
        val vertexValue = solver.getVectorValue(varsMap, vector).last()
        val vertexList = LogicList.of(vertexValue)
        // Substitution for optima
        val optimumValue = Real.of(solver.calculateExpression(varsMap, first).last())
        return replyWith(Substitution.of(mapOf(optimum to optimumValue, vertex to vertexList)))
    }
}