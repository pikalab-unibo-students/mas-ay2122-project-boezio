import org.chocosolver.solver.Model
import java.awt.geom.Arc2D

fun main() {

    val model = Model()
    val precision = model.precision

    val x = model.realVar(Double.MIN_VALUE, Double.MAX_VALUE, precision)
    val y = model.realVar(Double.MIN_VALUE, Double.MAX_VALUE, precision)

    // Constraints
    x.gt(y).equation().post()
    x.add(y).eq(10.0).equation().post()

    // solution generation
    val solver = model.solver

    if(solver.solve()) {
        println("$x \n $y")
    }


}