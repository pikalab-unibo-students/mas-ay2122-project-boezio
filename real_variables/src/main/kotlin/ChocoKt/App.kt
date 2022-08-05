import org.chocosolver.solver.Model

fun main() {

    val model = Model()

    val x = model.realVar(4.0)
    val y = model.realVar(0.2,10.0,0.001)

    // Constraints
    y.lt(5.0).equation().post()
    x.add(y).eq(5.0).equation().post()

    // solution generation
    val solver = model.solver

    if(solver.solve()) {
        println("$x \n $y")
    }


}