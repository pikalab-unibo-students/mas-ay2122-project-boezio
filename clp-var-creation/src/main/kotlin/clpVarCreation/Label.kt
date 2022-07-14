package clpVarCreation

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * label(Vars) :- labeling([], Vars).
 */
object Label : RuleWrapper<ExecutionContext>("label", 1) {

    private val Vars by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Vars)

    override val Scope.body: Term
        get() = structOf(Labeling.functor, emptyList, Vars)
}
