package clpVarCreation

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.Arithmetic
import org.chocosolver.solver.constraints.Constraint
import org.chocosolver.solver.expression.discrete.arithmetic.ArExpression
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin
import org.chocosolver.solver.search.strategy.selectors.values.IntValueSelector
import org.chocosolver.solver.search.strategy.selectors.variables.DomOverWDeg
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail
import org.chocosolver.solver.search.strategy.selectors.variables.InputOrder
import org.chocosolver.solver.search.strategy.selectors.variables.Largest
import org.chocosolver.solver.search.strategy.selectors.variables.Smallest
import org.chocosolver.solver.search.strategy.selectors.variables.VariableSelector
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy
import org.chocosolver.solver.search.strategy.strategy.IntStrategy
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable
import it.unibo.tuprolog.core.List as LogicList


private const val CHOCO_MODEL = "chocoModel"

private val Solve.Request<ExecutionContext>.chocoModel
    get() = if (CHOCO_MODEL !in context.customData.durable) {
        Model()
    } else {
        context.customData.durable[CHOCO_MODEL] as Model
    }

private fun Model.variablesMap(logicVariables: Set<Var>): Map<Variable, Var> {
    val logicVariablesByName = logicVariables.groupBy { it.completeName }.mapValues { it.value.single() }
    return vars.associateWith { logicVariablesByName[it.name]!! }
}

object In : BinaryRelation.NonBacktrackable<ExecutionContext>("in") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsVariable(0)
        val varName = first.castToVar().completeName
        ensuringArgumentIsCompound(1)
        val domainStruct = second.castToStruct()
        if (domainStruct.let { it.arity != 2 || it.functor != ".." || it[0] !is Integer || it[1] !is Integer }) {
            throw IllegalStateException("Argument 2 should be a compound term of the type '..'(int, int)")
        }
        val lb = domainStruct[0].castToInteger().intValue.toInt()
        val ub = domainStruct[1].castToInteger().intValue.toInt()
        if (lb > ub) {
            return replyFail()
        }
        val chocoModel = chocoModel
        // checking whether the model has already been created
        if (chocoModel.vars.any { it.name == varName }) {
            throw IllegalStateException("Variable $varName already defined")
        } else {
            chocoModel.intVar(varName, lb, ub)
            return replySuccess {
                setDurableData(CHOCO_MODEL, chocoModel)
            }
        }
    }
}

private val Variable.valueAsTerm: Term
    get() {
        when (this) {
            is IntVar -> TODO()
            else -> TODO()
        }
    }

data class LabellingConfiguration(
    var variableSelection: VariableSelectionStrategy = VariableSelectionStrategy.LEFTMOST,
    var problemType: ProblemType = ProblemType.SATISFY,
    var valueOrder: ValueOrder = ValueOrder.UP,
    var objective: Struct? = null // expression to be optimized if problemType is either MAXIMISE or MINIMISE
)

enum class VariableSelectionStrategy {
    LEFTMOST, FIRST_FAIL, FFC, MIN, MAX;

    fun toVariableSelector(): VariableSelector<IntVar> = TODO()

    companion object {
        fun fromAtom(atom: Atom): VariableSelectionStrategy? = when(atom.value) {
            "ff" -> FIRST_FAIL
            "ffc" -> FFC
            "min" -> MIN
            "max" -> MAX
            else -> null
        }
    }
}

enum class ProblemType {
    SATISFY, MAXIMISE, MINIMISE
}

enum class ValueOrder {
    UP, DOWN;

    companion object {
        fun fromAtom(atom: Atom): ValueOrder? = when(atom.value) {
            "up" -> UP
            "down" -> DOWN
            else -> null
        }
    }
}

enum class BranchingStrategy {
    STEP, ENUM, BISECT
}

fun parseConfiguration(arguments: List<Term>): LabellingConfiguration {
    val configuration = LabellingConfiguration()
    for (term in arguments) {
        if (term.isAtom) {
            val atom = term.castToAtom()
            VariableSelectionStrategy.fromAtom(atom)?.let { configuration.variableSelection = it }
            ValueOrder.fromAtom(atom)?.let { configuration.valueOrder = it }
        } else {
            val struct = term.asStruct()
            configuration.problemType = if (struct.let {it?.arity == 1 && it.functor == "max"}) {
                ProblemType.MAXIMISE
            } else if (struct.let {it?.arity == 1 && it.functor == "min"}) {
                ProblemType.MINIMISE
            } else {
                ProblemType.SATISFY
            }
            configuration.objective = struct!!.args[0].asStruct()
        // if (struct?.arity == 1) struct.args[0].asStruct() else null
        }
    }
    return configuration
}

// X + 1 - Z
// -(+(X, 1), Z)
fun parseExpression(variables: Map<Var, Variable>, term: Term): ArExpression {
    return when (term) {
        is Struct -> parseExpression(term)
        is Var -> parseExpression(term)
        else -> throw IllegalStateException()
    }
}

