package clpb

import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.TypeError
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

    @Test
    fun testInvalidWeights(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum(a, [A,B,C], Maximum)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidWeightsElement(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([a,1,2], [A,B,C], Maximum)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.INTEGER)
    }

    @Test
    fun testInvalidVs(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([1,2,3], a, Maximum)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidVsElement(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([1,2,3],[A,a,B], Maximum)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testDifferentListSize(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([1,2,3],[A,B], Maximum)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.ATOM_PROPERTY)
    }

    @Test
    fun testInvalidMaximum(){

        val goal = termParser.parseStruct(
            "sat('#'(A,B)), weighted_maximum([1,2,1], [A,B,C], a)"
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }
}