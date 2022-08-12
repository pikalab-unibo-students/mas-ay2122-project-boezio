package clpfd.globalConstraints

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class SerializedTest: BaseTest() {

    @Test
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

    @Test
    fun testSerializedIntegerAndVars() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S3,S4) :-
                ins([S1,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized([S1,6,S3,S4],[D1,D2,D3,D4]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S3,S4),label([S1,S3,S4])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("S1") to intOf(1),
                varOf("S3") to intOf(11),
                varOf("S4") to intOf(16)
            )
        }
    }

    @Test
    fun testDifferentListLengths() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S3) :-
                ins([S1,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized([S1,6,S3],[D1,D2,D3,D4]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S3),label([S1,S3])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }

    @Test
    fun testInvalidStarts() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S2,S3,S4) :-
                ins([S1,S2,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized([a,S2,S3,S4],[D1,D2,D3,D4]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S2,S3,S4),label([S1,S2,S3,S4])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }

    @Test
    fun testInvalidDurations() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S2,S3,S4) :-
                ins([S1,S2,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized([S1,S2,S3,S4],[a,D2,D3,D4]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S2,S3,S4),label([S1,S2,S3,S4])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }


}