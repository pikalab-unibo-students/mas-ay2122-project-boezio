package ChocoKt

import org.chocosolver.solver.Model

// Exercise concerning epsilon precision
fun main() {

    val model = Model()

    // definition of precision
    val eps = 1.0

    // definition of variables
    val x = model.realVar("x", 10.00, 20.00, eps)
    val y = model.realVar("y", 10.00, 20.00, eps)

    // constraint to find two values having a difference smaller than eps
    x.sub(y).lt(eps).equation().post()
    x.sub(y).gt(0.0).equation().post()


    val solver = model.solver

    if (solver.solve()) {
        println(x)
        println(y)
    } else if (solver.isStopCriterionMet){
        println("The solver could not find a solution nor prove that none exists in the given limits")
    } else {
        println("The solver has proved the problem has no solution")
    }

}