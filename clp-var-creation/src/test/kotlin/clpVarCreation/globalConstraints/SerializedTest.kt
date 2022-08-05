package clpVarCreation.globalConstraints

import clpVarCreation.BaseTest
import clpVarCreation.ClpFdLibrary
import clpVarCreation.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class SerializedTest: BaseTest() {

    @Test @Ignore
    fun testSerialized() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S2,S3,S4) :-
                ins([S1,S2,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized([S1,S2,S3,S4],[D1,D2,D3,D4]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S2,S3,S4),label([S1,S2,S3,S4])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("S1") to intOf(1),
                varOf("S2") to intOf(6),
                varOf("S3") to intOf(11),
                varOf("S4") to intOf(16)
            )
        }
    }




}