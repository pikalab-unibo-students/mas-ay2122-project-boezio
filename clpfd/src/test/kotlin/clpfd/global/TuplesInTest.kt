package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
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

        val solver = getSolver(theory)

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

        val solver = getSolver(theory)

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

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(2)
            )
        }
    }

    @Test
    fun testTuplesInvalidFirstArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples(a, [[1,2],[1,5],[4,0],[4,3]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidSecondArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples_in([[X,Y]], a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidTuples() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z, W) :- 
                ins([X,Y,Z,W], '..'(1,10)), 
                tuples_in([[a,Y],[Z,W]], [[1,2],[1,5],[4,0],[4,3]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z,W),label([X,Y,Z,W])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidRelation() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples_in([[X,Y]], [a]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidTupleLength() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples_in([[X,Y]], [[1,2,3]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.ATOM_PROPERTY)
    }

    @Test
    fun testTupleWithVar() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples_in([[X,Y]], [[X,2]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<ExistenceError>(solution, ExistenceError.ObjectType.RESOURCE)
    }

    @Test
    fun testInvalidTupleElem() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, 4), 
                in(Y, '..'(1, 10)), 
                tuples_in([[X,Y]], [[a,2]]).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }
}