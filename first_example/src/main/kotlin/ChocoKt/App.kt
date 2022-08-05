package ChocoKt

import org.chocosolver.solver.Model
import org.chocosolver.solver.variables.IntVar
import org.chocosolver.solver.Solver

fun main() {

    val model = Model()

    val S1 = model.intVar(0,100)
    val S2 = model.intVar(0,100)
    val S3 = model.intVar(0,100)
    val S4 = model.intVar(0,100)

    val D1 = model.intVar(1)
    val D2 = model.intVar(5)
    val D3 = model.intVar(3)
    val D4 = model.intVar(2)

    val starts = listOf(S1,S2,S3,S4).toTypedArray()
    val durations = listOf(D1,D2,D3,D4).toTypedArray()
    val size = starts.size
    val y = model.intVarArray(size, List(size){0}.toIntArray())
    val height = model.intVarArray(size, List(size){0}.toIntArray())

    // constraints
    model.diffN(starts, y, durations, height, true).post()

    // searching for a feasible solution
    val solver: Solver = model.solver

    if(solver.solve()) {
        println("${S1.value} ${S2.value} ${S3.value} ${S4.value}")
    }

}