fun parseExpression(variables: Map<Var, Variable>, struct: Var): ArExpression {
    return variables[struct] as IntVar
}

fun Variable.toExpression(): ArExpression =
    when (this) {
        is IntVar -> this
        else -> throw IllegalStateException()
    }

fun parseExpression(variables: Map<Var, Variable>, struct: Struct): ArExpression {
    when {
        struct.arity == 2 -> when {
            struct.functor == "+" -> return parseSum(variables, struct[0], struct[1])
            struct.functor == "-" -> return parseSub(variables, struct[0], struct[1])
            struct.functor == "*" -> return parseMult(variables, struct[0], struct[1])
            struct.functor == "/" -> return parseDiv(variables, struct[0], struct[1])
        }
        struct.arity == 1 -> when {
            struct.functor == "abs" -> return parseAbs(variables, struct[0])
        }
    }
    throw IllegalStateException()
}

fun parseAbs(variables: Map<Var, Variable>, term: Term): ArExpression {
    val first = parseExpression(variables, term)
    return first.abs()
}

fun parseSum(variables: Map<Var, Variable>, term1: Term, term2: Term): ArExpression {
    return when {
        term1 is Numeric && term2 is Numeric -> TODO()
        term1 is Numeric -> {
            val second = parseExpression(variables, term2)
            return second.add(term1.intValue.toInt())
        }
        term2 is Numeric -> {
            val first = parseExpression(variables, term1)
            return first.add(term2.intValue.toInt())
        }
        else -> {
            val first = parseExpression(variables, term1)
            val second = parseExpression(variables, term2)
            return first.add(second)
        }
    }
}

fun parseSub(variables: Map<Var, Variable>, term1: Term, term2: Term): ArExpression {
    val first = parseExpression(variables, term1)
    val second = parseExpression(variables, term2)
    return first.sub(second)
}

fun createChocoSolver(model: Model, config: LabellingConfiguration, variables: Array<IntVar>): Model {

    val variableStrategy: VariableSelector<IntVar>? = when(config.variableSelection) {
        VariableSelectionStrategy.LEFTMOST -> InputOrder(model)
        VariableSelectionStrategy.FIRST_FAIL -> FirstFail(model)
        VariableSelectionStrategy.FFC -> null
        VariableSelectionStrategy.MIN -> Smallest()
        VariableSelectionStrategy.MAX -> Largest()
    }


    val valueStrategy: IntValueSelector = when(config.valueOrder) {
        ValueOrder.UP -> IntDomainMax()
        ValueOrder.DOWN -> IntDomainMin()
    }

    var domWdeg: DomOverWDeg? = null
    val SEED = 1L
    if (variableStrategy == null) {
        domWdeg = DomOverWDeg(variables, SEED, valueStrategy)
    }

    require((config.objective != null) xor (config.problemType == ProblemType.SATISFY))



    when (config.problemType) {
        ProblemType.SATISFY -> {
            // does nothing
        }
        ProblemType.MINIMISE -> {
            model.setObjective(Model.MINIMIZE, parseExpression(config.objective!!).intVar())
        }
        ProblemType.MAXIMISE -> {
            model.setObjective(Model.MAXIMIZE, parseExpression(config.objective!!).intVar())
        }
    }

    // use domWdeg if specified
    domWdeg?.let {
        model.solver.setSearch(IntStrategy(
            variables,
            variableStrategy,
            valueStrategy
        ), domWdeg)
    } ?: model.solver.setSearch(IntStrategy(
        variables,
        variableStrategy,
        valueStrategy
    ))

    return model
}


object Labelling : BinaryRelation.NonBacktrackable<ExecutionContext>("labelling") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val keys: Set<Struct> = (first as LogicList).toList().filterIsInstance<Struct>().toSet()
        ensuringArgumentIsList(1)
        val vars: Set<Var> = (first as LogicList).toList().filterIsInstance<Var>().toSet()
        val chocoModel = chocoModel
        if (Atom.of("min") in keys) {
            TODO("handle min")
        }
        keys.find { it.arity == 1 && it.functor == "max" }?.let {
            TODO("handle $it")
        }
        val chocoToLogic = chocoModel.variablesMap(vars)
        if (chocoModel.solver.solve()) {
            return replyWith(
                Substitution.of(chocoToLogic.map { (k, v) -> v to k.valueAsTerm })
            )
        } else {
            return replyFail()
        }

    }

}

object ClpLibrary : AliasedLibrary by Library.aliased(
    alias = "prolog.clp.int",
    primitives = listOf(In).map { it.descriptionPair }.toMap()
)

fun main() {

    val theory = ClausesParser.withDefaultOperators.parseTheory(
        """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
        """.trimIndent()
    )

    val solver = Solver.prolog.solverOf(
        staticKb = theory,
        libraries = Libraries.of(ClpLibrary)
    )
}