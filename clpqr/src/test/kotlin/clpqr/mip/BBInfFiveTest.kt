package clpqr.mip

import clpqr.*
import it.unibo.tuprolog.solve.exception.error.DomainError
import it.unibo.tuprolog.solve.exception.error.ExistenceError
import it.unibo.tuprolog.solve.exception.error.TypeError
import org.junit.jupiter.api.Test

class BBInfFiveTest: BaseTest() {

    @Test
    fun testBBInfFiveVariable(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([Y,Z],X,Inf,Vertex,0.0)."
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val xExpected = "2.0"
        val vertexExpected = termParser.parseStruct(
            "[1.0, 1.0]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(xExpected),
                varOf("Vertex") to vertexExpected
            )
        }
    }

    @Test
    fun testBBInfFiveExpression(){

        val goal = termParser.parseStruct(
            "{2*X+Y >= 16, X+2*Y >= 11,X+3*Y >= 15}, bb_inf([X,Y], 30*X+50*Y, Inf, Vertex, 0.0)."
        )

        val solver = getSolver()

        val solution = solver.solveOnce(goal)

        val exprExpected = "360.0"
        val vertexExpected = termParser.parseStruct(
            "[7.0, 3.0]"
        )

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(exprExpected),
                varOf("Vertex") to vertexExpected
            )
        }
    }

    @Test
    fun testInvalidFirstArgument(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf(a,X,Inf,Vertex,0.0)."
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.LIST)
    }

    @Test
    fun testInvalidFirstArgumentElement(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([a,Y],X,Inf,Vertex,0.0)."
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testInvalidThirdArgument(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([X,Y],X,a,Vertex,0.0)."
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testInvalidFourthArgument(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([X,Y],X,Inf,a,0.0)."
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<TypeError>(solution, TypeError.Expected.VARIABLE)
    }

    @Test
    fun testInvalidFifthArgument(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([X,Y],X,Inf,Vertex,5.0)."
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<DomainError>(solution, DomainError.Expected.ATOM_PROPERTY)
    }

    @Test
    fun testFifthArgumentNotYetSupported(){

        val goal = termParser.parseStruct(
            "{X >= Y+Z, Y >= 1, Z >= 1}, bb_inf([X,Y],X,Inf,Vertex,0.3)."
        )

        val solver = getSolver()
        val solution = solver.solveOnce(goal)
        assertException<ExistenceError>(solution, ExistenceError.ObjectType.RESOURCE)
    }

}