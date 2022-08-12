package clpfd.globalConstraints

import clpfd.BaseTest
import clpfd.ClpFdLibrary
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class LexChainTest: BaseTest() {

    @Test
    fun testLexChain() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z,W) :- 
                ins([X,Y,Z,W], '..'(1, 10)), 
                lex_chain([[X,Y],[Z,W]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(1),
                varOf("W") to intOf(1)
            )
        }
    }

    @Test
    fun testLexChainWithInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y,Z) :- 
                ins([X,Y,Z], '..'(1, 10)), 
                lex_chain([[X,Y],[Z,1]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(1)
            )
        }
    }

    @Test
    fun testLexChainNWithInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y) :- 
                ins([X,Y], '..'(1, 10)), 
                lex_chain([[2],[X],[Y]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(2)
            )
        }
    }

    @Test
    fun testLexChainInvalidArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X,Y) :- 
                ins([X,Y], '..'(1, 10)), 
                lex_chain([[a],[X],[Y]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
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