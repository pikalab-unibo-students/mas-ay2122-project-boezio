package clpVarCreation.globalConstraints

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/*

tuples_in([A],C):-
    tuples([A],C).

tuples_in([A|B],C):-
	tuples([A],C),
	tuples_in(B,C).

 */

abstract class TuplesInN: RuleWrapper<ExecutionContext>("tuples_in", 2) {

    val A by variables
    val C by variables

    object Base : TuplesInN() {
        override val Scope.head: List<Term>
            get() = ktListOf(listOf(A),C)

        override val Scope.body: Term
            get() = structOf(TuplesIn.functor, listOf(A), C)
    }

    object Recursive : TuplesInN() {
        val B by variables

        override val Scope.head: List<Term>
            get() = ktListOf(listFrom(A, last = B),C)

        override val Scope.body: Term
            get() = tupleOf(
                structOf(TuplesIn.functor, listOf(A), C),
                structOf(functor, B, C)
            )
    }

}