package clpqr

import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EntailedTest: BaseTest() {

    @Test
    fun testEntailedTrue(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                {}(','('>'(X,Y),'='('+'(X,Y), 10.0))),
                entailed('<'(Y,X)).
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
        val yesSolution = solution is Solution.Yes

        assertTrue(yesSolution)

    }

    @Test
    fun testEntailedFalse(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                {}(','('>'(X,Y),'='('+'(X,Y), 10.0))),
                entailed('>'(Y,X)).
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