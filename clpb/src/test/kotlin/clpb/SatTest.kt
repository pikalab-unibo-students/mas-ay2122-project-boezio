package clpb

import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class SatTest: BaseTest() {

    @Test
    fun testSatAndTrue(){

        val goal = termParser.parseStruct(
            "sat(X * Y)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)

    }

    @Test
    fun testSatAndFalse(){

        val goal = termParser.parseStruct(
            "sat(*(X,~(X)))"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatTrueValue(){

        val goal = termParser.parseStruct(
            "sat(X + 1)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatFalseValue(){

        val goal = termParser.parseStruct(
            "sat(X + 0)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatFalseValueFalseOutcome(){

        val goal = termParser.parseStruct(
            "sat(X * 0)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatLessThanTrue(){

        val goal = termParser.parseStruct(
            "sat(X < Y)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatLessThanFalse(){

        val goal = termParser.parseStruct(
            "sat(1 < Y)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testSatGreaterThanTrue(){

        val goal = termParser.parseStruct(
            "sat(X > Y)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isYes)
    }

    @Test
    fun testSatGreaterThanFalse(){

        val goal = termParser.parseStruct(
            "sat(0 > Y)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpBLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }
}