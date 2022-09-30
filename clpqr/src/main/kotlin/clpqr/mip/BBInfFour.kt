package clpqr.mip

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/*
   bb_inf(Ints, Expression, Inf, Vertex):-
        bb_inf(Ints, Expression, Inf, Vertex,0).
 */

object BBInfFour: RuleWrapper<ExecutionContext>("bb_inf", 4) {

    private val Ints by variables
    private val Expression by variables
    private val Inf by variables
    private val Vertex by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Ints, Expression, Inf, Vertex)

    override val Scope.body: Term
        get() = structOf(BBInfFive.functor, Ints, Expression, Inf, Vertex, Real.of(0.0))

}