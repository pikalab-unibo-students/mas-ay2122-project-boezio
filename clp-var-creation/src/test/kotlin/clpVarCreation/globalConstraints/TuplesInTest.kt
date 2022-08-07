package clpVarCreation.globalConstraints

import clpVarCreation.BaseTest
import clpVarCreation.ClpFdLibrary
import clpVarCreation.assertSolutionAssigns
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test

class TuplesInTest: BaseTest() {

    @Test
    fun testTuplesInTupleVariables() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples_in([[X,Y]], [[1,2],[1,5],[4,0],[4,3]]).
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
                varOf("X") to intOf(4),
                varOf("Y") to intOf(3)
            )
        }
    }

    @Test
    fun testTuplesInTupleMixed() {

        val theory = theoryParser.parseTheory(
            """
            problem(Y) :- 
                in(Y, '..'(1, 10)), 
                tuples_in([[4,Y]], [[1,2],[1,5],[4,0],[4,3]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(Y),label([Y])"
        )

        val solver = Solver.prolog.solverOf(
            staticKb = theory,
            libraries = Libraries.of(ClpFdLibrary)
        )

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Y") to intOf(3)
            )
        }
    }

    @Test
    fun testTuplesInN() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z, W) :- 
                ins([X,Y,Z,W], '..'(1,10)), 
                tuples_in([[X,Y],[Z,W]], [[1,2],[1,5],[4,0],[4,3]]).
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
                varOf("Y") to intOf(2)
            )
        }
    }
}