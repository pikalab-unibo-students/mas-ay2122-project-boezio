package ChocoKt

import org.chocosolver.solver.Model

fun main(){

    val model = Model()
    val precision = 0.1

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)

    x.gt(10.0).equation().post()

    if(model.solver.solve())
        print(x.lt(5.0).equation().isSatisfied)

}