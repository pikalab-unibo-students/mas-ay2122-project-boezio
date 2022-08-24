package clpqr.optimization

import clpqr.BaseTest
import clpqr.ClpQRLibrary
import clpqr.Precision
import clpqr.assertSolutionAssigns
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test

class SupTest: BaseTest() {

    @Test
    fun testSup(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                {}('<='('+'('*'(2,X),Y),16)),
                {}('=<'('+'(X,'*'(2,Y)),11)),
                {}('=<'('+'(X,'*'(3,Y)),15)),
                {}('='(Z,'+'('*'(30,X),'*'(50,Y)))).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),sup(Z,Sup)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = Libraries.of(ClpQRLibrary),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        val z = 309.9999970670992300
        val x = 7.0000000418985895
        val y = 2.0000002932901015

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Z") to realOf(z),
                varOf("Vertex") to listOf(realOf(x),realOf(y))
            )
        }
    }
}