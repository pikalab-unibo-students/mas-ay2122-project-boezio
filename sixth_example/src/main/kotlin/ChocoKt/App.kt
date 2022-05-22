package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp

/* Prolog program to play with boolean variables:

   problem(X,Y) :-
    sat(~X + Y).

   query:

   ?-problem(X,Y),labeling([X,Y]).

   Result:

   X = 0, Y = 0

 */

fun main(){

    val model = Model()

    // definition of variables
    val x = model.boolVar("X")
    val y = model.boolVar("Y")

    // constraints
    model.addClauses(LogOp.or(LogOp.nor(x),y))

    // search for a solution
    val solver = model.solver

    if(solver.solve()){
        println("X = ${x.value}, Y = ${y.value}")
    }else if(solver.isStopCriterionMet){
        println("The solver could not find a solution nor prove that none exists in the given limits")
    } else {
        println("The solver has proved the problem has no solution")
    }


}