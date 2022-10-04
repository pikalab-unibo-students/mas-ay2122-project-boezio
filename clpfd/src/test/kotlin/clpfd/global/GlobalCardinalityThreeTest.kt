package clpfd.global

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import it.unibo.tuprolog.solve.Solver
import  it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalStateException

class GlobalCardinalityThreeTest: BaseTest() {

    @Test
    fun testGlobalCardinality() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 3)), 
                in(W, '..'(2, 2)),
                in(V, '..'(1, 1)),
                global_cardinality([X,Y,Z],['-'(1,W), '-'(2,V)], [consistency(3)]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = ClpFdLibrary.toRuntime()
        )

        assertThrows<IllegalStateException> {
            solver.solveOnce(goal)
        }
    }
}