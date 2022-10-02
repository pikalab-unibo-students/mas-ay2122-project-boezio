import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp

fun main(){

    val model = Model()

    val x = model.boolVar("X")

    model.addClauses(LogOp.and(x,LogOp.nand(x)))

    val solver = model.solver

    while(solver.solve()) {
        println("${x.booleanValue}")
    }
}