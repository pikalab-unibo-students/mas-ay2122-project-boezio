package clpqr

import clpCore.flip
import clpCore.solutions
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import clpqr.search.Configuration as Configuration
import org.chocosolver.solver.Model
import org.chocosolver.solver.Solver
import org.chocosolver.solver.variables.Variable

internal fun Solve.Request<ExecutionContext>.createChocoSolver(
    chocoModel: Model,
    config: Configuration,
    variables: Map<Variable, Var>
): Solver {

    if(config.problemType != ProblemType.SATISFY){
        val parser = ExpressionParser(chocoModel, variables.flip())
        val objectiveExpression = config.objective!!.accept(parser)
        val precision = (context.flags[Precision] as Real).decimalValue.toDouble()
        chocoModel.setObjective(config.problemType.toChoco()!!, objectiveExpression.realVar(precision))
    }
    return chocoModel.solver
}