package clpVarCreation.globalConstraints

import clpVarCreation.domain.In
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper


/*
serialized(Starts, Durations) :-
    as_rectangles(Starts, Durations, Rectangles),
    disjoint2(Rectangles).
 */


object Serialized : RuleWrapper<ExecutionContext>("serialized", 2) {

    val Starts by variables
    val Durations by variables
    val Rectangles by variables

    override val Scope.head: List<Term>
        get() = ktListOf(Starts, Durations)

    override val Scope.body: Term
        get() = tupleOf(
            structOf(AsRectangles.Base.functor, Starts, Durations, Rectangles),
            structOf(Disjoint2.functor, Rectangles)
        )

}