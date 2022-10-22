package clpfd.reification

import clpCore.chocoModel
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import org.chocosolver.solver.constraints.nary.cnf.LogOp

abstract class UnaryReificationOperator(operator: String): UnaryPredicate.NonBacktrackable<ExecutionContext>(operator) {

    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response {

        val chocoModel = chocoModel
        val parser = ReificationParser(chocoModel, context.substitution, context, signature)
        val struct = Struct.of(functor, first)
        chocoModel.addClauses(struct.accept(parser) as LogOp)
        return replySuccess()
    }

}