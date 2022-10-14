package clpfd.reflection

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdInfTest: BaseTest() {

    @Test
    fun testFdInfBaseWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_inf(X,Inf)"
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Inf") to intOf(1)
            )
        }
    }

    @Test
    fun testFdInfBaseWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_inf(X,1)"
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdInfWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), '#>'(X,1), fd_inf(X,Inf)"
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Inf") to intOf(2)
            )
        }
    }

    @Test
    fun testFdInfWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), fd_inf(X,2)"
        )

        val solver = get_solver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }
}