package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.objective.ParetoMaximizer
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMax
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail
import org.chocosolver.solver.search.strategy.strategy.IntStrategy


/* Prolog program

problem(X,Y) :-
    X in 1 \/ 3 \/ 5,
    Y in 2 \/ 4,
    X #> Y.

?-problem(X,Y), labeling([ff, up, max(X), min(Y)], [X,Y]).

Result: X = 5, Y = 2

 */

fun main() {

    val model = Model()

    // definition of variables

    val x = model.intVar("x", intArrayOf(1,3,5))
    val y = model.intVar("y", intArrayOf(2,4))

    // definition of constraints
    x.gt(y).decompose().post()

    // search for a feasible solution

    val solver = model.solver
    // search heuristics
    solver.setSearch(
        IntStrategy(
            arrayOf(x,y),
            FirstFail(model),
            IntDomainMax()
        )
    )
    // multi-optimization problem
    val po = ParetoMaximizer(arrayOf(x, y.neg().intVar()))
    solver.plugMonitor(po)

    // displaying solution

    // retrieve all solutions
    while(solver.solve()){}

    val paretoFront = po.paretoFront
    println("X = ${paretoFront[0].getIntVal(x)}, Y = ${paretoFront[0].getIntVal(y)}")

}