package clpqr

import clpqr.utils.ConstraintReplacer
import clpqr.utils.constraints
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import it.unibo.tuprolog.core.List as LogicList


object Dump: TernaryRelation.NonBacktrackable<ExecutionContext>("dump") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term, second: Term, third: Term): Solve.Response {
        ensuringArgumentIsList(0)
        val varsList = first.castToList().toList()
        for(variable in varsList){
            if(variable !is Var)
                throw TypeError.forArgument(context, signature, TypeError.Expected.VARIABLE, variable)
        }
        val target = varsList.map { it.castToVar() }
        ensuringArgumentIsList(1)
        val newVars = second.castToList().toList()
        if(target.size != newVars.size)
            throw DomainError.forArgument(
                context,
                signature,
                DomainError.Expected.ATOM_PROPERTY,
                Integer.of(target.size)
            )
        ensuringArgumentIsVariable(2)
        val codedAnswer = third.castToVar()
        // conversion of constraints
        val varsMap = target.zip(newVars).toMap()
        val replacer = ConstraintReplacer(varsMap, context, signature)
        val newConstraints = constraints.map { it.accept(replacer) }
        val codedAnswerValue = LogicList.of(newConstraints)
        return replyWith(Substitution.of(codedAnswer to codedAnswerValue))
    }

}