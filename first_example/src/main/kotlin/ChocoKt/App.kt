package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.Solver

fun main() {

    val model = Model()
    val lb = 1
    val ub = 10

    val x = model.intVar("X",lb, ub)
    val y = model.intVar("Y", lb, ub)
    val z = model.intVar("Z", lb, ub)

    x.gt(y).decompose().post()
    y.gt(z).decompose().post()

    // searching for a feasible solution
    val solver: Solver = model.solver

    if(solver.solve()) {
        println("${x.value} ${y.value} ${z.value}")
    }

}