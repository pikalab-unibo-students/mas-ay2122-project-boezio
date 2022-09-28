import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp

fun main(){

    val model = Model()

    val x = model.boolVar("X")
    val y = model.boolVar(true)

    model.addClauses(LogOp.or(x,y))

    val solver = model.solver

    if(solver.solve()) {
        print("${x.booleanValue}")
    }
}