package clpfd.reflection

import clpfd.BaseTest
import clpfd.assertSolutionAssigns
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class FdDegreeTest: BaseTest() {

    @Test
    fun testFdDegreeZeroWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(X,Degree)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(0)
            )
        }
    }

    @Test
    fun testFdDegreeZeroWithInt() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(X,0)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testFdDegreeOneWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), fd_degree(X,Degree)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(1)
            )
        }
    }

    @Test
    fun testFdDegreeTwoWithVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_degree(X,Degree)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(2)
            )
        }
    }

    @Test
    fun testFdDegreeTwoWithVarFalseOutcome() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_degree(X,3)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdDegreeInvalidVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), fd_degree(Y,3)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testFdDegreeNotIntVar() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), #>(X,1), #<(X,10), { Y > 3 }, fd_degree(Y,3)"
        )

        val solver = getFdQRSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testInvalidFirstArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(a,Degree)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testInvalidSecondArgument() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(X,a)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testNewVarDegreeVariable() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(Y,Degree)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("Degree") to intOf(0)
            )
        }
    }

    @Test
    fun testNewVarDegreeValueNotZero() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(Y,1)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertTrue(solution.isNo)
    }

    @Test
    fun testNewVarDegreeValueZero() {

        val goal = termParser.parseStruct(
            "in(X,'..'(1,10)), fd_degree(Y,0)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertTrue(solution.isYes)
    }

}