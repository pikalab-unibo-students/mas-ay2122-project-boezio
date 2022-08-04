package clpVarCreation.globalConstraints

import clpVarCreation.domain.In
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Univ

/**
 *
 * Tasks is a list of struct task/5
 *
 * cumulative(Tasks) :-
 *          L = 1,
 *          cumulative(Tasks, [limit(L)]).
 */

object CumulativeOne : RuleWrapper<ExecutionContext>("cumulative", 1) {

    val Tasks by variables
    val L by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Tasks)

    override val Scope.body: Term
        get() = tupleOf(
            structOf(In.functor, L, intOf(1)),
            structOf(CumulativeTwo.functor, Tasks, listFrom(structOf("limit", L)))
        )

}