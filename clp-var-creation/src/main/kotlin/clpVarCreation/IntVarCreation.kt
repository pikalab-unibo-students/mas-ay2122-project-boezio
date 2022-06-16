package clpVarCreation

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.chocosolver.solver.Model


object In : TernaryRelation.NonBacktrackable<ExecutionContext>("in") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        val chocoModel: Model
        val varName = first.toString()
        val lb = second.castToInteger().intValue.toInt()
        val ub = third.castToInteger().intValue.toInt()
        // checking whether the model has been created
        if ("chocoModel" in context.customData.durable) {
            chocoModel = context.customData.durable.get("chocoModel") as Model
            val variable = chocoModel.intVar(varName, lb, ub)
            replySuccess {
                addDurableData(varName, variable)
            }
        } else {
            chocoModel = Model()
            replySuccess {
                addDurableData("chocoModel", chocoModel)
                addDurableData(varName, chocoModel.intVar(varName, lb, ub))
            }
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