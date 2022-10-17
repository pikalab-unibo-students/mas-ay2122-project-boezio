package clpfd

import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import it.unibo.tuprolog.theory.parsing.ClausesParser
import org.junit.jupiter.api.Test


internal class LabelingTest: BaseTest() {

    private val theory = ClausesParser.withDefaultOperators().parseTheory(
        """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
    )

    private val parser = TermParser.withDefaultOperators()

    @Test
    fun testLeftMost() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([leftmost],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testFirstFail() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([ff],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testDomOverWDeg() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([ffc],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testMin() {
        val goal = parser.parseStruct(
            "problem(X,Y),labeling([min],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testMax() {
        val goal = parser.parseStruct(
            "problem(X,Y),labeling([max],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    // ValueOrder tests

    @Test
    fun testUp() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([up],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testDown() {

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([down],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(10),
                varOf("Y") to intOf(9)
            )
        }
    }

    @Test
    fun testMinimize() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X,Y),labeling([min(X)],[X,Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(2),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testMaximize() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling([max('-'(X, Y))],[X, Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(10),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testMaximizeWithStep() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling([max('-'(X, Y)), step],[X, Y])"
        )

        val solver = getSolver(theory)

        val solution = solver.solveOnce(goal)

        parser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(10),
                varOf("Y") to intOf(1)
            )
        }
    }


    @Test
    fun testLabelingInvalidFirstArgument() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling(a,[X, Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testLabelingInvalidSecondArgument() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling([],a)"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testNotImplementedBisect() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling([max('-'(X, Y)), bisect],[X, Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<ExistenceError>(solution, ExistenceError.ObjectType.RESOURCE)
    }

    @Test
    fun testNotImplementedEnum() {

        val theory = ClausesParser.withDefaultOperators().parseTheory(
            """
            problem(X, Y) :- 
                in(X, '..'(1, 10)), 
                in(Y, '..'(1, 10)), 
                '#>'(X, Y).
            """.trimIndent()
        )

        val parser = TermParser.withDefaultOperators()

        val goal = parser.parseStruct(
            "problem(X, Y), labeling([max('-'(X, Y)), enum],[X, Y])"
        )

        val solver = getSolver(theory)
        val solution = solver.solveOnce(goal)
        assertException<ExistenceError>(solution, ExistenceError.ObjectType.RESOURCE)
    }
}