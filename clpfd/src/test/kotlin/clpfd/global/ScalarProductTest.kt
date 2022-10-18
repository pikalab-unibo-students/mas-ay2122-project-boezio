package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test

class ScalarProductTest: BaseTest() {

    @Test
    fun testScalarProductInteger() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([1, 2], [X,Y], #=, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(5)
            )
        }
    }

    @Test
    fun testScalarProductVariable() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)),
                in(Z, '..'(1,20)),
                scalar_product([1, 2], [X,Y], #=, Z).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
            varOf("Z") to intOf(3)
            )
        }
    }

    @Test
    fun testScalarProductExpression() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)),
                in(Z, '..'(1,20)),
                scalar_product([1, 2], [X,Y], #=, Z + 1).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),label([X,Y,Z])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1),
                varOf("Z") to intOf(2)
            )
        }
    }

    @Test
    fun testScalarProductMixedVs() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 10)),
                scalar_product([1,2], [X,5], #=, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testInvalidFirstArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 10)),
                scalar_product(a, [X,5], #=, 11).
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
    fun testInvalidSecondArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X) :- 
                in(X, '..'(1, 10)),
                scalar_product([1,2], a, #=, 11).
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
    fun testInvalidThirdArgument() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([1, 2], [X,Y], 1, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.ATOM)
    }

    @Test
    fun testVariableAsCoefficient() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([X, 2], [X,Y], #=, 11).
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
    fun testInvalidCoefficient() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([a, 2], [X,Y], #=, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidVariables() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([1, 2], [a,Y], #=, 11).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidOperator() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([1, 2], [X,Y], a, 11).
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
    fun testInvalidExpression() {

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                scalar_product([1, 2], [X,Y], #=, a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y),label([X,Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.EVALUABLE)
    }

}