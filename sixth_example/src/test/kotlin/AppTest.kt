import org.chocosolver.solver.Model
import org.chocosolver.solver.constraints.nary.cnf.LogOp
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AppTest {

    @Test
    fun sixthExampleTest() {
        val model = Model()

        // definition of variables
        val x = model.boolVar("X")
        val y = model.boolVar("Y")

        // constraints
        model.addClauses(LogOp.or(LogOp.nor(x),y))

        // search for a solution
        val solver = model.solver
        solver.solve()

        assertEquals(x.value, 0)
        assertEquals(y.value, 0)

    }
}