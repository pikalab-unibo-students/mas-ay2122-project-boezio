package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EntailedTest: BaseTest() {

    @Test @Ignore
    fun testEntailedTrue(){

        val goal = termParser.parseStruct(
            "{}(','('>'(X,Y),'='('+'(X,Y), 10.0))), entailed('<'(Y,X)), satisfy([X,Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision))
        )

        // failure because the constraint to check returns undefined

        val solution = solver.solveOnce(goal)
        val yesSolution = solution is Solution.Yes

        assertTrue(yesSolution)

    }


    // Can undefined correctly considered as false?
    @Test
    fun testEntailedFalse(){

        val goal = termParser.parseStruct(
            "{}(','('>'(X,Y),'='('+'(X,Y), 10.0))), entailed('>'(Y,X)), satisfy([X,Y])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision))
        )

        val solution = solver.solveOnce(goal)
        val noSolution = solution is Solution.No

        assertTrue(noSolution)

    }
}