package clpqr.utils

import clpCore.chocoModel
import clpCore.flip
import clpCore.getOuterVariable
import clpCore.valueAsTerm
import clpqr.Precision
import clpqr.search.ProblemType
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder
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
        val parser = ExpressionParser(chocoModel, variables.flip(), context.substitution)
        val objectiveExpression = config.objective!!.accept(parser)
        val precision = (context.flags[Precision] as Real).decimalValue.toDouble()
        chocoModel.setObjective(config.problemType.toChoco()!!, objectiveExpression.realVar(precision))
    }
    return chocoModel.solver
}

internal fun Solver.getVectorValue(varsMap: Map<Variable, Var>, vector: List<Var>): Sequence<List<Term>> = sequence {
    // filter solution variables
    val vectorMap = varsMap.filter { (_,v) -> vector.any { it == v } }
    while (solve()) {
        // values of variables in vector
        yield(vectorMap.map { (k, _) -> k.valueAsTerm })
    }
}

internal fun Solver.calculateExpression(
    varsMap: Map<Variable, Var>,
    expression: Term,
    substitution: Substitution.Unifier
): Sequence<Double> = sequence {
    val parser = ExpressionEvaluator(varsMap.flip(), substitution)
    while (solve()) {
        yield(expression.accept(parser))
    }
}

private const val CONSTRAINTS = "constraints"
val Solve.Request<ExecutionContext>.constraints
    get() = if (CONSTRAINTS !in context.customData.durable) {
        mutableListOf<Term>()
    } else {
        context.customData.durable[CONSTRAINTS] as MutableList<Term>
    }

fun SideEffectsBuilder.setConstraints(constraints: MutableList<Term>) {
    setDurableData(CONSTRAINTS, constraints)
}

fun Map<Variable, Var>.filterNotConstantVar(): Map<Variable, Var> {
    return this.filter { (k,_) -> !k.name[0].isDigit() }
}

internal val Solve.Request<ExecutionContext>.bound
        get() = Int.MAX_VALUE / 2


// Ensure the expression to optimize is a variable
internal fun Solve.Request<ExecutionContext>.convertExpression(
    expression: Term,
    varsMap: Map<Variable, Var>,
    substitution: Substitution.Unifier
): Var{
    return if(expression is Var)
        expression.getOuterVariable(substitution)
    else{
        val precision = ((context.flags[Precision] ?: Precision.defaultValue) as Real).decimalValue.toDouble()
        val parser = ExpressionParser(chocoModel, varsMap.flip(), context.substitution)
        // convert expression from 2p-kt to Choco data structure
        val chocoExpr = expression.accept(parser)
        // introduce new variable which denotes the expression
        val newExpr = Var.of("Placeholder")
        val placeholder = chocoModel.realVar(newExpr.completeName, Double.MIN_VALUE, Double.MAX_VALUE, precision)
        placeholder.eq(chocoExpr).equation().post()

        return newExpr
    }
}