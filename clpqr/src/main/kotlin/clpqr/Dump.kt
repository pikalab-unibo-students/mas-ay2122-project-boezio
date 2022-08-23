package clpqr

import clpqr.utils.ConstraintReplacer
import clpqr.utils.constraints
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.core.List as LogicList


object Dump: TernaryRelation.NonBacktrackable<ExecutionContext>("dump") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        require(first.castToList().toList().all { it is Var }){
            "First argument does not contain only variables"
        }
        val target = first.variables.distinct().toList()
        ensuringArgumentIsList(1)
        val newVars = second.castToList().toList()
        ensuringArgumentIsVariable(2)
        val codedAnswers = third.castToVar()
        // conversion of constraints
        val varsMap = target.zip(newVars).toMap()
        val replacer = ConstraintReplacer(varsMap)
        val newConstraints = constraints.map { it.accept(replacer) }
        val codedAnswerValue = LogicList.of(newConstraints)
        return replyWith(Substitution.of(codedAnswers to codedAnswerValue))
    }

}