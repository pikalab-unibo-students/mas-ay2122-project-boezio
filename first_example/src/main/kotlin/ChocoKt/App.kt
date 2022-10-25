package ChocoKt

import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val lb = 1
    val ub = 10

    val x = model.intVar("X",lb, ub)

    println(x.isInstantiated)

    val solver = model.solver
    if (solver.solve())
        print(x.isInstantiated)

}