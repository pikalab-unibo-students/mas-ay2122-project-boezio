package ChocoKt

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.parsing.parseAsStruct
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.AliasedLibrary
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.solve.library.Library
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.chocosolver.solver.Solver as ChocoSolver

object In : BinaryRelation.NonBacktrackable<ExecutionContext>("in") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        val chocoSolver: ChocoSolver? = if ("chocoSolver" in context.customData.durable)
            context.customData.durable.get("chocoSolver") as ChocoSolver else null

        replySuccess {
            addDurableData("chocoSolver", chocoSolver!!)
        }
    }
}

object Geq : BinaryRelation.NonBacktrackable<ExecutionContext>("#>") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term): Solve.Response {
        TODO("Not yet implemented")
    }
}

object Label : UnaryPredicate<ExecutionContext>("label") {
    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        TODO("Not yet implemented")
    }
}

object ClpLibrary : AliasedLibrary by Library.aliased(
    alias = "prolog.clp.int",
    primitives = listOf(In, Geq, Label).map { it.descriptionPair }.toMap()
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

    for (solution in solver.solve("problem(X, Y), label([X, Y])".parseAsStruct())) {
        when (solution) {
            is Solution.Yes -> {
                println("yes: " + solution.substitution.getByName("A"))
            }
            is Solution.No -> {
                println("no")
            }
            is Solution.Halt -> {
                println("error: " + solution.exception.message)
            }
        }
    }
}