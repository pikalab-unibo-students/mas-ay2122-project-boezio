package clpVarCreation.globalConstraints

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper


/**
 * serialized([S1,S2], [D1,D2]) :-
 *          N = 0,
 *          disjoint2([f(S1,D1,N,N), f(S2,D2,N,N)]).
 */


object Serialized : RuleWrapper<ExecutionContext>("serialized", 2) {

    // To implement

}