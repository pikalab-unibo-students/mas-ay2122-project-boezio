package clpqr

import clpCore.flip
import clpCore.solutions
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import clpqr.search.Configuration as Configuration
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.Variable

internal fun Solve.Request<ExecutionContext>.solve(
    chocoModel: Model,
    config: Configuration,
    variables: Map<Variable, Var>
): Solve.Response {

    if(config.problemType != ProblemType.SATISFY){
        val parser = ExpressionParser(chocoModel, variables.flip())
        val objectiveExpression = config.objective!!.accept(parser)
        chocoModel.setObjective(config.problemType.toChoco()!!, objectiveExpression.realVar(config.precision))
    }
    val solver = chocoModel.solver
    return if (config.problemType == ProblemType.SATISFY) {
        replyWith(solver?.solutions(variables)!!.first())
    } else {
        replyWith(solver?.solutions(variables)!!.last())
    }
}