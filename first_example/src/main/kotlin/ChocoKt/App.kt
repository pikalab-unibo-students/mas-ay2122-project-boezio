package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.Solver

fun main() {

    val model = Model()

    // definition of the variables
    val numValues = 3
    val x: IntVar = model.intVar("x", 1, numValues)
    val y = model.intVar("y", 1, numValues)
    val z = model.intVar("z", 1, numValues)

    // definition of constraints
    model.arithm(x, "!=", y).post()

    // searching for a feasible solution
    val solver: Solver = model.solver

    if(solver.solve()){
        println("$x")
        println("$y")
        println("$z")
    } else {
        println("The solver has proved the problem has no solution")
    }

}