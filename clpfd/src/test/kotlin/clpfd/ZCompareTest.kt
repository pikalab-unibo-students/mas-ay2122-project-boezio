package clpfd

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class ZCompareTest: BaseTest() {

    @Test @Ignore
    fun testGreater() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                ins([X,Y],'..'(1,10)),
                zcompare('>',X,Y).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = ClpFdLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }
}