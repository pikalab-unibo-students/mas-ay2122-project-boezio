package clpfd.reification

import clpfd.relational.GreaterThan
import clpfd.relational.LessThan
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

abstract class ZCompare: RuleWrapper<ExecutionContext>("zcompare", 3) {

    val A by variables
    val B by variables

    object Greater: ZCompare(){

        /*
        zcompare(>,A,B) :- #>(A,B).
         */

        override val Scope.head: List<Term>
            get() = ktListOf(atomOf(">"), A, B)


        override val Scope.body: Term
            get() = structOf(GreaterThan.functor, A, B)

    }

    object Less: ZCompare(){

        /*
        zcompare(<,A,B) :- #<(A,B).
         */

        override val Scope.head: List<Term>
            get() = ktListOf(atomOf("<"), A, B)


        override val Scope.body: Term
            get() = structOf(LessThan.functor, A, B)

    }

    object Equals: ZCompare(){

        /*
        zcompare(=,A,B) :- #=(A,B).
         */

        override val Scope.head: List<Term>
            get() = ktListOf(atomOf("="), A, B)


        override val Scope.body: Term
            get() = structOf(clpfd.relational.Equals.functor, A, B)

    }
}