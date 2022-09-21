package ChocoKt

import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val precision = 0.1

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val y = model.realVar("Y", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val z = model.realVar("Z", Double.MIN_VALUE, Double.MAX_VALUE, precision)

    // Constraints
    x.mul(2.0).add(y).gt(16.0).equation().post()
    x.add(y.mul(2.0)).gt(11.0).equation().post()
    x.add(y.mul(3.0)).gt(15.0).equation().post()
    z.eq(x.mul(30.0).add(y.mul(50.0))).equation().post()

    // solve as maximization problem
    model.setObjective(Model.MINIMIZE, z)

    // solution generation
    val solver = model.solver

    while(solver.solve()) {
        println("$x\n$y\n$z\n")
    }

}