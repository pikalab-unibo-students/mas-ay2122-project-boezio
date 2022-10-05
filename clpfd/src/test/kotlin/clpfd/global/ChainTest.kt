package clpfd.global

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class ChainTest: BaseTest() {

    @Test @Ignore
    fun testChain() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 10)), 
                chain([X,Y,Z], '#>').
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpFdLibrary.toRuntime(),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3),
                varOf("Y") to intOf(2),
                varOf("Z") to intOf(1)
            )
        }
    }

    @Test @Ignore
    fun testChainGoal() {

        val goal = termParser.parseStruct(
            "ins([X,Y,Z], '..'(1, 10)),chain([X,Y,Z], '#>'),label([X,Y,Z])"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpFdLibrary.toRuntime(),
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3),
                varOf("Y") to intOf(2),
                varOf("Z") to intOf(1)
            )
        }
    }
}