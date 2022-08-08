package clpVarCreation.globalConstraints

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper


/*

lex_chain([A,B]):-
    lex([A,B]).

lex_chain([A |B,C]) :-
    lex([A,B]),
    lex_chain([B,C]).

 */

abstract class LexChainN: RuleWrapper<ExecutionContext>("lex_chain", 1) {

    val A by variables
    val B by variables

    object Base : LexChainN() {
        override val Scope.head: List<Term>
            get() = ktListOf(listOf(A,B))

        override val Scope.body: Term
            get() = structOf(LexChain.functor, listOf(A,B))
    }

    object Recursive : LexChainN() {
        val C by variables

        override val Scope.head: List<Term>
            get() = ktListOf(listOf(A,B,C))

        override val Scope.body: Term
            get() = tupleOf(
                structOf(LexChain.functor, listOf(A,B)),
                structOf(functor, listOf(B,C))
            )
    }
}