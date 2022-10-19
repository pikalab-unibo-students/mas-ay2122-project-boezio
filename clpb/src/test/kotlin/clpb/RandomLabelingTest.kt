package clpb

import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class RandomLabelingTest: BaseTest() {

    @Test
    fun testRandomLabelingVariable(){

        val goal = termParser.parseStruct(
            "sat(X),random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(0)
            )
        }
    }

    @Test
    fun testRandomLabelingOr(){

        val goal = termParser.parseStruct(
            "sat(X+Y),random_labeling(0,[X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(0)
            )
        }
    }

    @Test
    fun testRandomLabelingOrSecondSolution(){

        val goal = termParser.parseStruct(
            "sat(X+Y),random_labeling(1,[X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(0),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testRandomLabelingOrThirdSolution(){

        val goal = termParser.parseStruct(
            "sat(X+Y),random_labeling(2,[X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1),
                varOf("Y") to intOf(1)
            )
        }
    }

    @Test
    fun testRandomLabelingVarOrBool(){

        val goal = termParser.parseStruct(
            "sat(X+1),random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testRandomLabelingSatFalse(){

        val goal = termParser.parseStruct(
            "sat(X*0),random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testRandomLabelingVarNotPreviouslyDefined(){

        val goal = termParser.parseStruct(
            "sat(X),random_labeling(0,[X,Y])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(0),
                varOf("Y") to intOf(0)
            )
        }
    }

    @Test
    fun testRandomLabelingDoubleSat(){

        val goal = termParser.parseStruct(
            "sat(X*1), sat(X), random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("X") to intOf(1)
            )
        }
    }

    @Test
    fun testRandomLabelingDoubleSatAndTautTrue(){

        val goal = termParser.parseStruct(
            "sat(X =< Y), sat(Y =< Z), taut(X =< Z, T), random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(1),
                varOf("X") to intOf(0),
            )
        }
    }

    @Test
    fun testRandomLabelingDoubleSatAndTautFalse(){

        val goal = termParser.parseStruct(
            "sat(X =< Y), sat(Y =< Z), taut(X > Z, T), random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("T") to intOf(0),
                varOf("X") to intOf(0),
            )
        }
    }

    @Test
    fun testRandomLabelingTaut(){

        val goal = termParser.parseStruct(
            "taut('+'(X,'~'(X)), T), random_labeling(0,[X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testInvalidFirstArgument(){

        val goal = termParser.parseStruct(
            "sat(X), random_labeling(a,[X])"
        )
        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidSecondArgument(){

        val goal = termParser.parseStruct(
            "sat(X), random_labeling(0,a)"
        )
        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidListElement(){

        val goal = termParser.parseStruct(
            "sat(X), random_labeling(0,[a])"
        )
        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }
}