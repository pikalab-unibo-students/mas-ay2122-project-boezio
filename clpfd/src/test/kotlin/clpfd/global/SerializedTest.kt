package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test

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

        val solver = getSolver(theory)

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

        val solver = getSolver(theory)

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
    fun testInvalidFirstArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S2,S3,S4) :-
                ins([S1,S2,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized(a,[D1,D2,D3,D4]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S2,S3,S4),label([S1,S2,S3,S4])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidSecondArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(S1,S2,S3,S4) :-
                ins([S1,S2,S3,S4],'..'(1,100)),
                ins([D1,D2,D3,D4], 5),
                serialized([S1,S2,S3,S4],a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(S1,S2,S3,S4),label([S1,S2,S3,S4])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
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

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.PREDICATE_PROPERTY)
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

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
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

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }


}