package clpVarCreation.globalConstraints

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper

/**
 * cumulative(Tasks) :-
 *          L = 1,
 *          cumulative(Tasks, [limit(L)]).
 */

object CumulativeOne : RuleWrapper<ExecutionContext>("cumulative", 1) {

    // To implement

}