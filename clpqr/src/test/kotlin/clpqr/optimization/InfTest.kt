package clpqr.optimization

import clpqr.*
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import  it.unibo.tuprolog.solve.library.toRuntime
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class InfTest: BaseTest() {

    @Test
    fun testInfVariable(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                { 2*X+Y >= 16, X+2*Y >= 11,
                  X+3*Y >= 15, Z = 30*X+50*Y
                }.
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),inf(Z,Inf)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpQRLibrary.toRuntime(),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        val inf = "338.00128254557313"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Inf") to realOf(inf)
            )
        }
    }

    @Test
    fun testInfExpression(){

        val theory = theoryParser.parseTheory(
            """
            problem(X, Y, Z) :- 
                { 2*X+Y =< 16, X+2*Y =< 11,
                  X+3*Y =< 15
                }.
            """.trimIndent()
        )

        val goal = termParser.parseStruct(
            "problem(X,Y,Z),sup(30*X+50*Y,Sup)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpQRLibrary.toRuntime(),
            flags = FlagStore.DEFAULT + (Precision to Real.of(precision)),
            staticKb = theory
        )

        val solution = solver.solveOnce(goal)

        val sup = "309.9924428241242000"

        termParser.scope.with {
            solution.assertSolutionAssigns(
                precision,
                varOf("Sup") to realOf(sup)
            )
        }
    }
}