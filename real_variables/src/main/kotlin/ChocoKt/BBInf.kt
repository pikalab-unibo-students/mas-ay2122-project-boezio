package ChocoKt

import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val precision = 0.1

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val y = model.realVar("Y", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val z = model.realVar("Z", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val yInt = model.intVar("yInt",-500, 500)
    val zInt = model.intVar("zInt", -500, 500)

    // Constraints
    x.ge(y.add(z)).equation().post()
    y.ge(1.0).equation().post()
    z.ge(1.0).equation().post()
    model.eq(y, yInt).post()
    model.eq(z, zInt).post()

    // solve as maximization problem
    model.setObjective(Model.MINIMIZE, x)

    // solution generation
    val solver = model.solver

    while(solver.solve()) {
        println("$x\n$yInt\n$zInt\n")
    }

}