package clpVarCreation.globalConstraints

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/*
sum(Vs, Op, Expr) :-
	same(Cs, 1),
	length(Vs, Lv),
	length(Cs, Lv),
	scalar_product(Cs, Vs, Op, Expr).
 */

object Sum : RuleWrapper<ExecutionContext>("sum", 3) {

    val Vs by variables
    val Cs by variables
    val Op by variables
    val Expr by variables
    val Lv by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Vs, Op, Expr)

    override val Scope.body: Term
        get() = tupleOf(
            structOf(Same.Base.functor, Cs, intOf(1)),
            structOf("length", Vs, Lv),
            structOf("length", Cs, Lv),
            structOf(ScalarProduct.functor, Cs, Vs, Op, Expr)
        )
}