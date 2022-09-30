package ChocoKt

import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val precision = 0.1
    val bound = 500.0

    val x = model.realVar("X", -bound, bound, precision)
    val y = model.realVar("Y", -bound, bound, precision)
    val z = model.realVar("Z", -bound, bound, precision)
    val yInt = model.intVar("xInt", -bound.toInt(), bound.toInt())
    val zInt = model.intVar("xInt", -bound.toInt(), bound.toInt())

    // Constraints
    x.ge(y.add(z)).equation().post()
    y.ge(1.0).equation().post()
    z.ge(1.0).equation().post()

    // solve as maximization problem
    model.setObjective(Model.MINIMIZE, x)

    // solution generation
    val solver = model.solver

    while(solver.solve()) {
        println("$x\n$yInt\n$zInt\n")
    }

}