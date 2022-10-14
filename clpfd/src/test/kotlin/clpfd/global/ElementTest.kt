package clpfd.global

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException

class ElementTest: BaseTest() {

    @Test
    fun testElement() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value, Index) :- 
                in(Value, '..'(2, 2)), 
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

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }

    @Test
    fun testInvalidList() {

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

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }

    @Test
    fun testInvalidValue() {

        val theory = theoryParser.parseTheory(
            """
            problem(Value) :- 
                in(Value, '..'(1, 10)),
                element(1, [5,3,8,a,4], a).
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X),label([X])"
        )

        val solver = getSolver(theory)

        assertThrows<IllegalArgumentException> {
            solver.solveOnce(goal)
        }
    }
}