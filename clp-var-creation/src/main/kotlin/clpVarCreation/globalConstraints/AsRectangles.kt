package clpVarCreation.globalConstraints

import clpVarCreation.domain.In
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Univ

/*
as_rectangles([], [], []).
as_rectagles([S | Ss], [D | Ds], [f(S, D, 0, 0) | Others]) :-
    as_rectagles(Ss, Ds, Others).
 */


abstract class AsRectangles : RuleWrapper<ExecutionContext>("as_rectangles", 3) {

    object Base: AsRectangles() {

        override val Scope.head: List<Term>
            get() = ktListOf(emptyList, emptyList, emptyList)

    }

    object Recursive: AsRectangles() {

        val S by variables
        val Ss by variables
        val D by variables
        val Ds by variables
        val Others by variables
        val Zero by variables

        override val Scope.head: List<Term>
            get() = ktListOf(
                listFrom(S, last = Ss),
                listFrom(D, last = Ds),
                listFrom(
                    structOf("f", S, D, Zero, Zero),
                    last = Others
                )

            )

        override val Scope.body: Term
            get() = tupleOf(
                structOf(In.functor, Zero, intOf(0)),
                structOf(functor, Ss, Ds, Others)
            )


    }
}