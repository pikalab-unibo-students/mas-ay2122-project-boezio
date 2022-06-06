import org.chocosolver.solver.Model
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AppTest {

    @Test
    fun secondExampleTest() {
        val model = Model()

        // definition of variables
        val maxCakes = 100

        val banana = model.intVar("banana", 0, maxCakes)
        val chocolate = model.intVar("chocolate", 0, maxCakes)

        // definition of constraints
        banana.mul(250).add(chocolate.mul(200)).le(4000).decompose().post()
        banana.mul(2).le(6).decompose().post()
        banana.mul(75).add(chocolate.mul(150)).le(2000).decompose().post()
        banana.mul(100).add(chocolate.mul(150)).le(500).decompose().post()
        chocolate.mul(75).le(500)

        // searching for an optimal solution

        model.setObjective(Model.MAXIMIZE, banana.mul(400).add(chocolate.mul(450)).intVar())
        val solver = model.solver

        solver.solve()

        assertEquals(banana.value,2)
        assertEquals(chocolate.value, 2)

    }
}