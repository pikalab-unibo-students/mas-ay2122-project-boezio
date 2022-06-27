package clpVarCreation

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.variables.Variable


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