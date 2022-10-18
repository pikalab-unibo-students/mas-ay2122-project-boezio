package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test

class ElementTest: BaseTest() {

    @Test
    fun testElement() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value, Index) :- 
                in(Value, 2), 
                in(Index, '..'(1, 10)), 
                element(Index, [5,3,8,2,4], Value).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(3)
            )
        }
    }

    @Test
    fun testElementWithIntIndex() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value) :- 
                in(Value, '..'(2, 2)), 
                element(3, [5,3,8,2,4], Value).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
            )
        }
    }

    @Test
    fun testElementWithIntegerValue() {

        val theory = theoryParser.parseTheory(
            """
            problem(Index) :- 
                in(Index, '..'(1, 10)), 
                element(Index, [5,3,8,2,4], 2).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(3)
            )
        }
    }

    @Test
    fun testInvalidIndex() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value) :- 
                in(Value, '..'(1, 10)),
                element(a, [5,3,8,2,4], Value).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidList() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value) :- 
                in(Value, '..'(1, 10)),
                element(1, a, Value).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidListElement() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value) :- 
                in(Value, '..'(1, 10)),
                element(1, [5,3,8,a,4], Value).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidValue() {

        val theory = theoryParser.parseTheory(
            """
            problem(Index) :- 
                in(Value, '..'(2, 2)), 
                in(Index, '..'(1, 10)), 
                element(Index, [5,3,8,2,4], a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testVsWithVariable() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value, Index) :- 
                in(Var, 5),
                in(Value, 2), 
                in(Index, '..'(1, 10)), 
                element(Index, [Var,3,8,2,4], Value).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<ExistenceError>(solution, ExistenceError.ObjectType.RESOURCE)
    }
}