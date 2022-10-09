package ChocoKt

import org.chocosolver.solver.Model

fun main(){

    val model = Model()
    val precision = 0.1

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val z = model.realVar("Z", Double.MIN_VALUE, Double.MAX_VALUE, precision)

    x.gt(0.0).equation().post()
    x.lt(3.0).equation().post()
    z.eq(x.mul(30.0)).equation().post()

    model.setObjective(Model.MAXIMIZE, z)

    val solver = model.solver
    while(solver.solve())
        println("$x - $z")
}