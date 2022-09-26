import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp

fun main(){

    val model = Model()

    val x = model.boolVar("X")
    val y = model.boolVar("Y")

    model.addClauses(LogOp.and(x,y))

    val solver = model.solver

    while(solver.solve()) {
        print("${x.booleanValue}\n${y.booleanValue}")
    }
}