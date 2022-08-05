package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.Solver

fun main() {

    val model = Model()

    val X = model.intVar(0,2)
    val Y = model.intVar(0,2)
    val Z = model.intVar(0,2)

    model.circuit(listOf(X,Y,Z).toTypedArray()).post()

    // searching for a feasible solution
    val solver: Solver = model.solver

    if(solver.solve()) {
        println("${X.value} ${Y.value} ${Z.value}")
    }

}