package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.Solution

fun main() {

    val model = Model()

    // definition of variables
    val r = model.realVar("R",260.0)
    val i = model.realVar("I",0.04)
    val p = model.realVar("P", 1000.0)

    // definition of support variables
    val b1 = model.realVar("B1", 0.0, 100.0, 0.001)
    val b2 = model.realVar("B2", 0.0, 100.0, 0.001)
    val b3 = model.realVar("B3", 0.0, 100.0, 0.001)
    val b4 = model.realVar("B4", 0.0, 100.0, 0.001)

    // definition of constraints
    b1.eq(p.mul(i.add(1.0)).sub(r)).equation().post()
    b2.eq(b1.mul(i.add(1.0)).sub(r)).equation().post()
    b3.eq(b2.mul(i.add(1.0)).sub(r)).equation().post()
    b4.eq(b3.mul(i.add(1.0)).sub(r)).equation().post()

    // searching for a feasible solution
    val solver = model.solver

    if(solver.solve()){
        println(b4)
    }else if(solver.isStopCriterionMet){
        println("The solver could not find a solution nor prove that none exists in the given limits")
    }else {
        println("The solver has proved the problem has no solution")
    }

}