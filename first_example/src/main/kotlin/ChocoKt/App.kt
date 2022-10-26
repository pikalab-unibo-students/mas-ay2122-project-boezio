package ChocoKt

import org.chocosolver.solver.ICause
import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val lb = 1

    val x = model.intVar("X",2, 5)
    println("${x.name} -- lb: ${x.lb} - ub: ${x.ub}")
    println("Propagators x: ${x.nbProps}")
    model.unassociates(x)
    println("Propagators x after disconnection: ${x.nbProps}")
    val y = model.intVar("X", 1, 8)
    println("${y.name} -- lb: ${y.lb} - ub: ${y.ub}")
    println("Propagators y: ${y.nbProps}")

    val solver = model.solver

    if(solver.solve())
        print("${x.value}")

}