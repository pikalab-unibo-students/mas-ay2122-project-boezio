import org.chocosolver.solver.Model

fun main() {

    val model = Model()
    val precision = model.precision

    val x = model.realVar("X", Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val y = model.realVar("Y", Double.MIN_VALUE, Double.MAX_VALUE, precision)

    // Constraints
    x.gt(y).equation().post()
    x.add(y).eq(10.0).equation().post()

    // solution generation
    val solver = model.solver

    if(solver.solve()) {
        println("$x \n$y")
    }


}