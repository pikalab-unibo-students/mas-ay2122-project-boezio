package clpfd.reification

import clpfd.relational.GreaterThan
import clpfd.relational.LessThan
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

abstract class ZCompare: RuleWrapper<ExecutionContext>("zcompare", 3) {

    object Greater: ZCompare(){

        /*
        zcompare(>,A,B) :- #>(A,B).
         */

        val A by variables
        val B by variables

        override val Scope.head: List<Term>
            get() = ktListOf(Atom.of(">"), A, B)


        override val Scope.body: Term
            get() = Struct.of(GreaterThan.functor, A, B)

    }

    object Less: ZCompare(){

        /*
        zcompare(<,A,B) :- #<(A,B).
         */

        val A by variables
        val B by variables

        override val Scope.head: List<Term>
            get() = ktListOf(Atom.of("<"), A, B)


        override val Scope.body: Term
            get() = Struct.of(LessThan.functor, A, B)

    }

    object Equals: ZCompare(){

        /*
        zcompare(=,A,B) :- #=(A,B).
         */

        val A by variables
        val B by variables

        override val Scope.head: List<Term>
            get() = ktListOf(Atom.of("="), A, B)


        override val Scope.body: Term
            get() = Struct.of(clpfd.relational.Equals.functor, A, B)

    }
}