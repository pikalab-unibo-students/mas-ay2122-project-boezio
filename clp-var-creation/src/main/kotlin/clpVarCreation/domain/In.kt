package clpVarCreation.domain

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * in(Var, Domain) :- ins(Vars, Domain).
 */
object In : RuleWrapper<ExecutionContext>("in", 2) {

    private val Var by variables
    private val Domain by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Var, Domain)

    override val Scope.body: Term
        get() = structOf(Ins.functor, listOf(Var), Domain)
}