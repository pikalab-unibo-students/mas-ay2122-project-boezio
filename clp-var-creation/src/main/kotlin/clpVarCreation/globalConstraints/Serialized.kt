package clpVarCreation.globalConstraints

import clpVarCreation.domain.In
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Univ


/**
 * serialized([S1,S2], [D1,D2]) :-
 *          N = 0,
 *          disjoint2([f(S1,D1,N,N), f(S2,D2,N,N)]).
 *
 * serialized([S1,S2 | S3], [D1,D2|D3]) :-
 *          serialized([S1 | S3],[D1 |D3]),
 *          serialized([S2|S3],[D2|D3]),
*           serialized([S1,S2],[D1,D2]).
 */


abstract class Serialized : RuleWrapper<ExecutionContext>("serialized", 2) {

    protected val S1 by variables
    protected val S2 by variables
    protected val D1 by variables
    protected val D2 by variables
    protected val N by variables

    object Base: Serialized() {


        override val Scope.head: List<Term>
            get() = ktListOf(
                listOf(S1,S2),
                listOf(D1,D2)
            )

        override val Scope.body: Term
            get() = tupleOf(
                structOf(In.functor, N, intOf(0)), //N = 0
                structOf(
                    Disjoint2.functor,
                    listOf(
                        structOf("f",S1,D1,N,N),
                        structOf("f",S2,D2,N,N)
                    )
                ) // disjoint2([f(S1,D1,N,N), f(S2,D2,N,N)])
            )
    }

    object Recursive: Serialized() {

        val S3 by variables
        val D3 by variables

        override val Scope.head: List<Term>
            get() = ktListOf(
                listFrom(S1, S2, last = S3),
                listFrom(D1, D2, last = D3)
            )

        override val Scope.body: Term
            get() = tupleOf(
                structOf(functor, listFrom(S1, last = S3), listFrom(D1, last = D3)),
                structOf(functor, listFrom(S2, last = S3), listFrom(D2, last = D3)),
                structOf(functor, listOf(S1, S2), listOf(D1, D2))
            )

    }


}