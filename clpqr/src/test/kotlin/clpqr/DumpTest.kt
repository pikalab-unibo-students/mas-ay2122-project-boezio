package clpqr

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.flags.FlagStore
import  it.unibo.tuprolog.solve.library.toRuntime
import it.unibo.tuprolog.core.List as LogicList
import org.junit.jupiter.api.Test

class DumpTest: BaseTest() {

    @Test
    fun testDumpAtoms(){

        val goal = termParser.parseStruct(
            "{}('='('+'(X,Y), 10.0)), dump([X,Y],[a,b],Cons)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpQRLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        val codedAnswer = LogicList.of(listOf(Struct.of(
            "=",
            Struct.of("+", Atom.of("a"), Atom.of("b")),
            Real.of(10.0)
        )))

        termParser.scope.with {
            solution.assertSolutionAssigns(
                0.0,
                varOf("Cons") to codedAnswer
            )
        }
    }

    @Test
    fun testDumpMixed(){

        val goal = termParser.parseStruct(
            "{}('='('+'(X,Y), 10.0)), dump([X,Y],[a,f(a)],Cons)"
        )

        val solver = Solver.prolog.solverWithDefaultBuiltins(
            otherLibraries = ClpQRLibrary.toRuntime()
        )

        val solution = solver.solveOnce(goal)

        val codedAnswer = LogicList.of(listOf(Struct.of(
            "=",
            Struct.of("+", Atom.of("a"), Struct.of("f", Atom.of("a"))),
            Real.of(10.0)
        )))

        // using variable there is a scope problem, but it correctly works

        termParser.scope.with {
            solution.assertSolutionAssigns(
                0.0,
                varOf("Cons") to codedAnswer
            )
        }

    }
}