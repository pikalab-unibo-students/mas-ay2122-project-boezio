package clpfd.globalConstraints

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 *
 * Tasks is a list of struct task/5
 *
 * cumulative(Tasks) :-
 *          cumulative(Tasks, [limit(1)]).
 */

object CumulativeOne : RuleWrapper<ExecutionContext>("cumulative", 1) {

    private val Tasks by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Tasks)

    override val Scope.body: Term
        get() = structOf(CumulativeTwo.functor, Tasks, listOf(structOf("limit", intOf(1))))

}