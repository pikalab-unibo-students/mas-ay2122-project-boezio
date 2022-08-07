package clpVarCreation.globalConstraints

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/*
same([_], _).
same([Elem|List],Elem) :-
	same(List, Elem).
 */

abstract class Same : RuleWrapper<ExecutionContext>("same", 2){

    val Elem by variables

    object Base : Same() {
        override val Scope.head: List<Term>
            get() = ktListOf(listOf(anonymous()), anonymous())
    }

    object Recursive : Same() {
        val List by variables

        override val Scope.head: List<Term>
            get() = ktListOf(
                listFrom(Elem, last = List),
                Elem
            )

        override val Scope.body: Term
            get() = structOf(functor, List, Elem)
    }

}