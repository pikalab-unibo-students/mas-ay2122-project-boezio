import org.chocosolver.solver.Model
import kotlin.test.Test
import kotlin.test.assertEquals

internal class AppTest {

    @Test
    fun fifthExampleTest() {
        val model = Model()

        // definition of the variables

        val numTasks = 3
        val durations = intArrayOf(60,45,30)
        val capacities = intArrayOf(3,2,2)
        val maxCapacity = 4
        val sum = durations.sum()
        val starts = model.intVarArray("starts", numTasks, 1, sum)

        // definition of constraints

        model.cumulative(starts, durations, capacities, maxCapacity)

        // search for a solution

        val solver = model.solver
        solver.solve()

        assertEquals(starts[0].value, 1)
        assertEquals(starts[1].value, 61)
        assertEquals(starts[2].value, 61)

    }
}