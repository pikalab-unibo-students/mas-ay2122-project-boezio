package clpb

import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class WeightedMaximumTest: BaseTest() {

    @Test
    fun testWeightedMaximumExample(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([1,2,1], [A,B,C], Maximum)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("A") to intOf(0),
                varOf("B") to intOf(1),
                varOf("C") to intOf(1),
                varOf("Maximum") to intOf(3),
            )
        }
    }

    @Test
    fun testWeightedMaximumWithoutSat(){

        val goal = termParser.parseStruct(
            "weighted_maximum([1,2,1], [A,B,C], Maximum)"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("A") to intOf(1),
                varOf("B") to intOf(1),
                varOf("C") to intOf(1),
                varOf("Maximum") to intOf(4),
            )
        }
    }

    @Test @Ignore
    fun testWeightedMaximumWithLabeling(){

        val goal = termParser.parseStruct(
            "weighted_maximum([1,2,1], [A,B,C], Maximum), labeling([A,B,C])"
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        termParser.scope.with {
            solution.assertSolutionAssigns(
                varOf("A") to intOf(1),
                varOf("B") to intOf(1),
                varOf("C") to intOf(1),
                varOf("Maximum") to intOf(4),
            )
        }
    }
}