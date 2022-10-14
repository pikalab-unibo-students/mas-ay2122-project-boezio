package clpb

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class LabelingTest: BaseTest() {

    @Test
    fun testLabelingVariable(){

        val goal = termParser.parseStruct(
            "sat(X),labeling([X])"
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
    fun testLabelingOr(){

        val goal = termParser.parseStruct(
            "sat(X+Y),labeling([X,Y])"
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
    fun testLabelingVarOrBool(){

        val goal = termParser.parseStruct(
            "sat(X+1),labeling([X])"
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
    fun testLabelingSatFalse(){

        val goal = termParser.parseStruct(
            "sat(X*0),labeling([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }

    @Test
    fun testLabelingVarNotPreviouslyDefined(){

        val goal = termParser.parseStruct(
            "sat(X),labeling([X,Y])"
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
    fun testLabelingDoubleSat(){

        val goal = termParser.parseStruct(
            "sat(X*1), sat(X), labeling([X])"
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
    fun testLabelingDoubleSatAndTautTrue(){

        val goal = termParser.parseStruct(
            "sat(X =< Y), sat(Y =< Z), taut(X =< Z, T), labeling([X])"
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
    fun testLabelingDoubleSatAndTautFalse(){

        val goal = termParser.parseStruct(
            "sat(X =< Y), sat(Y =< Z), taut(X > Z, T), labeling([X])"
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
    fun testLabelingTaut(){

        val goal = termParser.parseStruct(
            "taut('+'(X,'~'(X)), T), labeling([X])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        assertTrue(solution.isNo)
    }
}