package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class SatisfyTest: BaseTest() {

    @Test
    fun testSatisfy(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                {}(','('>'(X,Y),'='('+'(X,Y), 10.0))).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),satisfy([X,Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        assertNotNull(solution)

    }
}