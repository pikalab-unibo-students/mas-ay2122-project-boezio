package ChocoKt

import org.chocosolver.solver.Model

// Problem with integer and real variables

fun main() {

    val model = Model()

    // definition of variables
    val x = model.intVar("x",1, 10)
    val y = model.intVar("y", 1, 10)
    val z = model.realVar("z", 1.0,10.0, 0.001)

    // definition of constraints
    // x > y
    x.gt(y).decompose().post()
    // z > x /\ z < 8
    z.gt(x.asRealVar()).equation()
    z.lt(8.0).equation().post()

    // function to maximize
    model.setObjective(Model.MAXIMIZE,z)

    // searching for a feasible solution
    val solver = model.solver

    if(solver.solve()){
        println(z)
    }else if(solver.isStopCriterionMet){
        println("The solver could not find a solution nor prove that none exists in the given limits")
    }else {
        println("The solver has proved the problem has no solution")
    }

}