package clpfd.global

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.classic.stdlib.rule.Call
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.Univ

/**
 * chain([], _).
 * chain([X, Y | Z], Op) :-
 *   Constraint =.. [Op, X, Y],
 *   call(Constraint),
 *   chain([Y | Z], Op).
 */

abstract class Chain : RuleWrapper<ExecutionContext>("chain", 2) {

    // chain([], _).
    object Base : Chain() {
        override val Scope.head: List<Term>
            get() = ktListOf(emptyList, anonymous())
    }

    // chain([X, Y | Z], Op) :-
    //  Constraint =.. [Op, X, Y],
    //  call(Constraint),
    //  chain([Y | Z], Op).
    @Suppress("MemberVisibilityCanBePrivate")
    object Recursive : Chain() {
        val X by variables
        val Y by variables
        val Z by variables
        val Op by variables
        val Constraint by variables

        override val Scope.head: List<Term>
            get() = ktListOf(
                listFrom(X, Y, last = Z), // [X, Y | Z]
                Op
            )

        override val Scope.body: Term
            get() = tupleOf(
                structOf(Univ.functor, Constraint, listOf(Op, X, Y)), // Constraint =.. [Op, X, Y]
                structOf(Call.functor, Constraint), // call(Constraint)
                structOf(functor, listFrom(Y, last = Z), Op) // chain([Y | Z], Op)
            )
    }
}