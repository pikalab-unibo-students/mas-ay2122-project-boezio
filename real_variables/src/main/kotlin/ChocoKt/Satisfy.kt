package ChocoKt

import org.chocosolver.solver.Model

fun main(){

    val model = Model()
    val precision = 0.1

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val y = model.realVar("Y", Double.MIN_VALUE, Double.MAX_VALUE, precision)

    //x.ge(y).equation().post()
    x.add(y).eq(10.0).equation().post()

    val solver = model.solver

    while(solver.solve()) {
        println("$x\n$y")
    }
}