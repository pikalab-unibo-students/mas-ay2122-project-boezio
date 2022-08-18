import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val precision = model.precision

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val y = model.realVar("Y", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val z = model.realVar("Z", 4.0)

    // Constraints
    x.gt(y).equation().post()
    y.gt(1.0).equation().post()

    // solution generation
    val solver = model.solver

    if(solver.solve()) {
        println("$x\n$y\n$z")
    }


}